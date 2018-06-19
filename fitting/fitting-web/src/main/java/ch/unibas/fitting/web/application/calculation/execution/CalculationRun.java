package ch.unibas.fitting.web.application.calculation.execution;

import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import akka.actor.Props;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.application.calculation.manager.StartDefinition;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.vavr.control.Option;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.runAsync;

public class CalculationRun extends AbstractActor {
    private static final long STATUS_POLLING_INTERVAL_MS = 5000;

    private final CalculationService service;
    private final StartDefinition definition;

    private String calculationId;
    private RunState status = RunState.Created;
    private String message = "-";
    private Cancellable triggerUpdate;

    public CalculationRun(StartDefinition definition,
                          CalculationService calculationService) {
        this.definition = definition;
        this.service = calculationService;
    }

    public static Props props(StartDefinition definition, CalculationService calculationService){
        return Props.create(CalculationRun.class,() -> new CalculationRun(definition, calculationService));
    }

    @Override
    public void preStart() {
        self().tell(new Initialize(), self());
    }

    @Override
    public void aroundPostStop() {
        if (triggerUpdate != null)
            triggerUpdate.cancel();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, this::handle)
                .match(SetupDone.class, this::handle)
                .match(SetupFailed.class, this::handle)
                .match(TriggerStatusUpdate.class, this::handle)
                .match(StatusUpdated.class, this::handle)
                .match(StatusUpdateFailed.class, this::handle)
                .match(FinishDone.class, this::handle)
                .match(FinishFailed.class, this::handle)

                .match(RunProtocol.GetDetails.class, this::handle)
                .match(RunProtocol.Cleanup.class, this::handle)
                .match(RunProtocol.Cancel.class, this::handle)
                .build();
    }

    // region Message handlers

    private void handle(RunProtocol.GetDetails m) {
        var response = new RunProtocol.GetDetailsResponse(createDetails());
        sender().tell(response, self());
    }

    private void handle(RunProtocol.Cancel m) {
        if (status == RunState.Running) {
            runAsync(() -> cancelRun(calculationId));
        }
    }

    private void handle(RunProtocol.Cleanup m) {
        if (definition.doNotDeleteCalculation && status == RunState.Succeeded) return;
        runAsync(() -> cleanupCalculation(calculationId));
    }

    private void handle(Initialize m) {
        status = RunState.Initializing;
        message = "...";
        runAsync(this::setupRun);
    }

    private void handle(SetupFailed m) {
        status = RunState.Failed;
        message = "Calculation submission failed:\n" + m.cause;
        notifyParent();
    }

    private void handle(SetupDone m) {
        status = RunState.Running;
        message = "Calculation submitted to service";
        self().tell(new TriggerStatusUpdate(), self());
    }

    private void handle(TriggerStatusUpdate m) {
        runAsync(this::updateStatus);
    }

    private void handle(StatusUpdateFailed m) {
        status = RunState.Failed;
        message = "Communication to service failed:\n" + m.cause;
        notifyParent();
    }

    private void handle(StatusUpdated m) {
        if (m.isRunning) {
            message = "Service status:\n" + m.message;
            triggerUpdate = context()
                    .system()
                    .scheduler()
                    .scheduleOnce(Duration.create(STATUS_POLLING_INTERVAL_MS, TimeUnit.MILLISECONDS),
                            self(),
                            new TriggerStatusUpdate(),
                            context().dispatcher(),
                            self());
        } else {
            runAsync(() -> finishRun(m));
        }
    }

    private void handle(FinishFailed m) {
        status = RunState.Failed;
        message = "FinishFailed:\n" + m.cause;
        notifyParent();
    }

    private void handle(FinishDone m) {
        if (m.lastUpdate.succeeded)
            status = RunState.Succeeded;
        else if (m.lastUpdate.isCanceled)
            status = RunState.Canceled;
        else if (m.lastUpdate.isFailed)
            status = RunState.Failed;
        else
            status = RunState.Unknown;

        message = "Last service status:\n" + m.lastUpdate.message;
        notifyParent();
    }

    // endregion

    // region Helper methods

    private void setupRun(){
        try {
            context().system().log().debug("setupRun");

            if (definition.calculationId.isEmpty())
                calculationId = service.createCalculation();
            else
                calculationId = definition.calculationId.get();

            for(var file : definition.inputFiles) {
                service.uploadInputFile(calculationId, file);
            }

            service.startRun(calculationId, definition.algorithmName, definition.parameters);

            context().system().log().debug("setupRun DONE");
            self().tell(new SetupDone(), self());
        } catch (Exception e) {
            context().system().log().error(e, "setupRun FAILED");
            self().tell(new SetupFailed(e), self());
        }
    }

    private void cancelRun(String calcId) {
        try {
            context().system().log().debug("cancelRun");
            service.cancelCalculation(calcId);
            context().system().log().debug("cancelRun DONE");
        } catch (Exception e) {
            context().system().log().error(e, "cancelRun FAILED");
        }
    }

    private void cleanupCalculation(String calculationId) {
        try {
            context().system().log().debug("cleanupCalculation");
            service.deleteCalculation(calculationId);
            context().system().log().debug("cleanupCalculation DONE");
        } catch (Exception e) {
            context().system().log().error(e, "cleanupCalculation FAILED");
        }
    }

    private void updateStatus() {
        try {
            context().system().log().debug("updateStatus");

            var status = service.getCalculationStatus(calculationId);

            boolean isRunning = "Running".equalsIgnoreCase(status.getStatus());
            boolean isSucceeded = "Finished".equalsIgnoreCase(status.getStatus());
            boolean isFailed = "Failed".equalsIgnoreCase(status.getStatus());
            boolean isCanceled = "Canceled".equalsIgnoreCase(status.getStatus());

            context().system().log().debug("updateStatus DONE");

            self().tell(new StatusUpdated(
                    isRunning,
                    isSucceeded,
                    isFailed,
                    isCanceled,
                    status.getStatus(),
                    status.getMessage()), self());
        } catch (Exception e) {
            context().system().log().error(e, "updateStatus FAILED");
            self().tell(new StatusUpdateFailed(e), self());
        }
    }

    private void finishRun(StatusUpdated lastUpdate) {
        try {
            context().system().log().debug("finishRun");

            var outputFiles = service.listOutputFiles(calculationId);

            for (var file : outputFiles) {
                var tempFile = service.downloadOutputFiles(calculationId, file);
                var targetFile = new File(definition.outputDir, file);
                targetFile.getParentFile().mkdirs();

                Files.move(tempFile.toPath(), targetFile.toPath());
            }

            if (lastUpdate.succeeded) {
                var resultJson = Option.<JsonObject>none();
                var resultFile = new File(definition.outputDir, "run_results.json");
                if (resultFile.exists()) {
                    resultJson = getJsonObject(resultFile);
                }

                final Option<JsonObject> finalResultJson = resultJson;
                definition.successCallback.forEach(action -> action.execute(finalResultJson));
            }

            context().system().log().debug("finishRun DONE");
            self().tell(new FinishDone(lastUpdate), self());
        } catch (Exception e) {
            context().system().log().error(e, "finishRun FAILED");
            self().tell(new FinishFailed(e), self());
        }
    }

    private void notifyParent() {
        var response = new RunProtocol.StatusChanged(createDetails());
        context().parent().tell(response, self());
    }

    private RunDetails createDetails() {
        var groupId = context().parent().path().name();
        return new RunDetails(groupId,
                self().path().name(),
                calculationId,
                status,
                message,
                self());
    }

    private Option<JsonObject> getJsonObject(File file) {
        try {
            try (var fr = new FileReader(file)) {
                try (var jr = new JsonReader(fr)) {
                    var parser = new JsonParser();
                    var obj = parser.parse(jr);
                    return Option.of(obj.getAsJsonObject());
                }
            }
        } catch (Exception e) {
            context().system().log().error(e, String.format("deserialize of result JSON [%s] failed.", file));
            return Option.none();
        }
    }

    //endregion

    private static class Initialize {}
    private static class SetupDone {}
    private static class SetupFailed {
        public final String cause;

        private SetupFailed(Exception e) {
            cause = e.getMessage();
        }
    }

    private static class TriggerStatusUpdate {}
    private static class StatusUpdated {
        public final boolean isRunning;
        public final boolean isFailed;
        public final boolean isCanceled;
        public final String status;
        public final String message;
        public boolean succeeded;

        public StatusUpdated(boolean isRunning,
                             boolean succeeded,
                             boolean isFailed,
                             boolean isCanceled,
                             String status,
                             String message) {

            this.isRunning = isRunning;
            this.succeeded = succeeded;
            this.isFailed = isFailed;
            this.isCanceled = isCanceled;
            this.status = status;
            this.message = message;
        }
    }
    private static class StatusUpdateFailed {
        public final String cause;

        public StatusUpdateFailed(Exception cause) {
            this.cause = cause.getMessage();
        }
    }

    private static class FinishDone {
        public final StatusUpdated lastUpdate;

        public FinishDone(StatusUpdated lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
    }
    private static class FinishFailed {
        public final String cause;

        public FinishFailed(Exception cause) {
            this.cause = cause.getMessage();
        }
    }
}

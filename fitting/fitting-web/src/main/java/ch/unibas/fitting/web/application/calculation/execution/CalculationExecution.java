package ch.unibas.fitting.web.application.calculation.execution;

import akka.actor.AbstractActor;
import akka.actor.Props;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.application.calculation.execution.messages.*;
import io.swagger.client.model.Status;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.runAsync;

public class CalculationExecution extends AbstractActor {
    private static final long statusUpdatePollingIntervalInMs = 5000;
    private final CalculationService service;
    private final StartDefinition definition;

    private String calculationId;
    private Status status;

    private final static String INITIALIZING = "Initializing";
    private final static String FINISHED = "Finished";
    private final static String CANCELED = "Canceled";
    private final static String FAILED = "Failed";
    private final static String DOWNLOADING = "Downloading";

    public CalculationExecution(StartDefinition definition,
                                CalculationService calculationService) {
        this.definition = definition;
        this.service = calculationService;
    }

    public static Props props(StartDefinition definition, CalculationService calculationService){
        return Props.create(CalculationExecution.class,() -> new CalculationExecution(definition, calculationService));
    }

    @Override
    public void preStart() {
        self().tell(INITIALIZING, self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DeleteCalculation.class, this::handleDeleteCalculation)
                .match(String.class, this::handleSetUp)
                .match(UpdateStatus.class, this::handleStatusUpdate)
                .match(ListExecutions.class, this::handleListExecutions)
                .match(CancelExecution.class, this::handleCancelExecution)
                .match(DownloadResults.class, this::handleStartDownload)
                .build();
    }

    // region Message handlers

    private void handleSetUp(String status) {
        if(status.equals(INITIALIZING)){
            runAsync(this::setupExecution);
        }
    }

    private void handleStatusUpdate(UpdateStatus msg) {
        if(calculationId != null) {
            status = service.getCalculationStatus(calculationId);
            if (terminated(status.getStatus())){
                self().tell(new DownloadResults(), self());
                status.setStatus(DOWNLOADING);
                return;
            }
        }
        context()
            .system()
            .scheduler()
            .scheduleOnce(Duration.create(msg.pollingIntervalInMs, TimeUnit.MILLISECONDS),
                self(),
                new UpdateStatus(statusUpdatePollingIntervalInMs),
                context().dispatcher(),
                self());
    }

    private void handleDeleteCalculation(DeleteCalculation msg) {
        service.deleteCalculation(calculationId);
        sender().tell(new DeleteCalculationResponse(), self());
    }

    private void handleStartDownload(DownloadResults msg) {
        runAsync(()-> service.listOutputFiles(calculationId)
                .forEach(relativePath->{
                    var tempFile = service.downloadOutputFiles(calculationId, relativePath);
                    var targetFile = new File(definition.outputDir, calculationId+File.separator+relativePath);
                    if(!targetFile.getParentFile().exists())
                        targetFile.getParentFile().mkdirs();
                    try {
                        Files.copy(tempFile.toPath(),targetFile.toPath());

                    } catch (IOException e) {
                        throw new RuntimeException("Failed to download file",e);
                    }
                })).thenRun(()->status.setStatus(FINISHED));
    }
    private void handleListExecutions(ListExecutions msg) {
        var taskId = context().parent().path().name();
        sender().tell(new ExecutionProgress(taskId, calculationId, status, self()), self());
    }
    private void handleCancelExecution(CancelExecution msg) {
        service.cancelCalculation(calculationId);
        sender().tell(new CancelExecutionResponse(), self());
    }

    // endregion

    // region Helper methods

    private void setupExecution(){
        try {
            context().system().log().info("setupExecution");

            if (definition.calculationId.isEmpty())
                calculationId = service.createCalculation();
            else
                calculationId = definition.calculationId.get();

            for(var file : definition.inputFiles) {
                service.uploadInputFile(calculationId, file);
            }

            service.startRun(calculationId, definition.algorithmName, definition.parameters);

            context().system().log().info("setupExecution DONE");
        } catch (Exception e) {
            context().system().log().error(e, "setupExecution failed");
        }
    }

    private void startStatusUpdater(String calcId){
        self().tell(new UpdateStatus(statusUpdatePollingIntervalInMs), self());
    }

    private boolean terminated(String status){
        return status.equals(FINISHED) || status.equals(CANCELED) || status.equals(FAILED) || status.equals(DOWNLOADING);
    }

    private void notifyParent() {
        context().parent().tell(new ExecutionChanged(), self());
    }

    //endregion

    public static class ExecutionChanged {}
}

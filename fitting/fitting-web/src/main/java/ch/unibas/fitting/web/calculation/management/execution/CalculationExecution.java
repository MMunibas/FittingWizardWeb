package ch.unibas.fitting.web.calculation.management.execution;

import akka.actor.AbstractActor;
import akka.actor.Props;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.management.execution.messages.*;
import io.swagger.client.model.Status;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class CalculationExecution extends AbstractActor {
    private static final long statusUpdatePollingIntervalInMs = 5000;
    private CalculationService service;
    private StartDefinition params;

    private final static String INITIALIZING = "Initializing";
    private final static String CREATED = "Created";
    private final static String FINISHED = "Finished";
    private final static String CANCELED = "Canceled";
    private final static String FAILED = "Failed";

    public static Props props(StartDefinition params, CalculationService calculationService){
        return Props.create(CalculationExecution.class,() -> {
            var run = new CalculationExecution();
            run.params = params;
            run.service = calculationService;

            run.context()
               .system()
               .scheduler()
               .scheduleOnce(Duration.create(1000, TimeUnit.MILLISECONDS),
                       run.self(),
                       INITIALIZING,
                       run.context().dispatcher(),
                       run.self());
            return run;
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetProgress.class, this::handleGetProgress)
                .match(String.class, this::handleSetUp)
                .match(UpdateStatus.class, this::handleStatusUpdate)
                .match(ListExecutions.class, this::handleListExecutions)
                .match(Cancel.class, this::handleCancel)
                .build();
    }

    private void handleSetUp(String status) {
        if(status.equals(INITIALIZING)){
            initialize();
        }
        else {

            context()
                .system()
                .scheduler()
                .scheduleOnce(Duration.create(1000, TimeUnit.MILLISECONDS),
                    self(),
                    CREATED,
                    context().dispatcher(),
                    self());
        }
    }

    private void handleStatusUpdate(UpdateStatus msg) {
        var status = service.getCalculationStatus(params.calculationId).getStatus();
        if (terminated(status)){
            selfDestruct();
        }
        else
            context()
                .system()
                .scheduler()
                .scheduleOnce(Duration.create(msg.pollingIntervalInMs, TimeUnit.MILLISECONDS),
                    self(),
                    new UpdateStatus(statusUpdatePollingIntervalInMs),
                    context().dispatcher(),
                    self());
    }

    private void initialize() {


        try {
            supplyAsync(this::createCalculation)
                    .thenApply(this::uploadFiles)
                    .thenApply(this::startRun)
                    .thenApply(this::updateStatus)
                    .thenAccept(this::endInit);
        } catch (Exception e) {
            throw new RuntimeException("failed to create calculation");
        }


    }

    private String createCalculation(){
        var id =  service.createCalculation();
        params.calculationId = id;
        return id;
    }

    private String uploadFiles(String calcId){

        for(var file : params.inputFiles){
            service.uploadInputFile(calcId, file);
        }
        return calcId;
    }

    private String startRun(String calcId){
        service.startRun(calcId, params.algorithmName, params.parameters);
        return calcId;
    }

    private String updateStatus(String calcId){
        service.startRun(calcId, params.algorithmName, params.parameters);
        return calcId;
    }

    private void endInit(String calcId){
        context()
            .system()
            .scheduler()
            .scheduleOnce(Duration.create(1000, TimeUnit.MILLISECONDS),
                self(),
                CREATED,
                context().dispatcher(),
                self());
    }

    private boolean terminated(String status){
        return status.equals(FINISHED) || status.equals(CANCELED) || status.equals(FAILED);
    }

    private void handleGetProgress(GetProgress msg) {
        if(!msg.calcId.equals(params.calculationId)) return;
        handleListExecutions(new ListExecutions());
    }
    private void handleListExecutions(ListExecutions msg) {
        var state = service.getCalculationStatus(params.calculationId);
        sender().tell(new ExecutionProgress(params.calculationId, new Status()), self());
    }
    private void handleCancel(Cancel msg) {
        if(!msg.calcId.equals(params.calculationId)) return;
        service.cancelCalculation(params.calculationId);
        selfDestruct();
    }
    private void selfDestruct(){
        context().stop(self());
    }
}

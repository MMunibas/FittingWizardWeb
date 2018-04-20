package ch.unibas.fitting.web.calculation.management;

import akka.actor.AbstractActor;
import akka.actor.Props;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.management.messages.*;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class CalculationExecution extends AbstractActor {
    private static final long statusUpdatePollingIntervalInMs = 5000;
    private CalculationService service;
    private StartDefinition params;

    private final static String CREATED = "Created";
    private final static String FINISHED = "Finished";
    private final static String CANCELED = "Canceled";
    private final static String FAILED = "Failed";

    public static Props props(StartDefinition params/*, CalculationService service*/){
        return Props.create(CalculationExecution.class,() -> {
            var run = new CalculationExecution();
            run.params = params;
            run.service = CalculationService.getInstance();

            run.params.calculationId = run.service.createCalculation();
            for(var file : run.params.inputFiles){
                run.service.uploadInputFile(run.params.calculationId, file);
            }
            run.service.startRun(params.calculationId, params.algorithmName, params.parameters);
            run.context()
               .system()
               .scheduler()
               .scheduleOnce(Duration.create(1000, TimeUnit.MILLISECONDS),
                       run.self(),
                       CREATED,
                       run.context().dispatcher(),
                       run.self());
            return run;
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetProgress.class, this::handleGetProgress)
                .match(String.class, this::handleStatusUpdate)
                .match(ListExecutions.class, this::handleListExecutions)
                .match(Cancel.class, this::handleCancel)
                .build();
    }

    private boolean terminated(String status){
        return status.equals(FINISHED) || status.equals(CANCELED) || status.equals(FAILED);
    }

    private void handleStatusUpdate(String status) {
        if (terminated(status)){
            selfDestruct();
        }
        else
            context()
                .system()
                .scheduler()
                .scheduleOnce(Duration.create(statusUpdatePollingIntervalInMs, TimeUnit.MILLISECONDS),
                    self(),
                    service.getCalculationStatus(params.calculationId).getStatus(),
                    context().dispatcher(),
                    self());
    }

    private void handleGetProgress(GetProgress msg) {
        if(!msg.calcId.equals(params.calculationId)) return;
        handleListExecutions(new ListExecutions());
    }
    private void handleListExecutions(ListExecutions msg) {
        var state = service.getCalculationStatus(params.calculationId);
        sender().tell(new ExecutionProgress(params.calculationId, state), self());
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

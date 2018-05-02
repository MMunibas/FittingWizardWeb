package ch.unibas.fitting.web.calculation.management.task;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import ch.unibas.fitting.shared.javaextensions.Function1;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.web.calculation.management.execution.CalculationExecution;
import ch.unibas.fitting.web.calculation.management.CalculationManager;
import ch.unibas.fitting.web.calculation.management.execution.messages.*;
import ch.unibas.fitting.web.calculation.management.task.messages.*;
import org.apache.wicket.request.component.IRequestablePage;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static ch.unibas.fitting.shared.javaextensions.StreamUtils.traverse;

public class Task extends AbstractActor
{
    private static final String initMsg = "Initialize";
    private CalculationService calculationService;
    private NavigationInfo navigationInfo;
    private String title;
    private String username;
    private StartDefinition[] calculationsToSpawn;
    public static Props props(Start startDefinitions, CalculationService calculationService){

        return Props.create(Task.class, ()->{
            var mgr = new Task();
            mgr.calculationService = calculationService;
            mgr.title = startDefinitions.title;
            mgr.username = startDefinitions.username;
            mgr.calculationsToSpawn = startDefinitions.starts;
            mgr.self().tell(initMsg, mgr.self());
            mgr.navigationInfo = startDefinitions.navigationInfo;
            return mgr;
        });
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartDefinition.class, this::handleStart)
                .match(String.class, this::handleStateChange)
                .match(CancelTask.class, this::handleCancelTask)
                .match(CancelExecution.class, this::handleCancelExecution)
                .match(ListExecutions.class, this::handleListExecutions)
                .match(TaskInfo.class, this::handleTaskInfo)
                .match(DeleteTask.class, this::handleDeleteTask)
                .build();
    }


    // region Message handlers

    private void handleStart(StartDefinition msg) {
        context().actorOf(CalculationExecution.props(msg,  calculationService), msg.title);
    }

    private void handleCancelExecution(CancelExecution msg) {
        forwardIfTaskExists(msg.executionId, msg, new CancelExecutionResponse());
    }
    private void handleTaskInfo(TaskInfo msg) {

        askAllChildren(msg, m-> new ListExecutions(), (List<ExecutionProgress> responses) -> new TaskInfoResponse(title, navigationInfo, responses));
    }
    private void handleCancelTask(CancelTask msg) {
        askAllChildren(msg, m-> new CancelExecution(), r-> new CancelTaskResponse());
    }
    private void handleStateChange(String msg) {
        if(msg.equals(initMsg)){
            for(var start: calculationsToSpawn){
                start.taskId = self().path().name();
                self().tell(start, self());
            }
        }

    }
    private void handleListExecutions(ListExecutions msg) {
        askAllChildren(msg, m-> m, ListExecutionsResponse::new);
    }
    private void handleDeleteTask(DeleteTask msg) {
        askAllChildren(msg, m-> new DeleteCalculation(), r-> new DeleteTaskResponse());
        context().stop(self());
    }

    // endregion

    // region Message handlers

    private <TSuccess, TFailure> void forwardIfTaskExists(String taskId, TSuccess msg, TFailure fallback) {
        var actor = context().child(taskId);
        if (actor.isEmpty()) {
            sender().tell(fallback, self());
        } else {
            actor.get().forward(msg, context());
        }
    }
    private <TMsg, TChildMsg, TChildResponse, TResponse> void askAllChildren(TMsg msg, Function1<TMsg, TChildMsg> messageFactory, Function1<List<TChildResponse>, TResponse> responseFactory){

        var children = scala.collection.JavaConversions.asJavaCollection(context().children());
        var answers = traverse(
                children.stream()
                        .map(c -> PatternsCS.ask(c, messageFactory.apply(msg), CalculationManager.AsyncOpTimeoutInMs))
                        .map(CompletionStage::toCompletableFuture)
                        .collect(Collectors.toList()))
                .join()
                .map(x->(TChildResponse)x)
                .collect(Collectors.toList());
        sender().tell(responseFactory.apply(answers), self());
    }
    // endregion

}

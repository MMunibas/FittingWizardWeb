package ch.unibas.fitting.web.calculation.management;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.management.execution.messages.*;
import ch.unibas.fitting.web.calculation.management.task.Task;
import ch.unibas.fitting.web.calculation.management.task.messages.*;
import scala.collection.JavaConversions;

import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static ch.unibas.fitting.shared.javaextensions.StreamUtils.*;

public class CalculationManager extends AbstractActor {

    public static final long AsyncOpTimeoutInMs = 1000 * 5;
    private final CalculationService calculationService;

    public CalculationManager(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    public static Props props(CalculationService calculationService) {
        return Props.create(CalculationManager.class, () -> new CalculationManager(calculationService));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Start.class, this::handleStart)
                .match(CancelTask.class, this::handleCancelTask)
                .match(CancelExecution.class, this::handleCancelExecution)
                .match(DeleteTask.class, this::handleDeleteTask)
                .match(ListTaskExecutions.class, this::handleListTaskExecutions)
                .match(TaskInfo.class, this::handleTaskInfo)
                .build();
    }

    // region Message handlers

    private void handleStart(Start msg) {
        var taskId = ActorUtils.nameWithUniqueSuffix("task_" + msg.username);
        context().actorOf(Task.props(msg, calculationService), taskId);
        sender().tell(new StartResponse(taskId), self());
    }

    private void handleTaskInfo(TaskInfo msg) {
        forwardIfTaskExists(msg.taskId, msg, new TaskInfoResponse("",null, null));
    }
    private void handleDeleteTask(DeleteTask msg) {
        forwardIfTaskExists(msg.taskId, msg, new DeleteTaskResponse());
    }
    private void handleCancelExecution(CancelExecution msg) {
        forwardIfTaskExists(msg.taskId, msg, new CancelExecutionResponse());
    }
    private void handleCancelTask(CancelTask msg) {
        forwardIfTaskExists(msg.taskId, msg, new CancelTaskResponse());
    }
    private void handleListTaskExecutions(ListTaskExecutions msg) {
        var children = JavaConversions.asJavaCollection(context().children());
        var answers = traverse(
                    children.stream()
                    .map(c -> PatternsCS.ask(c, new ListExecutions(), CalculationManager.AsyncOpTimeoutInMs))
                    .map(CompletionStage::toCompletableFuture)
                    .collect(Collectors.toList()))
                .join()
                .map(x->(ListExecutionsResponse)x)
                .collect(Collectors.toList());
        sender().tell(new ListTaskExecutionsResponse(answers), self());
    }

    // endregion

    // region Helper methods

    private <TSuccess, TFailure> void forwardIfTaskExists(String taskId, TSuccess msg, TFailure fallback) {
        var actor = context().child(taskId);
        if (actor.isEmpty()) {
            sender().tell(fallback, self());
        } else {
            actor.get().forward(msg, context());
        }
    }
}

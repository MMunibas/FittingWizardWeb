package ch.unibas.fitting.web.application.calculation.task;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import ch.unibas.fitting.shared.javaextensions.Function1;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.application.calculation.ActorUtils;
import ch.unibas.fitting.web.application.calculation.CalculationManager;
import ch.unibas.fitting.web.application.calculation.execution.CalculationExecution;
import ch.unibas.fitting.web.application.calculation.execution.messages.*;
import ch.unibas.fitting.web.application.calculation.task.messages.*;
import scala.collection.JavaConversions;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static ch.unibas.fitting.shared.javaextensions.StreamUtils.traverse;

public class Task extends AbstractActor {
    private static final String INIT_MESSAGE = "Initialize";

    private final CalculationService calculationService;
    private final Start start;

    public static Props props(Start start, CalculationService calculationService) {
        return Props.create(Task.class, () -> new Task(start, calculationService));
    }

    public Task(Start start, CalculationService calculationService) {
        this.start = start;
        this.calculationService = calculationService;
    }

    @Override
    public void preStart() {
        self().tell(INIT_MESSAGE, self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::handleInitMessage)
                .match(CancelTask.class, this::handleCancelTask)
                .match(CancelExecution.class, this::handleCancelExecution)
                .match(ListExecutions.class, this::handleListExecutions)
                .match(TaskInfo.class, this::handleTaskInfo)
                .match(DeleteTask.class, this::handleDeleteTask)
                .build();
    }

    // region Message handlers

    private void handleCancelExecution(CancelExecution msg) {
        forwardIfTaskExists(msg.executionId, msg, new CancelExecutionResponse());
    }

    private void handleTaskInfo(TaskInfo msg) {
        askAllChildren(
                msg,
                m -> new ListExecutions(),
                (List<ExecutionProgress> responses) -> new TaskInfoResponse(start.title, start.navigationInfo, responses));
    }

    private void handleCancelTask(CancelTask msg) {
        askAllChildren(msg, m -> new CancelExecution(), r -> new CancelTaskResponse());
    }

    private void handleInitMessage(String msg) {
        if (msg.equals(INIT_MESSAGE)) {
            for (var definition : start.definitions) {
                context().actorOf(CalculationExecution.props(definition, calculationService), ActorUtils.nameWithUniqueSuffix("execution"));
            }
        }
    }

    private void handleListExecutions(ListExecutions msg) {
        askAllChildren(msg, m -> m, ListExecutionsResponse::new);
    }

    private void handleDeleteTask(DeleteTask msg) {
        askAllChildren(msg, m -> new DeleteCalculation(), r -> new DeleteTaskResponse());
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

    private <TMsg, TChildMsg, TChildResponse, TResponse> void askAllChildren(
            TMsg msg,
            Function1<TMsg, TChildMsg> messageFactory,
            Function1<List<TChildResponse>, TResponse> responseFactory) {

        var children = JavaConversions.asJavaCollection(context().children());
        var answers = traverse(
                children.stream()
                        .map(c -> PatternsCS.ask(c, messageFactory.apply(msg), CalculationManager.AsyncOpTimeoutInMs))
                        .map(CompletionStage::toCompletableFuture)
                        .collect(Collectors.toList()))
                .join()
                .map(x -> (TChildResponse) x)
                .collect(Collectors.toList());
        sender().tell(responseFactory.apply(answers), self());
    }

    // endregion
}

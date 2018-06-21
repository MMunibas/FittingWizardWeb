package ch.unibas.fitting.web.application.calculation.manager;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import ch.unibas.fitting.web.infrastructure.javaextensions.Function1;
import ch.unibas.fitting.web.infrastructure.javaextensions.FutureUtils;
import ch.unibas.fitting.web.application.calculation.ActorUtils;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.application.calculation.execution.CalculationGroup;
import io.vavr.collection.List;
import scala.collection.JavaConversions;

import java.util.concurrent.CompletionStage;

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
                .match(CalculationProtocol.Start.class, this::handle)
                .match(CalculationProtocol.GetGroupDetails.class, this::handle)
                .match(CalculationProtocol.GetUsersGroups.class, this::handle)
                .match(CalculationProtocol.ListAllRuns.class, this::handle)
                .match(CalculationProtocol.CancelRun.class, this::handle)
                .match(CalculationProtocol.CancelGroup.class, this::handle)
                .match(CalculationProtocol.FinishGroup.class, this::handle)
                .build();
    }

    // region Message handlers

    private void handle(CalculationProtocol.Start msg) {
        var task = context().actorOf(CalculationGroup.props(msg, calculationService),  ActorUtils.uniqueName("group_" + msg.username));
        sender().tell(new CalculationProtocol.StartResponse(task), self());
    }

    private void handle(CalculationProtocol.GetGroupDetails msg) {
        forwardIfTaskExists(msg.groupId, msg, new CalculationProtocol.GetGroupDetailsResponse(null));
    }

    private void handle(CalculationProtocol.GetUsersGroups m) {
        var tasks = List.ofAll(JavaConversions.asJavaCollection(context().children()))
                .filter(actorRef -> actorRef.path().name().startsWith("group_" + m.username))
                .map(actorRef -> actorRef.path().name());

        sender().tell(new CalculationProtocol.GetUsersGroupsResponse(m.username, tasks), self());
    }

    private void handle(CalculationProtocol.ListAllRuns m) {
        this.<CalculationProtocol.GetGroupDetails, CalculationProtocol.GetGroupDetailsResponse, CalculationProtocol.ListAllRunsResponse>askAllChildren
                (
                        (c) -> new CalculationProtocol.GetGroupDetails(c.path().name()),
                        p1 -> new CalculationProtocol.ListAllRunsResponse(p1.flatMap(r -> r.groupDetails).flatMap(t -> t.runs))

                );
    }

    private void handle(CalculationProtocol.CancelRun m) {
        forwardIfTaskExists(m.groupId, m, null);
    }

    private void handle(CalculationProtocol.FinishGroup m) {
        forwardIfTaskExists(m.groupId, m, null);
    }

    private void handle(CalculationProtocol.CancelGroup m) {
        forwardIfTaskExists(m.groupId, m, null);
    }

    // endregion

    // region Helper methods

    private <TSuccess, TFailure> void forwardIfTaskExists(String groupId, TSuccess msg, TFailure fallback) {
        var actor = context().child(groupId);
        if (actor.isEmpty()) {
            if (fallback != null)
                sender().tell(fallback, self());
        } else {
            actor.get().forward(msg, context());
        }
    }

    private <TChildMsg, TChildResponse, TResponse> void askAllChildren(
            Function1<ActorRef, TChildMsg> childMessageCallback,
            Function1<List<TChildResponse>, TResponse> finalReponseCallback) {

        var askFutures = List.ofAll(JavaConversions.asJavaCollection(context().children()))
                .map(child -> (CompletionStage<TChildResponse>)PatternsCS.ask(child,
                        childMessageCallback.apply(child),
                        CalculationManager.AsyncOpTimeoutInMs));
        var answers = FutureUtils.aggregate(askFutures);
        var response = finalReponseCallback.apply(answers);
        sender().tell(response, self());
    }
}

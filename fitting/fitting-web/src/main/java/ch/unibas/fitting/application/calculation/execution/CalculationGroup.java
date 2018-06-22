package ch.unibas.fitting.application.calculation.execution;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import ch.unibas.fitting.infrastructure.javaextensions.Function0;
import ch.unibas.fitting.infrastructure.javaextensions.Function1;
import ch.unibas.fitting.infrastructure.javaextensions.FutureUtils;
import ch.unibas.fitting.application.calculation.ActorUtils;
import ch.unibas.fitting.application.calculation.CalculationService;
import ch.unibas.fitting.application.calculation.manager.CalculationManager;
import ch.unibas.fitting.application.calculation.manager.CalculationProtocol;
import ch.unibas.fitting.application.calculation.manager.GroupDetails;
import io.vavr.collection.List;
import scala.collection.JavaConversions;

import java.util.concurrent.CompletionStage;

public class CalculationGroup extends AbstractActor {

    private final CalculationService calculationService;
    private final CalculationProtocol.Start start;

    public static Props props(CalculationProtocol.Start start, CalculationService calculationService) {
        return Props.create(CalculationGroup.class, () -> new CalculationGroup(start, calculationService));
    }

    public CalculationGroup(CalculationProtocol.Start start, CalculationService calculationService) {
        this.start = start;
        this.calculationService = calculationService;
    }

    @Override
    public void preStart() {
        self().tell(new Initialize(), self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, this::handle)

                .match(CalculationProtocol.GetGroupDetails.class, this::handle)
                .match(CalculationProtocol.CancelRun.class, this::handle)
                .match(CalculationProtocol.CancelGroup.class, this::handle)
                .match(CalculationProtocol.FinishGroup.class, this::handle)

                .match(RunProtocol.StatusChanged.class, this::handle)
                .build();
    }

    private void handle(Initialize m) {
        for (var definition : start.definitions) {
            context().actorOf(CalculationRun.props(definition, calculationService), ActorUtils.uniqueName("execution"));
        }
    }

    // region Message handlers

    private void handle(RunProtocol.StatusChanged m) {
    }

    private void handle(CalculationProtocol.GetGroupDetails m) {
        askAllChildren(
                () -> new RunProtocol.GetDetails(),
                (List<RunProtocol.GetDetailsResponse> responses) -> {
                    var details = new GroupDetails(self().path().name(),
                            start.title,
                            start.navigationInfo,
                            responses.map(r -> r.details));
                    return new CalculationProtocol.GetGroupDetailsResponse(details);
                });
    }

    private void handle(CalculationProtocol.CancelRun cancelRun) {
        forwardIfTaskExists(cancelRun.runId, new RunProtocol.Cancel(), null);
    }

    private void handle(CalculationProtocol.CancelGroup m) {
        broadcastToChildren(new RunProtocol.Cancel());
    }

    private void handle(CalculationProtocol.FinishGroup m) {
        broadcastToChildren(new RunProtocol.Cleanup());
        context().stop(self());
    }

    // endregion

    // region Message handlers

    private <TSuccess, TFailure> void forwardIfTaskExists(String childId, TSuccess msg, TFailure fallback) {
        var actor = context().child(childId);
        if (actor.isEmpty()) {
            if (fallback != null)
                sender().tell(fallback, self());
        } else {
            actor.get().forward(msg, context());
        }
    }

    private void broadcastToChildren(Object msg) {
        for (ActorRef child : JavaConversions.asJavaCollection(context().children())) {
          child.tell(msg, self());
        }
    }

    private <TChildMsg, TChildResponse, TResponse> void askAllChildren(
            Function0<TChildMsg> childMessageCallback,
            Function1<List<TChildResponse>, TResponse> finalReponseCallback) {

        var askFutures = List.ofAll(JavaConversions.asJavaCollection(context().children()))
                .map(child -> (CompletionStage<TChildResponse>)PatternsCS.ask(child,
                        childMessageCallback.apply(),
                        CalculationManager.AsyncOpTimeoutInMs));
        var answers = FutureUtils.aggregate(askFutures);
        var response = finalReponseCallback.apply(answers);
        sender().tell(response, self());
    }

    // endregion

    private static class Initialize {}
}

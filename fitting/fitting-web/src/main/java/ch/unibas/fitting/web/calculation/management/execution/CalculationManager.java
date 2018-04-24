
package ch.unibas.fitting.web.calculation.management.execution;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.pattern.Patterns;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.management.execution.messages.*;
import ch.unibas.fitting.web.calculation.management.task.Task;


public class CalculationManager extends AbstractActor {

    public static final long AsyncOpTimeoutInMs = 1000 * 60 * 5;
    private final CalculationService calculationService;

    public CalculationManager(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    public static Props props(CalculationService calculationService){

        return Props.create(CalculationManager.class, ()->{
            var mgr = new CalculationManager(calculationService);
            return mgr;
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Start.class, this::handleStart)
                .match(Cancel.class, this::handleCancel)
                .match(GetProgress.class, this::handleGetProgress)
                .match(ListExecutions.class, this::handleListExecutions)
                .build();
    }

    private void handleStart(Start msg) {
        context().actorOf(Task.props(msg, calculationService, msg.title));
    }


    private void handleCancel(Cancel msg) {
        context().children().foreach(ref -> {
            ref.tell(msg, self());
            return true;
        });
    }


    private void handleGetProgress(GetProgress msg) {
        context().children().foreach(ref -> {
            ref.tell(msg, self());
            return true;
        });

    }

    private void handleListExecutions(ListExecutions msg) {

        var sender = sender();
        var children = scala.collection.JavaConversions.asJavaIterable(context().children());

        Futures.traverse(
                children,
                ref -> Patterns.ask(ref, msg, CalculationManager.AsyncOpTimeoutInMs),
                context().dispatcher())
                .onComplete(v1 -> v1.map(v11 -> {
                    sender.tell(new ListExecutionsResponse(v11), self());
                    return true;
                }), context().dispatcher());
    }
}

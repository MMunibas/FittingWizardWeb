
package ch.unibas.fitting.web.calculation.management;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.pattern.Patterns;
import ch.unibas.fitting.web.calculation.management.messages.*;


public class CalculationManager extends AbstractActor {

    public static final long AsyncOpTimeoutInMs = 1000 * 60 * 5;

    public static Props props(){

        return Props.create(CalculationManager.class, ()->{
            var mgr = new CalculationManager();
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
        for (var startDef : msg.starts){
            context().actorOf(CalculationExecution.props(startDef));
        }
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

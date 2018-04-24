package ch.unibas.fitting.web.calculation.management.task;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.pattern.Patterns;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.management.execution.CalculationExecution;
import ch.unibas.fitting.web.calculation.management.execution.CalculationManager;
import ch.unibas.fitting.web.calculation.management.execution.messages.*;
import ch.unibas.fitting.web.calculation.management.task.messages.ListTaskExecutions;
import ch.unibas.fitting.web.calculation.management.task.messages.ListTaskExecutionsResponse;

import java.time.Duration;

public class Task extends AbstractActor
{
    private static final String initMsg = "Initialize";
    private CalculationService calculationService;

    private String title;
    private StartDefinition[] calculationsToSpawn;
    public static Props props(Start startDefinitions, CalculationService calculationService, String title){

        return Props.create(Task.class, ()->{
            var mgr = new Task();
            mgr.calculationService = calculationService;
            mgr.title = startDefinitions.title;
            mgr.calculationsToSpawn = startDefinitions.starts;
            mgr.title = title;
            mgr.context().system().scheduler().scheduleOnce(
                    Duration.ofMillis(1),
                    mgr.self(),
                    initMsg,
                    mgr.context().dispatcher(),
                    mgr.self());


            return mgr;
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartDefinition.class, this::handleStart)
                .match(String.class, this::handleStateChange)
                .match(GetProgress.class, this::handleGetProgress)
                .match(ListTaskExecutions.class, this::handleListExecutions)
                .build();
    }

    private void handleStart(StartDefinition msg) {
            context().actorOf(CalculationExecution.props(msg, calculationService), msg.title);
    }
    private void handleStateChange(String msg) {
        if(msg.equals(initMsg)){
            for(var start: calculationsToSpawn){

                context().system().scheduler().scheduleOnce(
                        Duration.ofMillis(1),
                        self(),
                        start,
                        context().dispatcher(),
                        self());

            }
        }

    }
    private void handleGetProgress(GetProgress msg) {
        context().children().foreach(ref -> {
            ref.tell(msg, sender());
            return true;
        });
    }
    private void handleListExecutions(ListTaskExecutions msg) {

        var sender = sender();
        var children = scala.collection.JavaConversions.asJavaIterable(context().children());

        Futures.traverse(
                children,
                ref -> Patterns.ask(ref, msg, CalculationManager.AsyncOpTimeoutInMs),
                context().dispatcher())
                .onComplete(v1 -> v1.map(v11 -> {
                    sender.tell(new ListTaskExecutionsResponse(v11), self());
                    return true;
                }), context().dispatcher());
    }

}

package ch.unibas.fitting.web.calculation.management;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.management.execution.CalculationManager;
import ch.unibas.fitting.web.calculation.management.execution.messages.*;
import ch.unibas.fitting.web.calculation.management.task.messages.SpawnTask;
import io.vavr.control.Option;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
public class CalculationManagementClient {

    public final long AsyncOpTimeoutInMs = 1000 * 2;
    private ActorRef calcMgr;

    @Inject
    public CalculationManagementClient(CalculationService calculationService){
        ActorSystem system = ActorSystem.create("calculation-actors");
        calcMgr = system.actorOf(CalculationManager.props(calculationService));
    }

    public CalculationManagementClient() {}

    public String spawnTask(SpawnTask msg) {
        return "akka://bla/task_mhelmer";
    }

    public Option<String> getTaskForUsername(String username) {
        return Option.none();
    }


    public StartResponse Start(String title, StartDefinition... starts){
        return Synchronized(new Start(title, starts));
    }
    public CancelResponse Cancel(String calcId) {
        return Synchronized(new Cancel(calcId));
    }
    public GetProgressResponse GetProgress(String calcId) {
        return Synchronized(new GetProgress(calcId));
    }
    public ListExecutionsResponse ListExecutions() {
        return Synchronized(new ListExecutions());
    }

    private <TRequest, TResponse> TResponse Synchronized(TRequest request){

        try {
            Future<Object> responseTask = Patterns.ask(calcMgr, request, AsyncOpTimeoutInMs);
            var response = Await.result(responseTask, Duration.create(AsyncOpTimeoutInMs, TimeUnit.MILLISECONDS));
            return (TResponse)response;
        } catch (Exception e) {
            throw new RuntimeException("something went wrong: "+e.toString());
        }
    }
}

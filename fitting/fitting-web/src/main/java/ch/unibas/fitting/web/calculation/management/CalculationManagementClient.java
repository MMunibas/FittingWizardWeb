package ch.unibas.fitting.web.calculation.management;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import ch.unibas.fitting.web.calculation.management.messages.*;
import com.google.inject.Provides;
import scala.concurrent.Await;
import scala.concurrent.Future;

import javax.inject.Singleton;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

@Singleton
public class CalculationManagementClient {

    public final long AsyncOpTimeoutInMs = 1000 * 60 * 5;
    private ActorRef calcMgr;

    private static CalculationManagementClient instance = null;
    @Provides
    public static synchronized CalculationManagementClient getInstance(){
        if(instance == null){
            instance = new CalculationManagementClient();
        }
        return instance;
    }


    public CalculationManagementClient(){
        if(instance != null) return;
        ActorSystem system = ActorSystem.create("calculation-actors");
        calcMgr = system.actorOf(CalculationManager.props());

    }
    public StartResponse Start(StartDefinition... starts){
        return Synchronized(new Start(starts));
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

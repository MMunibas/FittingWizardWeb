package ch.unibas.fitting.web.application.calculation;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.web.application.calculation.execution.messages.*;
import ch.unibas.fitting.web.application.calculation.task.messages.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.runAsync;

@Singleton
public class CalculationManagementClient {

    public final long AsyncOpTimeoutInMs = 1000 * 5;
    private ActorRef calcMgr;

    @Inject
    public CalculationManagementClient(CalculationService calculationService) {
        ActorSystem system = ActorSystem.create("calculation_service");
        calcMgr = system.actorOf(CalculationManager.props(calculationService), "calculation_manager");
    }

    /**
     * Required for wicket and guice integration
     **/
    public CalculationManagementClient() {
    }

    public StartResponse spawnTask(String title,
                                   String username,
                                   NavigationInfo navigationInfo,
                                   StartDefinition... starts) {
        return askSynchronously(new Start(title, username, navigationInfo, starts));
    }

    public TaskInfoResponse getTaskInfo(String taskId) {
        return askSynchronously(new TaskInfo(taskId));
    }

    public CancelExecutionResponse cancelExecution(String taskId, String executionId) {
        return askSynchronously(new CancelExecution(taskId, executionId));
    }

    public void cancelTask(String taskId) {
        runAsync(() -> {
            calcMgr.tell(new CancelTask(taskId), ActorRef.noSender());
            while (!getTaskInfo(taskId).executions.stream().allMatch(e -> e.isCompleted)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            calcMgr.tell(new DeleteTask(taskId), ActorRef.noSender());
        });
    }

    public DeleteTaskResponse deleteTask(String taskId) {
        return askSynchronously(new DeleteTask(taskId));
    }

    public ListTaskExecutionsResponse listExecutions() {
        return askSynchronously(new ListTaskExecutions());
    }

    private <TRequest, TResponse> TResponse askSynchronously(TRequest request) {
        try {
            CompletionStage<Object> responseTask = PatternsCS.ask(calcMgr, request, AsyncOpTimeoutInMs);
            var response = responseTask.toCompletableFuture().get();
            return (TResponse) response;
        } catch (Exception e) {
            throw new RuntimeException("something went wrong: " + e.toString());
        }
    }
}

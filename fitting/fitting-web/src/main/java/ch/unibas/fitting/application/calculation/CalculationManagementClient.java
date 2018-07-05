package ch.unibas.fitting.application.calculation;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import ch.unibas.fitting.application.calculation.manager.CalculationManager;
import ch.unibas.fitting.application.calculation.manager.CalculationProtocol;
import ch.unibas.fitting.application.calculation.manager.StartDefinition;
import ch.unibas.fitting.application.calculation.manager.GroupDetails;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import io.vavr.control.Option;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

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
    protected CalculationManagementClient() {}

    public CalculationProtocol.ListAllRunsResponse listExecutions() {
        return askSynchronously(new CalculationProtocol.ListAllRuns());
    }

    /**
     * Spawns a new calculation group with one or more calculation runs.
     * @param title name shown in the {@link ch.unibas.fitting.web.misc.ProgressPage}
     * @param username association to the user of the spawned {@link ch.unibas.fitting.application.calculation.execution.CalculationGroup}
     * @param navigationInfo details of navigation behavior when calculation is finished
     * @param startDefinitions details of all calculation runs which are spawned together as a {@link ch.unibas.fitting.application.calculation.execution.CalculationGroup}
     * @return
     */
    public CalculationProtocol.StartResponse spawnCalculationGroup(String title,
                                                                   String username,
                                                                   NavigationInfo navigationInfo,
                                                                   StartDefinition... startDefinitions) {
        return askSynchronously(new CalculationProtocol.Start(title, username, navigationInfo, startDefinitions));
    }

    public Option<GroupDetails> getGroupInfo(String groupId) {
        var response = this.<CalculationProtocol.GetGroupDetails, CalculationProtocol.GetGroupDetailsResponse>askSynchronously(new CalculationProtocol.GetGroupDetails(groupId));
        return response.groupDetails;
    }

    public void cancelRun(String groupId, String runId) {
        calcMgr.tell(new CalculationProtocol.CancelRun(groupId, runId), Actor.noSender());
    }

    public void cancelGroup(String groupId) {
        calcMgr.tell(new CalculationProtocol.CancelGroup(groupId), ActorRef.noSender());
    }

    public void finishGroup(String groupId) {
        calcMgr.tell(new CalculationProtocol.FinishGroup(groupId), ActorRef.noSender());
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

    public Option<String> getUsersTask(String username) {
        var response = (CalculationProtocol.GetUsersGroupsResponse)askSynchronously(new CalculationProtocol.GetUsersGroups(username));
        return response.groupIds.headOption();
    }
}

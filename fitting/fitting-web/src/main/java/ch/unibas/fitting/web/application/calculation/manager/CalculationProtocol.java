package ch.unibas.fitting.web.application.calculation.manager;

import akka.actor.ActorRef;
import ch.unibas.fitting.web.application.calculation.execution.RunDetails;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import io.vavr.collection.List;
import io.vavr.control.Option;

public class CalculationProtocol {
    public static class Start {
        public final String title;
        public final String username;
        public final StartDefinition[] definitions;
        public NavigationInfo navigationInfo;

        public Start(String title,
                     String username,
                     NavigationInfo navigationInfo,
                     StartDefinition... definitions){
            this.definitions = definitions;
            this.title = title;
            this.username = username;
            this.navigationInfo = navigationInfo;
        }
    }

    public static class StartResponse {
        public final String groupId;

        public StartResponse(String groupId){
            this.groupId = groupId;
        }

        public StartResponse(ActorRef task) {
            this(task.path().name());
        }
    }

    public static class GetGroupDetails {
        public final String groupId;
        public GetGroupDetails(String groupId){
            this.groupId = groupId;
        }
    }

    public static class GetGroupDetailsResponse {
        public final Option<GroupDetails> groupDetails;

        public GetGroupDetailsResponse(GroupDetails groupDetails) {
            this.groupDetails = Option.of(groupDetails);
        }
    }

    public static class ListAllRuns {}

    public static class ListAllRunsResponse {
        public final List<RunDetails> runDetails;
        public ListAllRunsResponse(List<RunDetails> runDetails){
            this.runDetails = runDetails;
        }
    }

    public static class CancelRun {
        public final String groupId;
        public final String runId;

        public CancelRun(String groupId, String runId) {
            this.groupId = groupId;
            this.runId = runId;
        }
    }

    public static class CancelGroup {
        public final String groupId;

        public CancelGroup(String groupId) {
            this.groupId = groupId;
        }
    }

    public static class FinishGroup {
        public final String groupId;

        public FinishGroup(String groupId) {
            this.groupId = groupId;
        }
    }

    public static class GetUsersGroups {
        public final String username;

        public GetUsersGroups(String username) {
            this.username = username;
        }
    }

    public static class GetUsersGroupsResponse {
        public final String username;
        public final List<String> groupIds;

        public GetUsersGroupsResponse(String username, List<String> groupIds) {
            this.username = username;
            this.groupIds = groupIds;
        }
    }
}

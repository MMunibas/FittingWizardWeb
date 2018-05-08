package ch.unibas.fitting.web.calculation.management.execution.messages;

import akka.actor.ActorRef;
import io.swagger.client.model.Status;

public class ExecutionProgress {

    private final static String FINISHED = "Finished";
    private final static String CANCELED = "Canceled";
    private final static String FAILED = "Failed";

    public final String executionId;
    public final String taskId;
    public final ActorRef actorRef;
    public final Status state;
    private final boolean isEmpty;

    public ExecutionProgress(String taskId, String executionId, Status state, ActorRef ref) {
        this.executionId = executionId;
        this.taskId = taskId;
        this.actorRef = ref;
        if(state == null) {
            this.state = new Status();
            this.isEmpty = true;
        } else {
            this.state = state;
            this.isEmpty = false;
        }
    }

    public boolean isTerminated(){
        if(isEmpty || state == null) return false;
        return state.getStatus().equals(FINISHED) || state.getStatus().equals(CANCELED) || state.getStatus().equals(FAILED);
    }
}

package ch.unibas.fitting.web.application.calculation.execution.messages;

import akka.actor.ActorRef;
import io.swagger.client.model.Status;

public class ExecutionProgress {

    private final static String FINISHED = "Finished";
    private final static String CANCELED = "Canceled";
    private final static String FAILED = "Failed";

    public final String executionId;
    public final String taskId;
    public final ActorRef actorRef;
    public final String status;
    public final String message;

    public final boolean isCompleted;
    public final boolean isCanceled;
    public final boolean isSucceeded;
    public final boolean isFailed;

    public ExecutionProgress(String taskId, String executionId, Status state, ActorRef ref) {
        this.executionId = executionId;
        this.taskId = taskId;
        this.actorRef = ref;

        var status = state != null ? state.getStatus() : null;

        this.isCanceled = status != null && status.equals(CANCELED);
        this.isSucceeded = status != null && status.equals(FINISHED);
        this.isFailed = status != null && status.equals(FAILED);
        this.isCompleted = isCanceled || isSucceeded || isFailed;
        this.status = status != null ? status : "unkown";
        this.message = state != null ? state.getMessage() : "-";
    }
}

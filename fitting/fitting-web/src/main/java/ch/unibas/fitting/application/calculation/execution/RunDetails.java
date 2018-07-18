package ch.unibas.fitting.application.calculation.execution;

import akka.actor.ActorRef;
import com.google.gson.JsonObject;
import io.vavr.control.Option;

import java.io.Serializable;

public class RunDetails implements Serializable {

    public final String executionId;
    public final String groupId;
    public final String calculationId;
    public final ActorRef actorRef;
    public final RunState status;
    public final String message;

    public final boolean isCompleted;
    public final boolean isCanceled;
    public final boolean isSucceeded;
    public final boolean isFailed;

    public RunDetails(String groupId,
                      String executionId,
                      String calculationId,
                      RunState status,
                      String message,
                      ActorRef ref) {
        this.executionId = executionId;
        this.groupId = groupId;
        this.calculationId = calculationId;
        this.actorRef = ref;

        this.isCanceled = status == RunState.Canceled;
        this.isSucceeded = status == RunState.Succeeded;
        this.isFailed = status == RunState.Failed;
        this.isCompleted = isCanceled || isSucceeded || isFailed;
        this.status = status;
        this.message = message != null ? message : "-";
    }
}

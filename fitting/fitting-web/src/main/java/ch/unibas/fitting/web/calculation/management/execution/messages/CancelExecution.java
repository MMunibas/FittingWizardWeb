package ch.unibas.fitting.web.calculation.management.execution.messages;

public class CancelExecution {
    public final String taskId;
    public final String executionId;

    public CancelExecution(String taskId, String executionId){
        this.taskId = taskId;
        this.executionId = executionId;
    }
    public CancelExecution(){
        this.taskId = null;
        this.executionId = null;
    }
}

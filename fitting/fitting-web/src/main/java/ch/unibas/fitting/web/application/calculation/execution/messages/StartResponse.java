package ch.unibas.fitting.web.application.calculation.execution.messages;

public class StartResponse {
    public final String taskId;

    public StartResponse(String taskId){
        this.taskId = taskId;
    }
}

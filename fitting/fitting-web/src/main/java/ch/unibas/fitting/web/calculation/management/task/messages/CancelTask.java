package ch.unibas.fitting.web.calculation.management.task.messages;

public class CancelTask {
    public final String taskId;

    public CancelTask(String taskId){
        this.taskId = taskId;
    }
}

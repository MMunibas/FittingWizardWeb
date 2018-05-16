package ch.unibas.fitting.web.application.calculation.task.messages;

public class DeleteTask {

    public final String taskId;

    public DeleteTask(String taskId){
        this.taskId = taskId;
    }
}

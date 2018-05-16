package ch.unibas.fitting.web.application.calculation.task.messages;

import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.web.application.calculation.execution.messages.ExecutionProgress;

import java.util.List;

public class TaskInfoResponse {
    public final String title;
    public final NavigationInfo navigationInfo;
    public final List<ExecutionProgress> executions;

    public TaskInfoResponse(String title,
                            NavigationInfo navigationInfo,
                            List<ExecutionProgress> executions){
        this.title = title;
        this.navigationInfo = navigationInfo;
        this.executions = executions;
    }
}

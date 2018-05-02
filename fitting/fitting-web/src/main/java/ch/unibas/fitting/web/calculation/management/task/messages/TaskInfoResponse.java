package ch.unibas.fitting.web.calculation.management.task.messages;

import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.web.calculation.management.execution.messages.ExecutionProgress;
import org.apache.wicket.request.component.IRequestablePage;

import java.util.List;

public class TaskInfoResponse {
    public String title;
    public final NavigationInfo navigationInfo;
    public List<ExecutionProgress> executions;
    public TaskInfoResponse(String title, NavigationInfo navigationInfo, List<ExecutionProgress> executions){
        this.title = title;
        this.navigationInfo = navigationInfo;
        this.executions = executions;
    }
}

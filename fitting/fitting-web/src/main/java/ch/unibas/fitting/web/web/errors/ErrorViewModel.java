package ch.unibas.fitting.web.web.errors;

import ch.unibas.fitting.web.application.TaskHandle;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;

/**
 * Created by mhelmer on 28.06.2016.
 */
public class ErrorViewModel {
    private String message;
    private DateTime date;
    private String details;
    private String taskTitle;

    public ErrorViewModel(TaskHandle th, Throwable ex) {
        taskTitle = th.getTitle();
        date = DateTime.now();
        message = ex.getMessage();
        details = ExceptionUtils.getFullStackTrace(ex);
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getMessage() {
        return message;
    }

    public DateTime getDate() {
        return date;
    }

    public String getDetails() {
        return details.replace("\n", "<br/>");
    }
}

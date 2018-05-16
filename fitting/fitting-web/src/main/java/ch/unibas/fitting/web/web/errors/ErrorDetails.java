package ch.unibas.fitting.web.web.errors;

import ch.unibas.fitting.web.application.task.PageContext;
import ch.unibas.fitting.web.application.task.TaskHandle;
import io.vavr.control.Option;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;

/**
 * Created by mhelmer on 28.06.2016.
 */
public class ErrorDetails {
    private final String message;
    private final DateTime date;
    private final String details;
    private final String taskTitle;
    private final Option<PageContext> origin;

    public ErrorDetails(
            TaskHandle th,
            Throwable ex) {
        taskTitle = th.getTitle();
        date = DateTime.now();
        message = ex.getMessage();
        details = ExceptionUtils.getFullStackTrace(ex);
        origin = th.getTaskContext().getOriginPage();
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
        return details;
    }

    public Option<PageContext> getOrigin() {
        return origin;
    }
}

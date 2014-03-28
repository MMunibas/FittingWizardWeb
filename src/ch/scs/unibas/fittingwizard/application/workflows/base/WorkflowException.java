package ch.scs.unibas.fittingwizard.application.workflows.base;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 09:32
 */
public class WorkflowException extends RuntimeException {
    public WorkflowException() {
    }

    public WorkflowException(String message) {
        super(message);
    }

    public WorkflowException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkflowException(Throwable cause) {
        super(cause);
    }

    public WorkflowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

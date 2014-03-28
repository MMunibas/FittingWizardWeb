package ch.scs.unibas.fittingwizard.application.scripts.base;

/**
 * User: mhelmer
 * Date: 06.12.13
 * Time: 13:45
 */
public class ScriptExecutionException extends RuntimeException {
    public ScriptExecutionException() {
        super();
    }

    public ScriptExecutionException(String message) {
        super(message);
    }

    public ScriptExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptExecutionException(Throwable cause) {
        super(cause);
    }

    protected ScriptExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

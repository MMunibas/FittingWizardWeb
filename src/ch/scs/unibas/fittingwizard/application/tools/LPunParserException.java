package ch.scs.unibas.fittingwizard.application.tools;

/**
 * User: mhelmer
 * Date: 06.12.13
 * Time: 15:51
 */
public class LPunParserException extends RuntimeException {
    public LPunParserException() {
    }

    public LPunParserException(String message) {
        super(message);
    }

    public LPunParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public LPunParserException(Throwable cause) {
        super(cause);
    }

    public LPunParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

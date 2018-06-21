package ch.unibas.fitting.web.application.directories;

/**
 * Created by mhelmer on 23.06.2016.
 */
public class DirectoryException extends RuntimeException {
    public DirectoryException(String message) {
        super(message);
    }

    public DirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}

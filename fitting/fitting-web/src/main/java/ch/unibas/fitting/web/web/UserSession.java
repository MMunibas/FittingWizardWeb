package ch.unibas.fitting.web.web;

import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.web.errors.ErrorDetails;
import io.vavr.control.Option;
import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.joda.time.DateTime;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 04.06.2016.
 */
public class UserSession extends WebSession {

    private static final Logger LOGGER = Logger.getLogger(UserSession.class);

    public static final String UsernamePattern = "^[a-zA-Z0-9_-]+$";

    private String username;
    private DateTime created;

    private ErrorDetails lastError;

    private boolean isDebuggingMode;

    public UserSession(Request request) {
        super(request);
        created = DateTime.now();
    }

    public static UserSession current() {
        return (UserSession) Session.get();
    }

    public boolean hasUserName() { return username != null && !username.isEmpty();}

    public DateTime getCreated() {
        return created;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        Pattern pattern = Pattern.compile(UsernamePattern); // only allow A-Za-z0-9_ and must not be empty
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches()) {
            throw new RuntimeException("bad username: only A-Za-z0-9_ allowed. string must not be empty");
        }
        this.username = username;
    }

    public boolean isDebuggingMode() {
        return isDebuggingMode;
    }

    public void setDebuggingMode(boolean debuggingMode) {
        isDebuggingMode = debuggingMode;
    }

    public Option<ErrorDetails> getLastError() {
        return Option.of(lastError);
    }

    public void setFailedTask(TaskHandle th) {
        Throwable ex = th.getException();
        LOGGER.debug("Failed task id [" + th.getId() + "] reported for user [" + th.getUsername() + "]", ex);
        lastError = new ErrorDetails(th, ex);
    }

    public void resetLastError() {
        lastError = null;
    }
}

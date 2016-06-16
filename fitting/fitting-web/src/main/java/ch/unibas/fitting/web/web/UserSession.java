package ch.unibas.fitting.web.web;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.joda.time.DateTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 04.06.2016.
 */
public class UserSession extends WebSession {
    private String username;
    private DateTime created;

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
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]+$"); // only allow A-Za-z0-9_ and must not be empty
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
}

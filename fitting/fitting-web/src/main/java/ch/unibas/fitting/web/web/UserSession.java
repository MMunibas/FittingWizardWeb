package ch.unibas.fitting.web.web;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.joda.time.DateTime;

/**
 * Created by martin on 04.06.2016.
 */
public class UserSession extends WebSession {
    private String username;
    private DateTime created;

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
        this.username = username;
    }
}

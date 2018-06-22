package ch.unibas.fitting.web.misc;

import ch.unibas.fitting.WebApp;
import org.apache.log4j.Logger;

import javax.inject.Singleton;
import java.util.ArrayList;

/**
 * Created by martin on 05.06.2016.
 */
@Singleton
public class SessionCounter  {

    private static final Logger LOGGER = Logger.getLogger(WebApp.class);

    private ArrayList<UserSession> sessions = new ArrayList<>();

    public synchronized ArrayList<UserSession> getSessions() {
        return new ArrayList<>(sessions);
    }

    public synchronized void track(UserSession session) {
        sessions.add(session);
        LOGGER.info("Tracking new session " + session.getId());
    }

    public synchronized void untrack(String sessionId) {
        sessions.removeIf(s -> s.getId().equals(sessionId));
        LOGGER.info("Removed session " + sessionId);
    }
}

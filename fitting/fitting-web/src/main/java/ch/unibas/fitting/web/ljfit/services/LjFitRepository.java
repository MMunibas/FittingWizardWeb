package ch.unibas.fitting.web.ljfit.services;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class LjFitRepository {


    public synchronized Optional<LjFitSession> loadSessionForUser(String username) {
        return Optional.empty();
    }

    public synchronized void save(LjFitSession session) {
    }

    public synchronized void deleteSession(String username) {
    }

    public synchronized boolean sessionExists(String username) {
        return false;
    }
}

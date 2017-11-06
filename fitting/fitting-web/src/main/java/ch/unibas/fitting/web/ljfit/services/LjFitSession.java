package ch.unibas.fitting.web.ljfit.services;

public class LjFitSession {
    private final String username;
    private final SessionParameter sessionParameter;

    public LjFitSession(String username, SessionParameter sessionParameter) {
        this.username = username;
        this.sessionParameter = sessionParameter;
    }

    public String getUsername() {
        return username;
    }

    public SessionParameter getSessionParameter() {
        return sessionParameter;
    }
}

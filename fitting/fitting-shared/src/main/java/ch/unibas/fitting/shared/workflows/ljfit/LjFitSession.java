package ch.unibas.fitting.shared.workflows.ljfit;

public class LjFitSession {
    private final String username;
    private final SessionParameter sessionParameter;
    private final UploadedFiles uploadedFiles;

    public LjFitSession(
            String username,
            SessionParameter sessionParameter,
            UploadedFiles uploadedFiles) {
        this.username = username;
        this.sessionParameter = sessionParameter;
        this.uploadedFiles = uploadedFiles;
    }

    public String getUsername() {
        return username;
    }

    public SessionParameter getSessionParameter() {
        return sessionParameter;
    }

    public UploadedFiles getUploadedFiles() { return uploadedFiles; }
}

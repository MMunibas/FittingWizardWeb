package ch.unibas.fitting.shared.workflows.ljfit;

public class LjFitSession {
    private final String username;
    private final SessionParameter sessionParameter;
    private final UploadedFileNames uploadedFileNames;

    public LjFitSession(
            String username,
            SessionParameter sessionParameter,
            UploadedFileNames uploadedFileNames) {
        this.username = username;
        this.sessionParameter = sessionParameter;
        this.uploadedFileNames = uploadedFileNames;
    }

    public String getUsername() {
        return username;
    }

    public SessionParameter getSessionParameter() {
        return sessionParameter;
    }

    public UploadedFileNames getUploadedFileNames() {
        return uploadedFileNames;
    }
}

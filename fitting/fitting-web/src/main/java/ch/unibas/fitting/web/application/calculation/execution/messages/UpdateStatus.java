package ch.unibas.fitting.web.application.calculation.execution.messages;

public class UpdateStatus {
    public final long pollingIntervalInMs;

    public UpdateStatus(long pollingIntervalInMs){
        this.pollingIntervalInMs = pollingIntervalInMs;
    }
}

package ch.unibas.fitting.web.calculation.management.execution.messages;

public class UpdateStatus {
    public final long pollingIntervalInMs;

    public UpdateStatus(long pollingIntervalInMs){
        this.pollingIntervalInMs = pollingIntervalInMs;
    }
}

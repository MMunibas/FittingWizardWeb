package ch.unibas.fitting.web.calculation.management.messages;

import io.swagger.client.model.Status;

public class ExecutionProgress {
    public final String id;
    public final Status state;

    public ExecutionProgress(String id, Status state) {
        this.id = id;
        this.state = state;
    }
}

package ch.unibas.fitting.shared.workflows.ljfit;

import org.joda.time.DateTime;

public class LjFitRun {
    public final String username;
    public final String dirName;
    public final DateTime created;
    public final LjFitRunInput input;
    public final LjFitRunResult result;

    public LjFitRun(String username,
                    String dirName,
                    DateTime created,
                    LjFitRunInput input,
                    LjFitRunResult result) {
        this.username = username;
        this.dirName = dirName;
        this.created = created;
        this.input = input;
        this.result = result;
    }
}

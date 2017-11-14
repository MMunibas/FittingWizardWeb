package ch.unibas.fitting.shared.workflows.ljfit;

import io.vavr.control.Option;
import org.joda.time.DateTime;

public class LjFitRun {
    public final String username;
    public final String dirName;
    public final DateTime created;
    public final Option<LjFitRunInput> input;
    public final Option<LjFitRunResult> result;

    public LjFitRun(String username,
                    String dirName,
                    DateTime created,
                    Option<LjFitRunInput> input,
                    Option<LjFitRunResult> result) {
        this.username = username;
        this.dirName = dirName;
        this.created = created;
        this.input = input;
        this.result = result;
    }

    public boolean wasSuccessful() {
        return result.isDefined();
    }
}

package ch.unibas.fitting.web.application.algorithms.ljfit;

import io.vavr.control.Option;

public class LjFitRun {
    public final String username;
    public final String dirName;
    public final Option<LjFitRunInput> input;
    public final Option<LjFitRunResult> result;

    public LjFitRun(String username,
                    String dirName,
                    Option<LjFitRunInput> input,
                    Option<LjFitRunResult> result) {
        this.username = username;
        this.dirName = dirName;
        this.input = input;
        this.result = result;
    }

    public boolean wasSuccessful() {
        return result.isDefined();
    }
}

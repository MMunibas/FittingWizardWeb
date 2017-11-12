package ch.unibas.fitting.web.ljfit.ui.step2.run;

import io.vavr.collection.List;

public class RunFromPage {
    public final List<RunPair> runPairs;
    public final double lambda_spacing;

    public RunFromPage(RunPair runPair, double lambda_spacing) {
        this.runPairs = List.of(runPair);
        this.lambda_spacing = lambda_spacing;
    }

    public RunFromPage(List<RunPair> runPairs, double lambda_spacing) {
        this.runPairs = runPairs;
        this.lambda_spacing = lambda_spacing;
    }
}

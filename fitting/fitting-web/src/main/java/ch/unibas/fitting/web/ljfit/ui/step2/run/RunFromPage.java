package ch.unibas.fitting.web.ljfit.ui.step2.run;

import io.vavr.collection.List;

public class RunFromPage {
    public final List<RunPair> runPairs;
    public final double lambda_spacing;
    public final int ncpus;
    public final String clusterName;

    public RunFromPage(RunPair runPair,
                       double lambda_spacing,
                       int ncpus,
                       String clusterName) {
        this(List.of(runPair), lambda_spacing, ncpus, clusterName);
    }

    public RunFromPage(List<RunPair> runPairs,
                       double lambda_spacing,
                       int ncpus,
                       String clusterName) {
        this.runPairs = runPairs;
        this.lambda_spacing = lambda_spacing;
        this.ncpus = ncpus;
        this.clusterName = clusterName;
    }
}

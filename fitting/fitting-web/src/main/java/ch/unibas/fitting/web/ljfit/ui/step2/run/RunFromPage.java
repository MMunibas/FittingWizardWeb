package ch.unibas.fitting.web.ljfit.ui.step2.run;

import io.vavr.collection.List;

public class RunFromPage {
    public final List<RunPair> runPairs;
    public final double lambda_size_electrostatic;
    public final double lambda_size_vdw;
    public final int ncpus;

    public RunFromPage(RunPair runPair,
                       double lambda_size_electrostatic,
                       double lambda_size_vdw,
                       int ncpus) {
        this(List.of(runPair), lambda_size_electrostatic, lambda_size_vdw, ncpus);
    }

    public RunFromPage(List<RunPair> runPairs,
                       double lambda_size_electrostatic,
                       double lambda_size_vdw,
                       int ncpus) {
        this.runPairs = runPairs;
        this.lambda_size_electrostatic = lambda_size_electrostatic;
        this.lambda_size_vdw = lambda_size_vdw;
        this.ncpus = ncpus;
    }
}

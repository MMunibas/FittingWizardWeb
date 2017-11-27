package ch.unibas.fitting.shared.workflows.ljfit;

public class LjFitRunInput {
    public final double lambdaEpsilon;
    public final double lambdaSigma;
    public final double lambdaSpacing;
    public final double temperature;

    public LjFitRunInput(double lambdaEpsilon,
                         double lambdaSigma,
                         double lambdaSpacing,
                         double temperature) {
        this.lambdaEpsilon = lambdaEpsilon;
        this.lambdaSigma = lambdaSigma;
        this.lambdaSpacing = lambdaSpacing;
        this.temperature = temperature;
    }
}

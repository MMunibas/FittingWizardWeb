package ch.unibas.fitting.shared.workflows.ljfit;

public class LjFitRunInput {
    public final double lambdaEpsilon;
    public final double lambdaSigma;
    public final double lambdaSpacing;

    public LjFitRunInput(double lambdaEpsilon, double lambdaSigma, double lambdaSpacing) {
        this.lambdaEpsilon = lambdaEpsilon;
        this.lambdaSigma = lambdaSigma;
        this.lambdaSpacing = lambdaSpacing;
    }
}

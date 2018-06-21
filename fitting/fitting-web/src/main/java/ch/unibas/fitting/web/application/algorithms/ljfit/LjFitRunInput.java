package ch.unibas.fitting.web.application.algorithms.ljfit;

public class LjFitRunInput {
    public final double lambdaEpsilon;
    public final double lambdaSigma;
    public final double lambda_size_electrostatic;
    public final double lambda_size_vdw;
    public final double temperature;

    public LjFitRunInput(double lambdaEpsilon,
                         double lambdaSigma,
                         double lambda_size_electrostatic,
                         double lambda_size_vdw,
                         double temperature) {
        this.lambdaEpsilon = lambdaEpsilon;
        this.lambdaSigma = lambdaSigma;
        this.lambda_size_electrostatic = lambda_size_electrostatic;
        this.lambda_size_vdw = lambda_size_vdw;
        this.temperature = temperature;
    }
}

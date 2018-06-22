package ch.unibas.fitting.application.algorithms.ljfit;

public class LjFitRunInput {
    public final double lambda_epsilon;
    public final double lambda_sigma;
    public final double lambda_size_electrostatic;
    public final double lambda_size_vdw;
    public final double temperature;

    public LjFitRunInput(double lambda_epsilon,
                         double lambda_sigma,
                         double lambda_size_electrostatic,
                         double lambda_size_vdw,
                         double temperature) {
        this.lambda_epsilon = lambda_epsilon;
        this.lambda_sigma = lambda_sigma;
        this.lambda_size_electrostatic = lambda_size_electrostatic;
        this.lambda_size_vdw = lambda_size_vdw;
        this.temperature = temperature;
    }
}

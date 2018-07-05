package ch.unibas.fitting.web.ljfit.commands;

public class RunPair {
    public final double lambda_sigma, lambda_epsilon;

    public RunPair(double lambda_sigma, double lambda_epsilon) {
        this.lambda_sigma = lambda_sigma;
        this.lambda_epsilon = lambda_epsilon;
    }
}

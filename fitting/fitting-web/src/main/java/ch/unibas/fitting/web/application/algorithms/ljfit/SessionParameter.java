package ch.unibas.fitting.web.application.algorithms.ljfit;

public class SessionParameter {
    public final double lambda_size_electrostatic;
    public final double lambda_size_vdw;
    public final double temperature;
    public final double expectedDensity;
    public final double expectedDeltaH;
    public final double expectedDeltaG;

    public SessionParameter(
            double lambda_size_electrostatic,
            double lambda_size_vdw,
            double temperature,
            double expectedDensity,
            double expectedDeltaH,
            double expectedDeltaG) {
        this.lambda_size_electrostatic = lambda_size_electrostatic;
        this.lambda_size_vdw = lambda_size_vdw;
        this.temperature = temperature;
        this.expectedDensity = expectedDensity;
        this.expectedDeltaH = expectedDeltaH;
        this.expectedDeltaG = expectedDeltaG;
    }
}

package ch.unibas.fitting.shared.workflows.ljfit;

public class SessionParameter {
    public final double lambda_size_electrostatic;
    public final double lambda_size_vdw;
    public final double temperature;
    public final double molarMass;
    public final int numberOfResidues;
    public final double expectedDensity;
    public final double expectedDeltaH;
    public final double expectedDeltaG;

    public SessionParameter(
            double lambda_size_electrostatic,
            double lambda_size_vdw,
            double temperature,
            double molarMass,
            int numberOfResidues,
            double expectedDensity,
            double expectedDeltaH,
            double expectedDeltaG) {
        this.lambda_size_electrostatic = lambda_size_electrostatic;
        this.lambda_size_vdw = lambda_size_vdw;
        this.temperature = temperature;
        this.molarMass = molarMass;
        this.numberOfResidues = numberOfResidues;
        this.expectedDensity = expectedDensity;
        this.expectedDeltaH = expectedDeltaH;
        this.expectedDeltaG = expectedDeltaG;
    }
}

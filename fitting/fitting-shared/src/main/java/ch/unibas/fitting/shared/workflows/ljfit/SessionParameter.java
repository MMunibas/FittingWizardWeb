package ch.unibas.fitting.shared.workflows.ljfit;

public class SessionParameter {
    public final double lambdaSpacing;
    public final double temperature;
    public final double molarMass;
    public final double numberOfResidues;
    public final double expectedDensity;
    public final double expectedDeltaH;
    public final double expectedDeltaG;

    public SessionParameter(
            double lambdaSpacing,
            double temperature,
            double molarMass,
            double numberOfResidues,
            double expectedDensity,
            double expectedDeltaH,
            double expectedDeltaG) {
        this.lambdaSpacing = lambdaSpacing;
        this.temperature = temperature;
        this.molarMass = molarMass;
        this.numberOfResidues = numberOfResidues;
        this.expectedDensity = expectedDensity;
        this.expectedDeltaH = expectedDeltaH;
        this.expectedDeltaG = expectedDeltaG;
    }
}

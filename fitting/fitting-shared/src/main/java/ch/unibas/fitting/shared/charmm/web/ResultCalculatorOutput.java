package ch.unibas.fitting.shared.charmm.web;

/**
 * Created by tobias on 27.06.16.
 */
public class ResultCalculatorOutput {
    private double density;
    private double deltaH;
    private double deltaG;

    public ResultCalculatorOutput(Double density, Double deltaH, Double deltaG) {
        this.density = density;
        this.deltaH = deltaH;
        this.deltaG = deltaG;
    }

    public double getDensity() {
        return density;
    }

    public double getDeltaH() {
        return deltaH;
    }

    public double getDeltaG() {
        return deltaG;
    }
}

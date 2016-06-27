package ch.unibas.fitting.shared.charmm.web;

/**
 * Created by tobias on 27.06.16.
 */
public class ResultCalculatorOutput {
    private Double density;
    private Double deltaH;
    private Double deltaG;

    public ResultCalculatorOutput(Double density, Double deltaH, Double deltaG) {
        this.density = density;
        this.deltaH = deltaH;
        this.deltaG = deltaG;
    }

    public Double getDensity() {
        return density;
    }

    public Double getDeltaH() {
        return deltaH;
    }

    public Double getDeltaG() {
        return deltaG;
    }
}

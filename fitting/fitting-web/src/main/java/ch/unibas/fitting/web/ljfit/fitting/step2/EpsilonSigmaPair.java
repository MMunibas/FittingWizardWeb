package ch.unibas.fitting.web.ljfit.fitting.step2;

import java.io.Serializable;

public class EpsilonSigmaPair implements Serializable {
    private Double eps, sigma;
    private Boolean selected;

    public EpsilonSigmaPair(Double eps, Double sigma, Boolean selected) {
        this.eps = eps;
        this.sigma = sigma;
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "eps"+ Double.toString(this.eps) + " sigma:" + Double.toString(this.sigma);
    }

    public Double getEps() { return eps; }

    public void setEps(Double _eps) { this.eps = _eps; }

    public Double getSigma() { return sigma; }

    public void setSigma(Double _sigma) { this.sigma = _sigma; }

    public Boolean getSelected() { return selected; }

    public void setSelected(Boolean _selected) { this.selected = _selected; }
}

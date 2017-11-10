package ch.unibas.fitting.web.ljfit.ui.step2;

import java.io.Serializable;

public class EpsilonSigmaPair implements Serializable {
    private Double eps, sigma;
    private Boolean selected;

    public EpsilonSigmaPair(Double _eps, Double _sigma, Boolean _selected) {
        this.eps = _eps;
        this.sigma = _sigma;
        this.selected = _selected;
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

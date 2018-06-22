package ch.unibas.fitting.web.ljfit.fitting.step2;
import java.io.Serializable;


public class GridPanelParameter implements Serializable {
    private Integer number_eps;
    private Double delta_eps;
    private Integer number_sigma;
    private Double delta_sigma;

    public GridPanelParameter(Integer number_eps, Double delta_eps, Integer number_sigma, Double delta_sigma) {
        this.number_eps = number_eps;
        this.delta_eps = delta_eps;
        this.number_sigma = number_sigma;
        this.delta_sigma = delta_sigma;
    }

    public Integer getNumber_eps() { return number_eps; }

    public void setNumber_eps(Integer number_eps) { this.number_eps = number_eps; }

    public Double getDelta_eps() { return delta_eps; }

    public void setDelta_eps(Double delta_eps) { this.delta_eps = delta_eps; }

//    public void setDelta_eps(String delta_eps) { this.delta_eps = Double.parseDouble(delta_eps); }

    public Integer getNumber_sigma() { return number_sigma; }

    public void setNumber_sigma(Integer number_sigma) { this.number_sigma = number_sigma; }

    public Double getDelta_sigma() { return delta_sigma; }

    public void setDelta_sigma(Double delta_sigma) { this.delta_sigma = delta_sigma; }
}


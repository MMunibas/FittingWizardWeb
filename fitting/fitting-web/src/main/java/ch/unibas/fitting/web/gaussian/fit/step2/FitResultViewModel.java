package ch.unibas.fitting.web.gaussian.fit.step2;

import ch.unibas.fitting.shared.molecules.AtomTypeId;

/**
 * Created by tschmidt on 17.06.2016.
 */
public class FitResultViewModel {
    private final AtomTypeId atomTypeId;
    private Double Q00 = null;
    private Double Q10 = null;
    private Double Q1C = null;
    private Double Q1S = null;
    private Double Q20 = null;
    private Double Q21C = null;
    private Double Q21S = null;
    private Double Q22C = null;
    private Double Q22S = null;

    public FitResultViewModel(AtomTypeId atomTypeId) {
        this.atomTypeId = atomTypeId;
    }

    public String getAtomTypeName() {
        return atomTypeId.getName();
    }

    public AtomTypeId getAtomTypeId() {
        return atomTypeId;
    }

    public Double getQ00() {
        return Q00;
    }

    public void setQ00(Double q00) {
        Q00 = q00;
    }

    public Double getQ10() {
        return Q10;
    }

    public void setQ10(Double q10) {
        Q10 = q10;
    }

    public Double getQ1C() {
        return Q1C;
    }

    public void setQ1C(Double q1C) {
        Q1C = q1C;
    }

    public Double getQ1S() {
        return Q1S;
    }

    public void setQ1S(Double q1S) {
        Q1S = q1S;
    }

    public Double getQ20() {
        return Q20;
    }

    public void setQ20(Double q20) {
        Q20 = q20;
    }

    public Double getQ21C() {
        return Q21C;
    }

    public void setQ21C(Double q21C) {
        Q21C = q21C;
    }

    public Double getQ21S() {
        return Q21S;
    }

    public void setQ21S(Double q21S) {
        Q21S = q21S;
    }

    public Double getQ22C() {
        return Q22C;
    }

    public void setQ22C(Double q22C) {
        Q22C = q22C;
    }

    public Double getQ22S() {
        return Q22S;
    }

    public void setQ22S(Double q22S) {
        Q22S = q22S;
    }
}

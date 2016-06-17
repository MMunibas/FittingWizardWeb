package ch.unibas.fitting.web.gaussian.fit.step1;

import ch.unibas.fitting.shared.molecules.AtomTypeId;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * Created by tschmidt on 17.06.2016.
 */
public class AtomTypesViewModel implements Serializable {
    private final AtomTypeId atomTypeId;
    private Double charge = null;

    public AtomTypesViewModel(AtomTypeId atomTypeId) {
        this.atomTypeId = atomTypeId;
    }

    public String getAtomTypeName() {
        return atomTypeId.getName();
    }

    public Double getCharge() {
        return charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    public boolean isChargeResolved() {
        return charge != null;
    }

    public AtomTypeId getAtomTypeId() {
        return atomTypeId;
    }
}

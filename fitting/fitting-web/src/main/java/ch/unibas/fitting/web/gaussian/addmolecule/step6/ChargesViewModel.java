package ch.unibas.fitting.web.gaussian.addmolecule.step6;

import java.io.Serializable;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class ChargesViewModel implements Serializable {

    private final String moleculeName;
    private String atomLabel;
    private int[] indices;
    private final Double chargeFromFile;
    private Double userCharge;

    public ChargesViewModel(
            String moleculeName,
            String atomLabel,
            int[] indices,
            Double chargeFromFile,
            Double userCharge) {
        this.moleculeName = moleculeName;
        this.atomLabel = atomLabel;
        this.indices = indices;
        this.chargeFromFile = chargeFromFile;
        this.userCharge = userCharge;
    }

    public String getMoleculeName() {
        return moleculeName;
    }

    public String getAtomLabel() {
        return atomLabel;
    }

    public int[] getIndices() {
        return indices;
    }

    public Double getUserCharge() {

        return userCharge != null ? userCharge : chargeFromFile;
    }

    public void setUserCharge(Double userCharge) {
        this.userCharge = userCharge;
    }

    public boolean isChargeDefined() {
        return userCharge != null;
    }
}

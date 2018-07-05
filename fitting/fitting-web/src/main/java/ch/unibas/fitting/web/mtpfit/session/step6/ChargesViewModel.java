package ch.unibas.fitting.web.mtpfit.session.step6;

import java.io.Serializable;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class ChargesViewModel implements Serializable {

    private final String moleculeName;
    private String atomLabel;
    private final int index;
    private int[] highlightIndices;
    private final Double chargeFromFile;
    private Double userCharge;

    public ChargesViewModel(
            String moleculeName,
            String atomType,
            int index,
            int[] highlightIndices,
            Double chargeFromFile,
            Double userCharge) {
        this.moleculeName = moleculeName;
        this.atomLabel = atomType;
        this.index = index;
        this.highlightIndices = highlightIndices;
        this.chargeFromFile = chargeFromFile;
        this.userCharge = userCharge;
    }

    public String getMoleculeName() {
        return moleculeName;
    }

    public String getAtomType() {
        return atomLabel;
    }

    public int getIndex() {
        return index;
    }

    public int[] getHighlightIndices() {
        return highlightIndices;
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

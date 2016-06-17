package ch.unibas.fitting.web.gaussian.addmolecule.step6;

import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class ChargesViewModel implements Serializable {

    private String name;
    private int[] indices;
    private Double userCharge;

    public ChargesViewModel(String name, int[] indices) {

        this.name = name;
        this.indices = indices;
    }

    public String getName() {
        return name;
    }

    public int[] getIndices() {
        return indices;
    }

    public Double getUserCharge() {
        return userCharge;
    }

    public void setUserCharge(Double userCharge) {
        this.userCharge = userCharge;
    }
}

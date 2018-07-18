package ch.unibas.fitting.web.mtpfit.fitting.step2;

import ch.unibas.fitting.application.algorithms.mtpfit.ChargeValue;
import ch.unibas.fitting.application.algorithms.mtpfit.FitResult;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by tschmidt on 17.06.2016.
 */
public class FitResultViewModel implements Serializable {
    private final String atomType;
    private HashMap<String, ChargeValue> values = new HashMap<>();

    public FitResultViewModel(
            FitResult fr) {
        this.atomType = fr.getAtomTypeName();
        fr.getChargeValues().forEach(chargeValue -> {
            values.put(chargeValue.getMultipoleComponent().toLowerCase(), chargeValue);
        });
    }

    public String getAtomType() {
        return atomType;
    }

    public String getValue(String chargeType) {
        var charge = value(chargeType.toLowerCase());
        return String.format("%7.4f", charge == null ? 0.0 : charge);
    }

    public String getColor(String chargeType) {
        var value = value(chargeType);
        if (value == null)
            return "#FFFFFF";
        value = Math.abs(value);
        if (chargeType.equalsIgnoreCase("Q00") && value > 0.8)
            return "#FF0000";
        if (value > 1.2)
            return "#FF0000";
        return "#FFFFFF";
    }

    private Double value(String chargeType) {
        chargeType = chargeType.toLowerCase();
        if (!values.containsKey(chargeType)) return null;
        return values.get(chargeType).getValue();
    }
}

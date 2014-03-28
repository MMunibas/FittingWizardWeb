package ch.unibas.fittingwizard.application.fitting;

import ch.unibas.fittingwizard.application.tools.charges.ChargeTypes;

import java.util.List;

/**
 *
 * User: mhelmer
 * Date: 11.12.13
 * Time: 19:02
 */
public class InitialQ00 {

    private final List<ChargeValue> chargeValues;

    public InitialQ00(List<ChargeValue> chargeValues) {
        this.chargeValues = chargeValues;
        verifyCharges();
    }

    private void verifyCharges() {
        for (ChargeValue chargeValue : chargeValues) {
            if (!chargeValue.getType().equalsIgnoreCase(ChargeTypes.charge)) {
                throw new IllegalArgumentException("Inital charge must be of type Q00.");
            }
        }
    }

    public List<ChargeValue> getChargeValues() {
        return chargeValues;
    }
}

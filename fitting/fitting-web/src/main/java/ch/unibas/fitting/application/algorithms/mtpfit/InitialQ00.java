/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.application.algorithms.mtpfit;

import java.util.LinkedHashSet;

/**
 *
 * User: mhelmer
 * Date: 11.12.13
 * Time: 19:02
 */
public class InitialQ00 {

    private final LinkedHashSet<ChargeValue> chargeValues;

    public InitialQ00(LinkedHashSet<ChargeValue> chargeValues) {
        this.chargeValues = chargeValues;
        verifyCharges();
    }

    private void verifyCharges() {
        for (ChargeValue chargeValue : chargeValues) {
            if (!chargeValue.getMultipoleComponent().equalsIgnoreCase(ChargeTypes.charge)) {
                throw new IllegalArgumentException("Inital charge must be of type Q00.");
            }
        }
    }

    public Double findQ00(String atomTypeId) {
        for (ChargeValue initialValue : chargeValues) {
            if (initialValue.getAtomType().equals(atomTypeId))
                return initialValue.getValue();
        }
        throw new RuntimeException("Missing inital Q for atom type " + atomTypeId);
    }
}

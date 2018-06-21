/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.fitting;


import io.vavr.collection.List;

public class OutputAtomType {
    private final String atomType;
    private List<ChargeValue> chargeValues;

    public OutputAtomType(String atomType, List<ChargeValue> chargeValues) {
        this.atomType = atomType;
        this.chargeValues = chargeValues;
    }

    public String getAtomType() {
        return atomType;
    }

    public List<ChargeValue> getChargeValues() {
        return chargeValues;
    }

    public void add(ChargeValue chargeValue) {
        chargeValues = chargeValues.append(chargeValue);
    }
}

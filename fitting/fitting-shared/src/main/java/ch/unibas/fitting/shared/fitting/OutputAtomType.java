/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.fitting;


import ch.unibas.fitting.shared.molecules.AtomTypeId;
import io.vavr.collection.List;

public class OutputAtomType {
    private final AtomTypeId id;
    private List<ChargeValue> chargeValues;

    public OutputAtomType(AtomTypeId id, List<ChargeValue> chargeValues) {
        this.id = id;
        this.chargeValues = chargeValues;
    }

    public AtomTypeId getId() {
        return id;
    }

    public List<ChargeValue> getChargeValues() {
        return chargeValues;
    }

    public void add(ChargeValue chargeValue) {
        chargeValues = chargeValues.append(chargeValue);
    }
}

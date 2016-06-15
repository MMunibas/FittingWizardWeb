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

import java.util.ArrayList;
import java.util.List;

/**
 * Charge values grouped by atom type.
 * User: mhelmer
 * Date: 10.12.13
 * Time: 17:47
 */
public class OutputAtomType {
    private final AtomTypeId id;
    private final List<ChargeValue> chargeValues;

    public OutputAtomType(AtomTypeId id, List<ChargeValue> chargeValues) {
        this.id = id;
        this.chargeValues = new ArrayList<>(chargeValues);
    }

    public AtomTypeId getId() {
        return id;
    }

    public List<ChargeValue> getChargeValues() {
        return chargeValues;
    }
}

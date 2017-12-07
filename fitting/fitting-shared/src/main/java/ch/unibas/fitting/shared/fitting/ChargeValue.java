/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.fitting;


import ch.unibas.fitting.shared.tools.AtomTypeId;

/**
* User: mhelmer
* Date: 11.12.13
* Time: 19:47
*/
public class ChargeValue {
    private final AtomTypeId id;
    private final String type;
    private final double value;

    public ChargeValue(AtomTypeId id, String type, double value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public AtomTypeId getAtomTypeId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChargeValue that = (ChargeValue) o;

        if (!id.equals(that.id)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}

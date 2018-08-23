/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.application.algorithms.mtpfit;


import java.io.Serializable;

/**
* User: mhelmer
* Date: 11.12.13
* Time: 19:47
*/
public class ChargeValue implements Serializable {
    private final String atomType;
    private final String multipoleComponent;
    private final double value;
    private final int index;

    public ChargeValue(String atomType,
                       String multipoleComponent,
                       double value,
                       int index) {
        this.atomType = atomType;
        this.multipoleComponent = multipoleComponent;
        this.value = value;
        this.index = index;
    }

    public String getMultipoleComponent() {
        return multipoleComponent;
    }

    public double getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public String getAtomType() {
        return atomType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChargeValue that = (ChargeValue) o;

        if (!atomType.equals(that.atomType)) return false;
        if (!multipoleComponent.equals(that.multipoleComponent)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = multipoleComponent.hashCode();
        result = 31 * result + atomType.hashCode();
        return result;
    }
}

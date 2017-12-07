/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.tools;

public class LPunAtomType {
    private final AtomTypeId id;
    private final double charge;
    private final int[] indices;
    private Double userQ0 = null;

    public LPunAtomType(String atomType,
                        double charge,
                        int[] indices) {
        this.id = new AtomTypeId(atomType);
        this.charge = charge;
        this.indices = indices;
    }

    public AtomTypeId getId() {
        return id;
    }

    public double getCharge() {
        return charge;
    }

    /**
     * Gets the indices this atom type occurs in the _l.pun file.
     * The indices correspond to the positions in the XYZ file.
     * @return
     */
    public int[] getIndices() {
        return indices;
    }

    /**
     * Gets the user defined charge of Q0.
     * This is an optional value.
     * @return
     */
    @Deprecated
    public Double getUserQ00() {
        return userQ0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LPunAtomType lPunAtomType = (LPunAtomType) o;

        if (!id.equals(lPunAtomType.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

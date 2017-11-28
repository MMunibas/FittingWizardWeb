/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.molecules;

/**
 * User: mhelmer
 * Date: 05.12.13
 * Time: 17:40
 */
public class AtomType {
    private final AtomTypeId id;
    private final int[] indices;
    private Double userQ0 = null;

    public AtomType(String atomType, int[] indices) {
        this.id = new AtomTypeId(atomType);
        this.indices = indices;
    }

    public AtomTypeId getId() {
        return id;
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

        AtomType atomType = (AtomType) o;

        if (!id.equals(atomType.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.types;

/**
 *
 * @author hedin
 */
public class Bond {

    private Atom a1;
    private Atom a2;
    private double length;

    public Bond(Atom _at1, Atom _at2) {
        this.a1 = _at1;
        this.a2 = _at2;
    }

    public Bond(Atom _at1, Atom _at2, double _l) {
        this.a1 = _at1;
        this.a2 = _at2;
        this.length = _l;
    }

}

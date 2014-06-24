/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.types;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 *
 * @author hedin
 */
public class Bond {

    private Atom a1 = null;
    private Atom a2 = null;
    private double length = 0.0;

    public Bond(Atom _at1, Atom _at2) {
        this.a1 = _at1;
        this.a2 = _at2;
        this.length = calcLength(a1, a2);
    }

    public Bond(Atom _at1, Atom _at2, double _l) {
        this.a1 = _at1;
        this.a2 = _at2;
        this.length = _l;
    }

    public static double calcLength(Atom a1, Atom a2) {
        double l = 0.0;
        l = pow(a1.getX() - a2.getX(), 2) + pow(a1.getY() - a2.getY(), 2) + pow(a1.getZ() - a2.getZ(), 2);
        l = sqrt(l);
        return l;
    }

}//end class

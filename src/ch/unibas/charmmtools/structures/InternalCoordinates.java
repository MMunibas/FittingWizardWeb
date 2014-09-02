/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.structures;

/**
 *
 * @author hedin
 */
public class InternalCoordinates {

    //4 atoms composing the internal coordinates structure from charmm
    private Atom at1, at2, at3, at4;

    //Bond length between first two atoms
    //Bond length between last two atoms
    private double bnd12, bnd23;

    //Bond angle between first three atoms
    //Bond angle between last three atoms
    private double ang123, ang234;

    //Torsion angle made by the four atoms (degree)
    private double dihe;

    //Flag indicating that this is an improper torsion
    private boolean isImproper;

    public InternalCoordinates(Atom a1, Atom a2, Atom a3, Atom a4, boolean impr) {
        this.at1 = a1;
        this.at2 = a2;
        this.at3 = a3;
        this.at4 = a4;
        this.isImproper = impr;

        bnd12 = Bond.calcLength(at1, at2);
        bnd23 = Bond.calcLength(at2, at3);

        // false because we want the angles and dihedral in degrees
        ang123 = Angle.calculateAngle(at1, at2, at3, false);
        ang234 = Angle.calculateAngle(at2, at3, at4, false);
        dihe = Dihedral.calculateDihe(at1, at2, at3, at4, false);

    }//ctor

}

/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.types;

import ch.unibas.charmmtools.utils.MathTools;

/**
 *
 * @author hedin
 */
public class Dihedral {

    Atom a1 = null;
    Atom a2 = null;
    Atom a3 = null;
    Atom a4 = null;

    double dihe = 0.0;
    boolean inRadian = false;

    public Dihedral(Atom _a1, Atom _a2, Atom _a3, Atom _a4) {
        this.a1 = _a1;
        this.a2 = _a2;
        this.a3 = _a3;
        this.a4 = _a4;
        this.dihe = calculateDihe(a1, a2, a3, a4, inRadian);
    }

    public Dihedral(Atom _a1, Atom _a2, Atom _a3, Atom _a4, double _dihe) {
        this.a1 = _a1;
        this.a2 = _a2;
        this.a3 = _a3;
        this.a4 = _a4;
        this.dihe = _dihe;
    }

    public Dihedral(Atom _a1, Atom _a2, Atom _a3, Atom _a4, boolean _radian) {
        this.a1 = _a1;
        this.a2 = _a2;
        this.a3 = _a3;
        this.a4 = _a4;
        if (_radian) {
            this.inRadian = true;
        }
        this.dihe = calculateDihe(a1, a2, a3, a4, inRadian);
    }

    public Dihedral(Atom _a1, Atom _a2, Atom _a3, Atom _a4, double _dihe, boolean _radian) {
        this.a1 = _a1;
        this.a2 = _a2;
        this.a3 = _a3;
        this.a4 = _a4;
        this.dihe = _dihe;
        if (_radian) {
            this.inRadian = true;
        }
    }

    public static double calculateDihe(Atom a1, Atom a2, Atom a3, Atom a4, boolean radian) {

        // first build 3 vectors from the 4 atoms
        double[] u = new double[3];
        double[] v = new double[3];
        double[] w = new double[3];

        u[0] = a2.getX() - a1.getX();
        u[1] = a2.getY() - a1.getY();
        u[2] = a2.getZ() - a1.getZ();

        v[0] = a3.getX() - a2.getX();
        v[1] = a3.getY() - a2.getY();
        v[2] = a3.getZ() - a2.getZ();

        w[0] = a4.getX() - a3.getX();
        w[1] = a4.getY() - a3.getY();
        w[2] = a4.getZ() - a3.getZ();

        //get 2 normalised vectors perpendicular to the planes defined by the 3 previous vectors
        double[] n1 = MathTools.crossProd3x3(u, v);
        double norm1 = MathTools.norm3(n1);
        n1[0] /= norm1;
        n1[1] /= norm1;
        n1[2] /= norm1;

        double[] n2 = MathTools.crossProd3x3(v, w);
        double norm2 = MathTools.norm3(n2);
        n2[0] /= norm2;
        n2[1] /= norm2;
        n2[2] /= norm2;

        /* TODO */

        return 0.0;
    }

}//end class

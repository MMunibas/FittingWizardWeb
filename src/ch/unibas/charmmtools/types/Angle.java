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
import static java.lang.Math.acos;

/**
 *
 * @author hedin
 */
public class Angle {

    Atom a1 = null;
    Atom a2 = null;
    Atom a3 = null;

    double angle = 0.0;
    boolean inRadian = false;

    public Angle(Atom _a1, Atom _a2, Atom _a3) {
        this.a1 = _a1;
        this.a2 = _a2;
        this.a3 = _a3;
        this.angle = calculateAngle(a1, a2, a3, inRadian);
    }

    public Angle(Atom _a1, Atom _a2, Atom _a3, double _angle) {
        this.a1 = _a1;
        this.a2 = _a2;
        this.a3 = _a3;
        this.angle = _angle;
    }

    public Angle(Atom _a1, Atom _a2, Atom _a3, boolean _in_radian) {
        this.a1 = _a1;
        this.a2 = _a2;
        this.a3 = _a3;
        if (_in_radian) {
            this.inRadian = true;
        }
        this.angle = calculateAngle(a1, a2, a3, inRadian);
    }

    public Angle(Atom _a1, Atom _a2, Atom _a3, double _angle, boolean _in_radian) {
        this.a1 = _a1;
        this.a2 = _a2;
        this.a3 = _a3;
        this.angle = _angle;
        if (_in_radian) {
            this.inRadian = true;
        }
    }

    public static double calculateAngle(Atom a1, Atom a2, Atom a3, boolean radian) {
        double[] u = new double[3];
        double[] v = new double[3];

        u[0] = a1.getX() - a2.getX();
        u[1] = a1.getY() - a2.getY();
        u[2] = a1.getZ() - a2.getZ();

        v[0] = a1.getX() - a3.getX();
        v[1] = a1.getY() - a3.getY();
        v[2] = a1.getZ() - a3.getZ();

        double dot_prod = MathTools.dotProd3x3(u, v);
        double uNorm = MathTools.norm3(u);
        double vNorm = MathTools.norm3(v);

        double angle = acos(dot_prod / (uNorm * vNorm));

        if (!radian) {
            angle *= 180.0 / Math.PI;
        }

        return angle;
    }



}//end class

/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.structures;

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
        double[] x = new double[2];
        double[] y = new double[2];
        double[] z = new double[2];

        x[0] = a1.getX() - a2.getX();
        y[0] = a1.getY() - a2.getY();
        z[0] = a1.getZ() - a2.getZ();

        x[1] = a3.getX() - a2.getX();
        y[1] = a3.getY() - a2.getY();
        z[1] = a3.getZ() - a2.getZ();

        double norm1 = Math.sqrt(x[0] * x[0] + y[0] * y[0] + z[0] * z[0]);
        double norm2 = Math.sqrt(x[1] * x[1] + y[1] * y[1] + z[1] * z[1]);
        double prod = x[0] * x[1] + y[0] * y[1] + z[0] * z[1];
        double angle = acos(prod / (norm1 * norm2));

        if (!radian) {
            angle *= 180.0 / Math.PI;
        }

        return angle;
    }

    /**
     * @return the a1
     */
    public Atom getA1() {
        return a1;
    }

    /**
     * @return the a2
     */
    public Atom getA2() {
        return a2;
    }

    /**
     * @return the a3
     */
    public Atom getA3() {
        return a3;
    }


}//end class

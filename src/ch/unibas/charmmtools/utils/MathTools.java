/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.utils;

import static java.lang.Math.sqrt;

/**
 *
 * @author hedin
 */
public abstract class MathTools {

    public static double norm3(double[] a) {
        if (a.length != 3) {
            throw new IllegalArgumentException("The dimensions have to be equal (3)!");
        }
        double norm = 0.0;
        norm = a[0] * a[0] + a[1] * a[1] + a[2] * a[2];
        return sqrt(norm);
    }

    public static double dotProd3x3(double[] a, double[] b) {
        if (a.length != 3 || b.length != 3) {
            throw new IllegalArgumentException("The dimensions have to be equal (3)!");
        }

        double sum = 0;

        sum += a[0] * b[0];
        sum += a[1] * b[1];
        sum += a[2] * b[2];

        return sum;
    }

    public static double[] crossProd3x3(double[] a, double[] b) {
        if (a.length != 3 || b.length != 3) {
            throw new IllegalArgumentException("The dimensions have to be equal (3)!");
        }

        double[] res = new double[3];

        res[0] = a[1] * b[2] - a[2] * b[1];
        res[1] = a[2] * b[0] - a[0] * b[2];
        res[2] = a[0] * b[1] - a[1] * b[0];

        return res;
    }

}//end class

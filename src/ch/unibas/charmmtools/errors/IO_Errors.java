/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.errors;

/**
 *
 * @author hedin
 */
public abstract class IO_Errors {

    public static void checkFortranIOerror(int fortcheck1, int fortcheck2, String trace) {
        if (fortcheck1 != fortcheck2) {
            System.err.println("Error when reading data from Fortran file : quantities do not match.");
            System.err.println("fortcheck1 = " + fortcheck1 + " and fortcheck2 = " + fortcheck2);
            System.err.println(trace);
            System.exit(-1);
        }
    }

    public static void checkFortranIOerror(int fortcheck1, int fortcheck2) {
        if (fortcheck1 != fortcheck2) {
            System.err.println("Error when reading data from Fortran file : quantities do not match.");
            System.err.println("fortcheck1 = " + fortcheck1 + " and fortcheck2 = " + fortcheck2);
            System.exit(-1);
        }
    }

}

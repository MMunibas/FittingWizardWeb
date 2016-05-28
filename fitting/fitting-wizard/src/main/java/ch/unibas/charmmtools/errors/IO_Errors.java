/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.errors;

/**
 * An abstract class for managing IO errors when reading a fortran binary file
 * 
 * When writing a binary record to a file, fortran adds before and after the record an int, containing
 * as value the size in bytes of the record.If for some reason those value differ there was a problem
 * when writing or reading.
 * 
 * @author hedin
 */
public abstract class IO_Errors {

    /**
     * Ensures that a given fortran record is valid.
     * 
     * @param fortcheck1 First integer to check
     * @param fortcheck2 Second integer to check
     * @param trace For writing an extra trace, obtained for example using CommonErrors.showTrace()
     */
    public static void checkFortranIOerror(int fortcheck1, int fortcheck2, String trace) {
        if (fortcheck1 != fortcheck2) {
            System.err.println("Error when reading data from Fortran file : quantities do not match.");
            System.err.println("fortcheck1 = " + fortcheck1 + " and fortcheck2 = " + fortcheck2);
            System.err.println(trace);
            System.exit(-1);
        }
    }

    /**
     * Ensures that a given fortran record is valid
     * 
     * @param fortcheck1 First integer to check
     * @param fortcheck2 Second integer to check
     */
    public static void checkFortranIOerror(int fortcheck1, int fortcheck2) {
        if (fortcheck1 != fortcheck2) {
            System.err.println("Error when reading data from Fortran file : quantities do not match.");
            System.err.println("fortcheck1 = " + fortcheck1 + " and fortcheck2 = " + fortcheck2);
            System.exit(-1);
        }
    }

}

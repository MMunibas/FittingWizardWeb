/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.exceptions;

/**
 *
 * @author hedin
 */
public class UnsupportedVELDException extends Exception {

    public UnsupportedVELDException() {
        System.err.println("CHARMM Velocity files (VELD) are not supported for the moment.");
    }

}

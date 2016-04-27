/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.exceptions;

/**
 * This class raises an Exception when trying to manipulate as DCD
 * a file which is instead a VCD (velocities instead of trajectories)
 * 
 * @author hedin
 */
public class UnsupportedVELDException extends Exception {

    private static final long serialVersionUID = 7933064051776191873L;

    /**
     * Prints error message
     */
    public UnsupportedVELDException() {
        System.err.println("CHARMM Velocity files (VELD) are not supported for the moment.");
    }

}

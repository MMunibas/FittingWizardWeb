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
public class NotPsfException extends Exception {

    public NotPsfException(String fname, String line) {
        System.err.println("This file does not contains the PSF key word on first line : "
                + fname);
        System.err.println("Parsed line was : " + line);
    }
}

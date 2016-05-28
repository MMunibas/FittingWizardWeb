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
 * This class raises an Exception when trying to read a PSF file
 * if the file is apparently not compatible.
 * 
 * @author hedin
 */
public class NotPsfException extends Exception {

    private static final long serialVersionUID = 519693123051566422L;

    /**
     * Print detailed error message
     * 
     * @param fname File name
     * @param line  The file line were error occurred
     */
    public NotPsfException(String fname, String line) {
        
        super("This file does not contains the PSF key word on first line : " +
                fname + " ; Parsed line was : " + line
        );

    }
}

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
 * This class raises an Exception when trying to manipulate as DCD a file which is apparently not
 * a DCD trajectory.
 * 
 * @author hedin
 */
public class UnknownFileTypeException extends Exception {

    private static final long serialVersionUID = 2239298133112592174L;

    /**
     * Prints error message
     */
    public UnknownFileTypeException() {
        super("This file is of an unknown type: is it really a DCD ?");
    }

}

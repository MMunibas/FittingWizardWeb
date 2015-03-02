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
public class UnknownFileTypeException extends Exception {

    public UnknownFileTypeException() {
        System.err.println("This file is of an unknown type: is it really a DCD ?");
    }

}

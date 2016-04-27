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
 * A general abstract class for tracing errors
 * 
 * @author hedin
 */
public abstract class CommonErrors {

    /**
     * Prints detailed information useful for debugging
     * 
     * @return Error string
     */
    public static String showTrace() {
        String str = "Trace: "
                + "file " + new Throwable().getStackTrace()[1].getFileName()
                + " class " + new Throwable().getStackTrace()[1].getClassName()
                + " method " + new Throwable().getStackTrace()[1].getMethodName()
                + " line " + new Throwable().getStackTrace()[1].getLineNumber();

        return str;
    }

}

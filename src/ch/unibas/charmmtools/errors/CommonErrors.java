/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
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
public abstract class CommonErrors {

    public static String showTrace() {
        String str = "Trace: "
                + "file " + new Throwable().getStackTrace()[1].getFileName()
                + " class " + new Throwable().getStackTrace()[1].getClassName()
                + " method " + new Throwable().getStackTrace()[1].getMethodName()
                + " line " + new Throwable().getStackTrace()[1].getLineNumber();

        return str;
    }

}

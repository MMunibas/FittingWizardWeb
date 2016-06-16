/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.workflows;

import java.io.File;

/**
 * User: mhelmer
 * Date: 16.12.13
 * Time: 10:01
 */

public class RunGaussianResult {

    public static final RunGaussianResult Success = new RunGaussianResult("Success", null);
    private static final String invalidLogFile = "InvalidLogFile";

    private final String type;
    private final File logFile;

    private RunGaussianResult(String type, File logFile) {
        this.type = type;
        this.logFile = logFile;
    }

    public static RunGaussianResult createInvalid(File logFile) {
        return new RunGaussianResult(invalidLogFile, logFile);
    }

    public File getLogFile() {
        return logFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RunGaussianResult that = (RunGaussianResult) o;

        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    public boolean isInvalid() {
        return type.equals(invalidLogFile);
    }
}

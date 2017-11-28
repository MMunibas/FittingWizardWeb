/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.workflows.gaussian;

import java.io.File;

/**
 * User: mhelmer
 * Date: 16.12.13
 * Time: 10:01
 */

public class RunGaussianResult {

    private final File logFile;

    public RunGaussianResult(File logFile) {
        this.logFile = logFile;
    }

    public File getLogFile() {
        return logFile;
    }
}

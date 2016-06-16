/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.fitmtp;

import java.io.File;

/**
 * User: mhelmer
 * Date: 10.12.13
 * Time: 19:37
 */
public class FitMtpOutput {
    private final File outputFile;
    private final File reusltsFile;

    public FitMtpOutput(File outputFile, File resultsFile) {
        this.outputFile = outputFile;
        this.reusltsFile = resultsFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public File getResultsFile() {
        return reusltsFile;
    }
}

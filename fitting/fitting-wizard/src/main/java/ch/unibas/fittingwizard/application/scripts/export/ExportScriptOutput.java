/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.scripts.export;

import java.io.File;

/**
 * User: mhelmer
 * Date: 13.12.13
 * Time: 15:12
 */
public class ExportScriptOutput {
    private final File exportFile;

    public ExportScriptOutput(File exportFile) {
        this.exportFile = exportFile;
    }

    public File getExportFile() {
        return exportFile;
    }
}

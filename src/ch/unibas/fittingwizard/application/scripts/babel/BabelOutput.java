/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.scripts.babel;

import java.io.File;

public class BabelOutput {

    private File outputFile;

	public BabelOutput(File sdfFile) {
        this.outputFile = sdfFile;
    }
	
	public File getSdfFile() {
		return outputFile;
	}
}

/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.scripts.babel;

import java.io.File;

public class BabelInput {

    private final File inputFile;

	public BabelInput(File gaussianLogFile) {
        this.inputFile = gaussianLogFile;
    }
	
	public File getGaussianLogFile() {
		return inputFile;
	}
	
}

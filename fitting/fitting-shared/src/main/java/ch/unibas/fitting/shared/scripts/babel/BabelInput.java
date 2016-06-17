/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.babel;

import ch.unibas.fitting.shared.directories.MoleculesDir;

import java.io.File;

public class BabelInput {

	private MoleculesDir moleculesDir;
	private final File inputFile;

	public BabelInput(MoleculesDir moleculesDir, File gaussianLogFile) {
		this.moleculesDir = moleculesDir;
		this.inputFile = gaussianLogFile;
    }
	
	public File getGaussianLogFile() {
		return inputFile;
	}

	public MoleculesDir getMoleculesDir() {
		return moleculesDir;
	}
}

/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.lra;

import ch.unibas.fitting.shared.directories.MoleculesDir;

import java.io.File;

public class LRAScriptInput {

	private MoleculesDir moleculesDir;
	private final File inputFile;

	public LRAScriptInput(MoleculesDir moleculesDir, File getSdfFile) {
		this.moleculesDir = moleculesDir;
		this.inputFile = getSdfFile;
    }
	
	public File getSdfFile() {
		return inputFile;
	}

	public MoleculesDir getMoleculesDir() {
		return moleculesDir;
	}
}

/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.fittab;

import ch.unibas.fitting.shared.directories.MoleculesDir;

import java.io.File;

public class FittabScriptInput {

	private MoleculesDir moleculesDir;
	private final File cubeFile;
	private final File vdwFile;
	private final File lpunFile;

	public FittabScriptInput(MoleculesDir moleculesDir,
							 File cubeFile,
							 File vdwFile,
							 File lpunFile) {
		this.moleculesDir = moleculesDir;
		this.cubeFile = cubeFile;
        this.vdwFile = vdwFile;
        this.lpunFile = lpunFile;
    }
	
	public File getCubeFile() {
		return cubeFile;
	}

	public File getVdwFile() {
		return vdwFile;
	}

	public File getLpunFile() {
		return lpunFile;
	}

	public MoleculesDir getMoleculesDir() {
		return moleculesDir;
	}
}

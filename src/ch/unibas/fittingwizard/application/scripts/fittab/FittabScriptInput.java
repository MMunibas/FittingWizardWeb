/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.scripts.fittab;

import java.io.File;

public class FittabScriptInput {

    private final File cubeFile;
	private final File vdwFile;
	private final File lpunFile;

	public FittabScriptInput(File cubeFile, File vdwFile, File lpunFile) {
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
	
}

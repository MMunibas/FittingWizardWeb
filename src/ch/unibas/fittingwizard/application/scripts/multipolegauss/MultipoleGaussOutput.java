/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.scripts.multipolegauss;

import java.io.File;

/**
 * User: mhelmer
 * Date: 28.11.13
 * Time: 14:30
 */
public class MultipoleGaussOutput {
    private final boolean logFileIsValid;
    private final File logFile;
	private final File punFile;
	private final File cubeFile;
	private final File vdwFile;

    public MultipoleGaussOutput(boolean logFileIsValid, File logFile, File punFile, File cubeFile, File vdwFile) {
        this.logFileIsValid = logFileIsValid;
        this.logFile = logFile;
        this.punFile = punFile;
        this.cubeFile = cubeFile;
        this.vdwFile = vdwFile;
    }

    public boolean isLogFileValid() {
        return logFileIsValid;
    }

    public File getLogFile() {
        return logFile;
    }

	public File getPunFile() {
		return punFile;
	}

	public File getCubeFile() {
		return cubeFile;
	}

	public File getVdwFile() {
		return vdwFile;
	}
}

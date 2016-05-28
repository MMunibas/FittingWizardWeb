/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.scripts.fittab;

import java.io.File;

public class FittabScriptOutput {

    private File mtpFile;

	public FittabScriptOutput(File mtpFile) {
        this.mtpFile = mtpFile;
    }
	
	public File getMtpFile() {
		return mtpFile;
	}
    
	
}

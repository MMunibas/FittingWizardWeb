/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.lra;

import java.io.File;

public class LRAScriptOutput {

    private File outputFile;

	public LRAScriptOutput(File lPunFile) {
        this.outputFile = lPunFile;
    }
	
	public File getLPunFile() {
		return outputFile;
	}
    
	
}

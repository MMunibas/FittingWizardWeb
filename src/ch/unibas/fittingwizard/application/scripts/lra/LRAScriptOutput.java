package ch.unibas.fittingwizard.application.scripts.lra;

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

package ch.scs.unibas.fittingwizard.application.scripts.lra;

import java.io.File;

public class LRAScriptInput {

    private final File inputFile;

	public LRAScriptInput(File getSdfFile) {
        this.inputFile = getSdfFile;
    }
	
	public File getSdfFile() {
		return inputFile;
	}
	
}

package ch.scs.unibas.fittingwizard.application.scripts.babel;

import java.io.File;

public class BabelOutput {

    private File outputFile;

	public BabelOutput(File sdfFile) {
        this.outputFile = sdfFile;
    }
	
	public File getSdfFile() {
		return outputFile;
	}
}

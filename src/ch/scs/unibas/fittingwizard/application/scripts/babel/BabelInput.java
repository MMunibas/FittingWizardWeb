package ch.scs.unibas.fittingwizard.application.scripts.babel;

import java.io.File;

public class BabelInput {

    private final File inputFile;

	public BabelInput(File gaussianLogFile) {
        this.inputFile = gaussianLogFile;
    }
	
	public File getGaussianLogFile() {
		return inputFile;
	}
	
}

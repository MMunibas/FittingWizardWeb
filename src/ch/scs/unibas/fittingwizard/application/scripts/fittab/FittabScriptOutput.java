package ch.scs.unibas.fittingwizard.application.scripts.fittab;

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

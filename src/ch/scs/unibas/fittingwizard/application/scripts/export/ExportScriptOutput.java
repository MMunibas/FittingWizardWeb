package ch.scs.unibas.fittingwizard.application.scripts.export;

import java.io.File;

/**
 * User: mhelmer
 * Date: 13.12.13
 * Time: 15:12
 */
public class ExportScriptOutput {
    private final File exportFile;

    public ExportScriptOutput(File exportFile) {
        this.exportFile = exportFile;
    }

    public File getExportFile() {
        return exportFile;
    }
}

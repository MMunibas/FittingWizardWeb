package ch.unibas.fitting.shared.workflows.ljfit;

import java.io.File;

public class UploadedFileNames {
    public final String parFile;
    public final String rtfFile;
    public final String molFile;
    public final String liquidFile;
    public final String solventFile;
    public final String lpunFile;

    public UploadedFileNames(
            File parFile,
            File rtfFile,
            File molFile,
            File liquidFile,
            File solventFile,
            File lpunFile) {
        this.parFile = parFile != null ? parFile.getName() : null;
        this.rtfFile = rtfFile != null ? rtfFile.getName() : null;
        this.molFile = molFile != null ? molFile.getName() : null;
        this.liquidFile = liquidFile != null ? liquidFile.getName() : null;
        this.solventFile = solventFile != null ? solventFile.getName() : null;
        this.lpunFile = lpunFile != null ? lpunFile.getName() : null;
    }

    public UploadedFileNames(
            String parFile,
            String rtfFile,
            String molFile,
            String liquidFile,
            String solventFile,
            String lpunFile) {
        this.parFile = parFile;
        this.rtfFile = rtfFile;
        this.molFile = molFile;
        this.liquidFile = liquidFile;
        this.solventFile = solventFile;
        this.lpunFile = lpunFile;
    }
}

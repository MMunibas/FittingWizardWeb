package ch.unibas.fitting.shared.workflows.ljfit;

import java.io.File;

public class UploadedFiles {
    public final File parFile;
    public final File rtfFile;
    public final File molFile;
    public final File liquidFile;
    public final File solventFile;
    public final File lpunFile;

    public UploadedFiles(
            File parFile,
            File rtfFile,
            File molFile,
            File liquidFile,
            File solventFile,
            File lpunFile) {
        this.parFile = parFile;
        this.rtfFile = rtfFile;
        this.molFile = molFile;
        this.liquidFile = liquidFile;
        this.solventFile = solventFile;
        this.lpunFile = lpunFile;
    }
}

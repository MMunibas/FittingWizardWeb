package ch.unibas.fitting.shared.workflows.charmm;

import ch.unibas.fitting.shared.workflows.ljfit.UploadedFileNames;

import java.io.File;

public class UploadedFiles {
    public final File parFile;
    public final File rtfFile;
    public final File molFile;
    public final File liquidFile;
    public final File solventFile;
    public final File lpunFile;

    public UploadedFiles(File baseDir, UploadedFileNames uploadedFileNames) {
        this.parFile = fileOrNull(baseDir, uploadedFileNames.parFile);
        this.rtfFile = fileOrNull(baseDir, uploadedFileNames.rtfFile);
        this.molFile = fileOrNull(baseDir, uploadedFileNames.molFile);
        this.liquidFile = fileOrNull(baseDir, uploadedFileNames.liquidFile);
        this.solventFile = fileOrNull(baseDir, uploadedFileNames.solventFile);
        this.lpunFile = fileOrNull(baseDir, uploadedFileNames.lpunFile);
    }

    private File fileOrNull(File baseDir, String name) {
        if (name == null || "".equals(name))
            return null;
        return new File(baseDir, name);
    }
}

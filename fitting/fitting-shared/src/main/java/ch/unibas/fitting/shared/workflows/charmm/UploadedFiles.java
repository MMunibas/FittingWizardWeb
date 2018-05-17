package ch.unibas.fitting.shared.workflows.charmm;

import ch.unibas.fitting.shared.workflows.ljfit.UploadedFileNames;

import java.io.File;
import java.util.ArrayList;

public class UploadedFiles {
    public final File parFile;
    public final File rtfFile;
    public final File molFile;
    public final File liquidFile;
    public final File solventFile;
    public final File lpunFile;
    public final File resFile;

    public UploadedFiles(File baseDir, UploadedFileNames uploadedFileNames) {
        this.parFile = fileOrNull(baseDir, uploadedFileNames.parFile);
        this.rtfFile = fileOrNull(baseDir, uploadedFileNames.rtfFile);
        this.molFile = fileOrNull(baseDir, uploadedFileNames.molFile);
        this.liquidFile = fileOrNull(baseDir, uploadedFileNames.liquidFile);
        this.solventFile = fileOrNull(baseDir, uploadedFileNames.solventFile);
        this.lpunFile = fileOrNull(baseDir, uploadedFileNames.lpunFile);
        this.resFile = fileOrNull(baseDir, uploadedFileNames.resFile);
    }

    private File fileOrNull(File baseDir, String name) {
        if (name == null || "".equals(name))
            return null;
        return new File(baseDir, name);
    }

    public File[] listFiles() {
        var array = new ArrayList<>();
        safeAdd(parFile, array);
        safeAdd(rtfFile, array);
        safeAdd(molFile, array);
        safeAdd(liquidFile, array);
        safeAdd(solventFile, array);
        safeAdd(lpunFile, array);
        safeAdd(resFile, array);
        return array.toArray(new File[0]);
    }

    private void safeAdd(File parFile, ArrayList<Object> array) {
        if (parFile != null || parFile.exists())
            array.add(parFile);
    }
}

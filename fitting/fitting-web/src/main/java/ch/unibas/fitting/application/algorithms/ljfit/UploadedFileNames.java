package ch.unibas.fitting.application.algorithms.ljfit;

import java.io.File;

public class UploadedFileNames {
    public final String parFile;
    public final String rtfFile;
    public final String molFile;
    public final String liquidFile;
    public final String lpunFile;
    public final String resFile;

    public UploadedFileNames(
            File parFile,
            File rtfFile,
            File molFile,
            File liquidFile,
            File lpunFile,
            File resFile) {
        this.parFile = nameOrNull(parFile);
        this.rtfFile = nameOrNull(rtfFile);
        this.molFile = nameOrNull(molFile);
        this.liquidFile = nameOrNull(liquidFile);
        this.lpunFile = nameOrNull(lpunFile);
        this.resFile = nameOrNull(resFile);
    }

    private String nameOrNull(File file) {
        return file != null ? file.getName() : null;
    }
}

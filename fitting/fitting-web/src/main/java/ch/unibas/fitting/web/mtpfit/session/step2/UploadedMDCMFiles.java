package ch.unibas.fitting.web.mtpfit.session.step2;

import java.io.File;

public class UploadedMDCMFiles {
    public final String axisFile;

    public UploadedMDCMFiles(
            File axisFile) {
        this.axisFile = nameOrNull(axisFile);
    }

    private String nameOrNull(File file) {
        return file != null ? file.getName() : null;
    }
}

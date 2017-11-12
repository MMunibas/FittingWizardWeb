package ch.unibas.fitting.web.ljfit.ui.step3;

import java.io.File;

public class FileViewModel {
    private final File file;
    private String groupName;

    public FileViewModel(File file) {
        this.file = file;
        this.groupName = file.getParentFile().getName();
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s",groupName, file.getName());
    }

    public String getGroupName() {
        return groupName;
    }
}

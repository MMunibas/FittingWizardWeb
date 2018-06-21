package ch.unibas.fitting.web.ljfit.ui.step3;

import ch.unibas.fitting.web.application.directories.FileWithTag;

import java.io.File;

public class FileViewModel {
    private final File file;
    private String groupName;

    public FileViewModel(FileWithTag file) {
        this.file = file.file;
        this.groupName = file.group;
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

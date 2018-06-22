package ch.unibas.fitting.application.directories;

import java.io.File;

public class FileWithTag {
    public final String group;
    public final File file;

    public FileWithTag(String group, File file) {
        this.group = group;
        this.file = file;
    }
}

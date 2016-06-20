package ch.unibas.fitting.shared.directories;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
public class XyzDirectory extends FittingDirectory {
    protected XyzDirectory(File directory) {
        super(directory);
    }

    public File getXyzFileFor(String name) {
        String withoutExt = FilenameUtils.removeExtension(name);
        return new File(getDirectory(), withoutExt + ".xyz");
    }

    public void deleteXyzFileFor(String moleculeName) {
        File xyz = new File(getDirectory(), moleculeName + ".xyz");
        if (xyz.exists())
            xyz.delete();
    }
}

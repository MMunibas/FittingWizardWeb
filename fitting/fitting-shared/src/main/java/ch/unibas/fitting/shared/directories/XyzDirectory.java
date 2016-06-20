package ch.unibas.fitting.shared.directories;

import ch.unibas.fitting.shared.xyz.XyzFile;
import ch.unibas.fitting.shared.xyz.XyzFileParser;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
public class XyzDirectory extends FittingDirectory {
    public XyzDirectory(File directory) {
        super(directory);
    }

    public File getXyzFileFor(String moleculeName) {
        String withoutExt = FilenameUtils.removeExtension(moleculeName);
        return new File(getDirectory(), withoutExt + ".xyz");
    }

    public void deleteXyzFileFor(String moleculeName) {
        File xyz = new File(getDirectory(), moleculeName + ".xyz");
        if (xyz.exists())
            xyz.delete();
    }

    public XyzFile getXyzFile(String moleculeName) {
        File f = getXyzFileFor(moleculeName);
        if (f == null)
            return null;
        return XyzFileParser.parse(f);
    }
}

package ch.unibas.fitting.shared.directories;

import ch.unibas.fitting.shared.directories.MoleculesDir;

import java.io.File;
import java.util.List;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public interface IUserDirectory {
    MoleculesDir getMoleculesDir(String username);
    FitOutputDir getFitOutputDir(String username);
    XyzDirectory getXyzDir(String username);

    File getLjfitInputFileName(String username, String name);

    List<String> listAllUserDirs();
}

package ch.unibas.fitting.shared.directories;

import ch.unibas.fitting.shared.directories.MoleculesDir;

import java.io.File;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public interface IUserDirectory {
    File getXyzFileFor(String username, String name);

    MoleculesDir getMoleculesDir(String username);

    File getLjfitInputFileName(String username, String name);

    void createUserdir(String username);
}

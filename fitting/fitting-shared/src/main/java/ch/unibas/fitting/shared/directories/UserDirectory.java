package ch.unibas.fitting.shared.directories;

import ch.unibas.fitting.shared.config.Settings;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
@Singleton
public class UserDirectory implements IUserDirectory {

    private static final Logger LOGGER = Logger.getLogger(UserDirectory.class);

    private File _rootDir;

    @Inject
    public UserDirectory(Settings config) {
        _rootDir = config.getDataDir();
        LOGGER.info("Using root dir " + _rootDir.getPath());
    }

    public void saveXyzFile(String username, String name, File xyz) {
        File dest = xyzFile(username, name);
        try {
            FileUtils.copyFile(xyz, dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File loadXyzFile(String username, String name) {
        File f  = xyzFile(username, name);
        if (!f.isFile())
            return null;
        return f;
    }

    private File xyzFile(String username, String name) {
        File xyzDir = xyzDir(username);
        return new File(xyzDir, name);
    }



    private File userSubDir(String username, String name) {
        return new File(userDir(username), name);
    }

    private File userDir(String username) {
        return new File(_rootDir, username);
    }

    @Override
    public File getXyzFileFor(String username, String name) {
        xyzDir(username).mkdirs();
        return xyzFile(username, name);
    }

    @Override
    public MoleculesDir getMoleculesDir(String username) {
        return new MoleculesDir(userSubDir(username, "molecules"));
    }

    private File xyzDir(String username) {
        return userSubDir(username, "xyz_files");
    }

    private File ljfitInputFile(String username, String name) {
        File ljfitInputDir = ljfitInputDir(username);
        return new File(ljfitInputDir, name);
    }

    @Override
    public File getLjfitInputFileName(String username, String name) {
        ljfitInputDir(username).mkdirs();
        return ljfitInputFile(username, name);
    }

    private File ljfitInputDir(String username) {
        return userSubDir(username, "ljfit_files");
    }

    @Override
    public void createUserdir(String username) {
        File f = userDir(username);
        f.mkdirs();
    }}

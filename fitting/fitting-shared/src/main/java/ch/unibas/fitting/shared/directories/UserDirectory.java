package ch.unibas.fitting.shared.directories;

import ch.unibas.fitting.shared.config.Settings;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
@Singleton
public class UserDirectory implements IUserDirectory {

    private static final Logger LOGGER = Logger.getLogger(UserDirectory.class);

    private File dataDir;

    @Inject
    public UserDirectory(Settings config) {
        dataDir = config.getDataDir();
        dataDir.mkdirs();
        LOGGER.info("Using root dir " + dataDir.getPath());
    }

    private File userSubDir(String username, String name) {
        return new File(userDir(username), name);
    }

    private File userDir(String username) {
        return new File(dataDir, username);
    }

    @Override
    public MoleculesDir getMoleculesDir(String username) {
        return new MoleculesDir(username, userSubDir(username, "molecules"));
    }

    @Override
    public FitOutputDir getFitOutputDir(String username) {
        return new FitOutputDir(username, userSubDir(username, "fit_mtp"));
    }

    @Override
    public XyzDirectory getXyzDir(String username) {
        return new XyzDirectory(username, userSubDir(username, "xyz_files"));
    }

    @Override
    public LjFitSessionDir getLjFitSessionDir(String username) {
        return new LjFitSessionDir(username, getLjFitSessionFile(username));
    }

    @Override
    public List<String> listAllUserDirs() {
        return Arrays.stream(dataDir.listFiles(File::isDirectory))
                .map(file -> file.getName())
                .collect(Collectors.toList());
    }

    @Override
    public boolean ljFitSessionDirectoryExists(String username) {
        return getLjFitSessionFile(username).exists();
    }

    @Override
    public void deleteLjFitSession(String username) {
        File f = getLjFitSessionFile(username);
        if (f.exists())
            f.delete();
    }

    private File getLjFitSessionFile(String username) {
        return userSubDir(username, "ljfit");
    }
}

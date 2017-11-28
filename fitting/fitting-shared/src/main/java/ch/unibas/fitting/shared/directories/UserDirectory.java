package ch.unibas.fitting.shared.directories;

import ch.unibas.fitting.shared.config.Settings;
import io.vavr.control.Option;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
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

    private File getMtpFitDirFile(String username) {
        return userSubDir(username, "mtp_fit");
    }

    @Override
    public MtpFitDir getMtpFitDir(String username) {
        return new MtpFitDir(username, getMtpFitDirFile(username));
    }

    @Override
    public LjFitSessionDir createLjFitSessionDir(String username) {
        File f = getLjFitSessionFile(username);
        f.mkdirs();
        return new LjFitSessionDir(username, f);
    }

    @Override
    public Option<LjFitSessionDir> getLjFitSessionDir(String username) {
        if (!ljFitSessionDirectoryExists(username))
            return Option.none();
        return Option.of(new LjFitSessionDir(username, getLjFitSessionFile(username)));
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
        try {
            FileUtils.deleteDirectory(f);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete " + f, e);
        }
    }

    @Override
    public void deleteMtpFitDir(String username) {
        File f = getMtpFitDirFile(username);
        try {
            FileUtils.deleteDirectory(f);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete " + f, e);
        }
    }

    private File getLjFitSessionFile(String username) {
        return userSubDir(username, "lj_fit");
    }
}

package ch.unibas.fitting.application.directories;

import ch.unibas.fitting.Settings;
import ch.unibas.fitting.infrastructure.JsonSerializer;
import io.vavr.control.Option;
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

    private File dataDir;
    private JsonSerializer serializer;

    @Inject
    public UserDirectory(Settings config, JsonSerializer serializer) {
        dataDir = config.getDataDir();
        this.serializer = serializer;
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
        return new MtpFitDir(username, getMtpFitDirFile(username), serializer);
    }

    @Override
    public LjFitSessionDir createLjFitSessionDir(String username) {
        File f = getLjFitSessionFile(username);
        f.mkdirs();
        return new LjFitSessionDir(username, f, serializer);
    }

    @Override
    public Option<LjFitSessionDir> getLjFitSessionDir(String username) {
        if (!ljFitSessionDirectoryExists(username))
            return Option.none();
        return Option.of(new LjFitSessionDir(username, getLjFitSessionFile(username), serializer));
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
    public File getUserBaseDir(String username) {
        return userDir(username);
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

package ch.unibas.fitting.web.ljfit.services;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.directories.LjFitSessionDir;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitSession;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

@Singleton
public class LjFitRepository {

    @Inject
    private IUserDirectory userDirectory;

    public synchronized Optional<LjFitSession> loadSessionForUser(String username) {
        LjFitSessionDir sessionDir = userDirectory.getLjFitSessionDir(username);
        if (!sessionDir.exists()) return Optional.empty();

        File f = sessionDir.getSessionJsonFile();
        try {
            String json = FileUtils.readFileToString(f, Charset.defaultCharset());
            Gson gson = new Gson();
            LjFitSession session = gson.fromJson(json, LjFitSession.class);

            return Optional.of(session);
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + f);
        }
    }

    public synchronized void save(String username, LjFitSession session) {
        LjFitSessionDir sessionDir = userDirectory.getLjFitSessionDir(username);
        if (sessionDir.exists()) {
            // TODO write to json file...
            Gson gson = new Gson();
            String json = gson.toJson(session);

            File f = new File(sessionDir.getDirectory(), "session.json");

            try {
                FileUtils.write(f, json, Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException("save json file failed " + f, e);
            }
        }
    }

    public synchronized void deleteSession(String username) {
        userDirectory.deleteLjFitSession(username);
    }

    public synchronized boolean sessionExists(String username) {
        return userDirectory.ljFitSessionDirectoryExists(username);
    }
}

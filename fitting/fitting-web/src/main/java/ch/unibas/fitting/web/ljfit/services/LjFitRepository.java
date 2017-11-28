package ch.unibas.fitting.web.ljfit.services;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.directories.LjFitSessionDir;
import ch.unibas.fitting.shared.infrastructure.JsonSerializer;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitRun;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitRunInput;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitRunResult;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitSession;
import com.google.gson.Gson;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@Singleton
public class LjFitRepository {

    @Inject
    private IUserDirectory userDirectory;
    @Inject
    private JsonSerializer serializer;

    public synchronized List<LjFitRun> listRuns(String username) {
        Option<LjFitSessionDir> dir = userDirectory.getLjFitSessionDir(username);
        if (dir.isEmpty())
            return List.empty();

        return dir.get()
                .listRunDirs()
                .map(runDir -> new LjFitRun(
                        runDir.getUsername(),
                        runDir.getDirectory().getName(),
                        runDir.getCreated(),
                        serializer.readJsonFile(runDir.getRunInputJson(), LjFitRunInput.class),
                        serializer.readJsonFile(runDir.getRunOutputJson(), LjFitRunResult.class)
                ))
                .toList();
    }

    public synchronized Option<LjFitSession> loadSessionForUser(String username) {
        return userDirectory.getLjFitSessionDir(username)
            .map(dir -> serializer.readJsonFile(dir.getSessionJsonFile(), LjFitSession.class).get());
    }

    public synchronized void save(String username, LjFitSession session) {
        userDirectory.getLjFitSessionDir(username)
                .map(dir -> {
                    serializer.writeJsonFile(
                            new File(dir.getDirectory(), "session.json"),
                            session);
                    return null;
                });
    }

    public synchronized boolean sessionExists(String username) {
        return userDirectory.ljFitSessionDirectoryExists(username);
    }

    public synchronized void deleteRunDir(String username, String runDir) {
        userDirectory.getLjFitSessionDir(username)
                .map(dir -> {
                    dir.deleteRunDir(runDir);
                    return null;
                });
    }
}

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
                        readJsonFile(runDir.getRunInputJson(), LjFitRunInput.class),
                        readJsonFile(runDir.getRunOutputJson(), LjFitRunResult.class)
                ))
                .toList();
    }

    public synchronized Option<LjFitSession> loadSessionForUser(String username) {
        return userDirectory.getLjFitSessionDir(username)
            .map(dir -> readJsonFile(dir.getSessionJsonFile(), LjFitSession.class).get());
    }

    public synchronized void save(String username, LjFitSession session) {
        userDirectory.getLjFitSessionDir(username)
                .map(dir -> {
                    writeJsonFile(
                            new File(dir.getDirectory(), "session.json"),
                            session);
                    return null;
                });
    }

    public synchronized void deleteSession(String username) {
        userDirectory.deleteLjFitSession(username);
    }

    public synchronized boolean sessionExists(String username) {
        return userDirectory.ljFitSessionDirectoryExists(username);
    }

    private void writeJsonFile(File file, Object src) {
        String json = serializer.toJson(src);
        try {
            FileUtils.write(file, json, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("save json file failed " + file, e);
        }
    }

    public <T> Option<T> readJsonFile(File file, Class<T> classOfT) {
        if (!file.isFile())
            return Option.none();
        try {
            String content = FileUtils.readFileToString(file, Charset.defaultCharset());
            T result = serializer.fromJson(content, classOfT);
            return Option.of(result);
        } catch (Exception e) {
            throw new RuntimeException("Could deserialize file " + file);
        }
    }

    public void deleteRunDir(String username, String runDir) {
        userDirectory.getLjFitSessionDir(username)
                .map(dir -> {
                    dir.deleteRunDir(runDir);
                    return null;
                });
    }
}

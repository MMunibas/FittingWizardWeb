package ch.unibas.fitting.web.ljfit.services;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.directories.LjFitSessionDir;
import ch.unibas.fitting.shared.infrastructure.JsonSerializer;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitRun;
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

        List<File> jsons = dir.get().listAllRunJsons();

        return jsons
                .map(file -> {
                    try {
                        return FileUtils.readFileToString(file, Charset.defaultCharset());
                    } catch (IOException e) {
                        throw new RuntimeException("Could not load file " + file);
                    }
                })
                .map(r -> fromJson(r, LjFitRun.class))
                .toList();
    }

    public synchronized Option<LjFitSession> loadSessionForUser(String username) {
        Option<LjFitSessionDir> dir = userDirectory.getLjFitSessionDir(username);
        if (dir.isEmpty())
            return Option.none();

        File f = dir.get().getSessionJsonFile();
        try {
            String json = FileUtils.readFileToString(f, Charset.defaultCharset());
            Gson gson = new Gson();
            LjFitSession session = gson.fromJson(json, LjFitSession.class);

            return Option.of(session);
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + f);
        }
    }

    public synchronized void save(String username, LjFitSession session) {
        userDirectory.getLjFitSessionDir(username)
                .map(dir -> {
                    String json = toJson(session);

                    File f = new File(dir.getDirectory(), "session.json");

                    try {
                        FileUtils.write(f, json, Charset.defaultCharset());
                    } catch (IOException e) {
                        throw new RuntimeException("save json file failed " + f, e);
                    }
                    return null;
                });
    }

    public synchronized void deleteSession(String username) {
        userDirectory.deleteLjFitSession(username);
    }

    public synchronized boolean sessionExists(String username) {
        return userDirectory.ljFitSessionDirectoryExists(username);
    }

    private String toJson(Object src) {
        return serializer.toJson(src);
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        return serializer.fromJson(json, classOfT);
    }

}

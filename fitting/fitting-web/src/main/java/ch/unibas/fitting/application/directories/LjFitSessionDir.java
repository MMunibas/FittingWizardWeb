package ch.unibas.fitting.application.directories;

import ch.unibas.fitting.infrastructure.JsonSerializer;
import ch.unibas.fitting.application.algorithms.ljfit.UploadedFileNames;
import ch.unibas.fitting.application.algorithms.ljfit.UploadedFiles;
import io.vavr.Tuple3;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class LjFitSessionDir extends FittingDirectory {

    public static final String Regex = "eps(\\d+\\.\\d+)_sig(\\d+\\.\\d+)_(\\d+)";

    private final JsonSerializer serializer;

    public LjFitSessionDir(String username, File directory, JsonSerializer serializer) {
        super(username, directory);
        this.serializer = serializer;
    }

    public File getSessionJsonFile() {return  new File(getDirectory(), "session.json");}

    public LjFitRunDir createRunDir(double lambda_sigma, double lambda_epsilon) {
        long unixTime = Instant.now().getEpochSecond();

        String runDirName = String.format("eps%.2f_sig%.2f_%d", lambda_epsilon, lambda_sigma, unixTime);

        File runDir = new File(getBaseRunDir(), runDirName);
        return new LjFitRunDir(username, runDir);
    }

    public Option<LjFitRunDir> getRunDir(String dirName) {
        File runDir = new File(getBaseRunDir(), dirName);
        if (!runDir.isDirectory())
            return Option.none();
        return Option.of(new LjFitRunDir(username, runDir));
    }

    public File getUploadDir() {
        return createAndGet(getDirectory(), "uploaded");
    }

    private File getBaseRunDir() {
        return createAndGet(getDirectory(), "runs");
    }

    public List<LjFitRunDir> listRunDirs() {
        return Stream.ofAll(Arrays.stream(getBaseRunDir().listFiles((dir, name) -> dir.isDirectory() && name.startsWith("eps"))))
                .map(file -> new LjFitRunDir(username, file))
                .toList();
    }

    public UploadedFiles lookupUploadedFiles(UploadedFileNames uploadedFileNames) {
        return new UploadedFiles(getUploadDir(), uploadedFileNames);
    }

    public static Tuple3<Double, Double, Long> parseDirName(String name) {
        Matcher m = Pattern.compile(Regex).matcher(name);
        m.matches();
        Double eps = Double.valueOf(m.group(1));
        Double sigma = Double.valueOf(m.group(2));
        Long time = Long.valueOf(m.group(3));
        return new Tuple3<>(eps, sigma, time);
    }

    public List<FileWithTag> listGeneratedRunFiles(String run) {
        return getRunDir(run)
            .map(dir -> {
                return List.ofAll(listFiles("", dir.getDirectory(), null));
            })
            .getOrElse(() -> List.empty());
    }

    private List<FileWithTag> listFiles(String group, File dir, String[] filter) {
        return Stream.ofAll(FileUtils.listFiles(dir, filter, true))
                .map(f -> {
                    String relative = dir.toURI().relativize(f.toURI()).getPath();
                    return new FileWithTag(relative, f);
                })
                .toList();
    }

    public void deleteRunDir(String runDirName) {
        File runDir = new File(getBaseRunDir(), runDirName);
        try {
            FileUtils.deleteDirectory(runDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete " + runDir);
        }
    }
}

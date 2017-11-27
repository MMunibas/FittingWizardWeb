package ch.unibas.fitting.shared.directories;

import ch.unibas.fitting.shared.workflows.charmm.UploadedFiles;
import ch.unibas.fitting.shared.workflows.ljfit.UploadedFileNames;
import io.vavr.Tuple3;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
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

    public LjFitSessionDir(String username, File directory) {
        super(username, directory);
    }

    public File getSessionJsonFile() {return  new File(getDirectory(), "session.json");}

    public LjFitRunDir createRunDir(double lambda_sigma, double lambda_epsilon) {
        long unixTime = Instant.now().getEpochSecond();

        String runDirName = String.format("eps%.2f_sig%.2f_%d", lambda_epsilon, lambda_sigma, unixTime);

        File runDir = new File(getBaseRunDir(), runDirName);

        return new LjFitRunDir(username, runDir);
    }

    public File getUploadDir() {
        return createAndGet(getDirectory(), "uploaded");
    }

    private File getBaseRunDir() {
        return createAndGet(getDirectory(), "runs");
    }

    public List<LjFitRunDir> listRunDirs() {
        return Stream.ofAll(Arrays.stream(getBaseRunDir().listFiles((dir, name) -> dir.isDirectory() && name.startsWith("eps"))))
                .map(file -> {
                    return new LjFitRunDir(username, file, parseDirName(file.getName())._3);
                })
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

    public List<File> listGeneratedRunFiles(String run) {
        File runDir = new File(getBaseRunDir(), run);
        if (!runDir.isDirectory())
            return List.empty();

        return Stream.ofAll(FileUtils.listFiles(runDir, new String[] {"inp", "out", "par"}, true))
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

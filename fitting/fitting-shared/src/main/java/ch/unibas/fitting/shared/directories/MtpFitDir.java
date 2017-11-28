package ch.unibas.fitting.shared.directories;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MtpFitDir extends FittingDirectory {
    protected MtpFitDir(String username, File directory) {
        super(username, directory);
    }

    private File getFitsDirFile() {
         return createSubDir("fits");
    }

    private File getMoleculeDirFile() {
        return createSubDir("molecule");
    }

    public int getNextFitId() {
        return Stream.ofAll(Arrays.asList(getFitsDirFile().listFiles()))
                .filter(file -> StringUtils.isNumeric(file.getName()))
                .map(file -> Integer.parseInt(file.getName()))
                .max()
                .map(integer -> integer + 1)
                .getOrElse(0);
    }

    public Option<FitOutputDir> getFitOutputDir(int index) {
        File fit = new File(getFitsDirFile(), String.valueOf(index));
        if (!fit.exists())
            return Option.none();

        return Option.of(new FitOutputDir(username, fit, index));
    }

    public Option<File> getLpunFile(int index) {
        return getFitOutputDir(index)
                .flatMap(d -> d.getLpunFile());
    }

    public void removeFitResult(int index) {
        getFitOutputDir(index)
                .peek(dir -> {
                    File f = dir.getDirectory();
                    LOGGER.debug("Removing fit result " + f.getName());
                    try {
                        FileUtils.deleteDirectory(f);
                    } catch (IOException e) {
                        throw new RuntimeException(
                                "failed to delete fit direcotry " + f.getAbsolutePath(),
                                e);
                    }
                });
    }

    public FitOutputDir createNextFitOutputDir() {
        int next = getNextFitId();
        File fit = new File(getFitsDirFile(), String.valueOf(next));
        fit.mkdirs();
        return new FitOutputDir(username, fit, next);
    }

    public MoleculesDir getMoleculeDir() {
        return new MoleculesDir(username, getMoleculeDirFile());
    }

    public List<File> listAllFitResultFiles() {
        return Stream.ofAll(listAllFitDirs())
                .flatMap(file -> List.ofAll(Arrays.asList(file.listFiles((dir, name) -> name.equals("result.json")))))
                .toList();
    }

    public List<File> listAllFitDirs() {
        return Stream.ofAll(Arrays.stream(getFitsDirFile().listFiles()))
                .filter(file -> file.isDirectory() && StringUtils.isNumeric(file.getName()))
                .toList();
    }

    public File getFitResultFile(int index) {
        File dir = new File(getFitsDirFile(), String.valueOf(index));
        return new File(dir, "result.json");
    }

    public boolean sessionExists() {
        String anyName = getMoleculeDir().getAnyMoleculeName();
        if (anyName == null)
            return false;

        File f = getMoleculeDir().getUserChargesFile(anyName);
        return f.exists();
    }
}

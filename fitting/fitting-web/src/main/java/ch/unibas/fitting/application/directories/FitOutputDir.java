package ch.unibas.fitting.application.directories;

import io.vavr.control.Option;

import java.io.File;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
public class FitOutputDir extends FittingDirectory {

    private final int id;

    public FitOutputDir(String username, File directory, int id) {
        super(username, directory);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Option<File> getLpunFile() {

        for (File f: getDirectory().listFiles()) {
            if (f.getName().endsWith(".dcm")) {
                return Option.of(f);
            }
        }
        return Option.none();
    }
}

package ch.unibas.fitting.shared.directories;

import java.io.File;
import java.io.ObjectInputStream;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class CharmmGeneratedInputOutputDir extends FittingDirectory {
    public CharmmGeneratedInputOutputDir(File directory) {
        super(directory);
    }

    public File getGasDir() {
        return createAndGet(getDirectory(), "gas");
    }

    public File getSolvDir() {
        return createAndGet(getDirectory(), "solv");
    }

    public File getGasVdwDir() {
        return createAndGet(getGasDir(), "vdw");
    }

    public File getGasMtpDir() {
        return createAndGet(getGasDir(), "mtp");
    }

    public File getSolvVdwDir() {
        return createAndGet(getSolvDir(), "vdw");
    }

    public File getSolvMtpDir() {
        return createAndGet(getSolvDir(), "mtp");
    }
}

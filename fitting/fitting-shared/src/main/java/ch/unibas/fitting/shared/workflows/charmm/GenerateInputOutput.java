package ch.unibas.fitting.shared.workflows.charmm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class GenerateInputOutput {
    private File gasInput;
    private File solvInput;
    private List<File> gasVdw;
    private List<File> gasMtp;
    private List<File> solvVdw;
    private List<File> solvMtp;

    public GenerateInputOutput(File gasInput, File solvInput, List<File> gasVdw, List<File> gasMtp, List<File> solvVdw, List<File> solvMtp) {
        this.gasInput = gasInput;
        this.solvInput = solvInput;
        this.gasVdw = gasVdw;
        this.gasMtp = gasMtp;
        this.solvVdw = solvVdw;
        this.solvMtp = solvMtp;
    }

    public File getGasInput() {
        return gasInput;
    }

    public File getSolvInput() {
        return solvInput;
    }

    public List<File> getGasVdw() {
        return gasVdw;
    }

    public List<File> getGasMtp() {
        return gasMtp;
    }

    public List<File> getSolvVdw() {
        return solvVdw;
    }

    public List<File> getSolvMtp() {
        return solvMtp;
    }
}

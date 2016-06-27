package ch.unibas.fitting.shared.workflows.charmm;

import ch.unibas.fitting.shared.directories.CharmmOutputDir;

import java.io.File;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class GenerateInputWorkflowInput {
    private final CharmmOutputDir charmmOutputDir;
    private File parFile;
    private File rtfFile;
    private File molFile;
    private File liquidFile;
    private File solventFile;
    private File lpunFile;
    private double lambda;

    public GenerateInputWorkflowInput(CharmmOutputDir charmmOutputDir,
                                      File parFile,
                                      File rtfFile,
                                      File molFile,
                                      File liquidFile,
                                      File solventFile,
                                      File lpunFile,
                                      double lambda) {
        this.charmmOutputDir = charmmOutputDir;

        this.parFile = parFile;
        this.rtfFile = rtfFile;
        this.molFile = molFile;
        this.liquidFile = liquidFile;
        this.solventFile = solventFile;
        this.lpunFile = lpunFile;
        this.lambda = lambda;
    }

    public CharmmOutputDir getCharmmOutputDir() {
        return charmmOutputDir;
    }

    public File getParFile() {
        return parFile;
    }

    public File getRtfFile() {
        return rtfFile;
    }

    public File getMolFile() {
        return molFile;
    }

    public File getLiquidFile() {
        return liquidFile;
    }

    public File getSolventFile() {
        return solventFile;
    }

    public File getLpunFile() {
        return lpunFile;
    }

    public double getLambda() {
        return lambda;
    }
}

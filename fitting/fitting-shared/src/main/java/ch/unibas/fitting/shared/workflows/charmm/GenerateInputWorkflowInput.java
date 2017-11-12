package ch.unibas.fitting.shared.workflows.charmm;

import ch.unibas.fitting.shared.directories.LjFitRunDir;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitRunInput;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class GenerateInputWorkflowInput {
    public final LjFitRunDir runDir;
    public final LjFitRunInput runInput;
    public final UploadedFiles uploadedFiles;

    public GenerateInputWorkflowInput(LjFitRunDir runDir, LjFitRunInput runInput, UploadedFiles uploadedFiles) {
        this.runDir = runDir;
        this.runInput = runInput;
        this.uploadedFiles = uploadedFiles;
    }
}

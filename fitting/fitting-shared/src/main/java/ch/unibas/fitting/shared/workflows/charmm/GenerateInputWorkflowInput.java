package ch.unibas.fitting.shared.workflows.charmm;

import ch.unibas.fitting.shared.directories.LjFitSessionDir;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitSession;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class GenerateInputWorkflowInput {
    public final LjFitSessionDir ljFitSessionDir;
    public final LjFitSession ljFitSession;

    public GenerateInputWorkflowInput(
            LjFitSessionDir ljFitSessionDir,
            LjFitSession ljFitSession) {
        this.ljFitSessionDir = ljFitSessionDir;
        this.ljFitSession = ljFitSession;
    }
}

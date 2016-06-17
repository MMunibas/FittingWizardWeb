package ch.unibas.fitting.shared.workflows.gaussian;

import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussInput;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public interface GaussianWorkflow {
    RunGaussianResult execute(WorkflowContext<MultipoleGaussInput> status);
}

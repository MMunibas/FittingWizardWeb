package ch.unibas.fitting.web.gaussian.addmolecule.step4;

import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussInput;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import ch.unibas.fitting.shared.workflows.gaussian.GaussianWorkflow;
import ch.unibas.fitting.shared.workflows.gaussian.RunGaussianResult;
import ch.unibas.fitting.web.application.IAmAUsercase;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class RunGaussian implements IAmAUsercase {
    @Inject
    private IBackgroundTasks tasks;
    @Inject
    private GaussianWorkflow workflow;

    public UUID runGaussian(String username, MultipoleGaussInput input) {
        TaskHandle th = tasks.execute(username, "Multiple Gaussian MEP", () -> {
            Thread.sleep(1000);
            RunGaussianResult result = workflow.execute(WorkflowContext.withInput(input));
            return result;
        });
        return th.getId();
    }
}

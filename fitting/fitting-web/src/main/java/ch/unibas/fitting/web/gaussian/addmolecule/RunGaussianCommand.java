package ch.unibas.fitting.web.gaussian.addmolecule;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussInput;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import ch.unibas.fitting.shared.workflows.gaussian.GaussianWorkflow;
import ch.unibas.fitting.shared.workflows.gaussian.RunGaussianResult;
import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
import ch.unibas.fitting.web.gaussian.addmolecule.step6.AtomTypesPage;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class RunGaussianCommand implements IAmACommand {
    @Inject
    private IBackgroundTasks tasks;
    @Inject
    private GaussianWorkflow workflow;
    @Inject
    private MoleculeUserRepo moleculeUserRepo;
    @Inject
    private IUserDirectory userDir;

    public UUID runGaussian(String username,
                            String moleculeName,
                            Integer netCharge,
                            String quantum,
                            Integer nCores,
                            Integer multiplicity) {

        final MultipoleGaussInput input = new MultipoleGaussInput(
                userDir.getMoleculesDir(username),
                userDir.getXyzDir(username),
                moleculeName,
                netCharge,
                quantum,
                nCores,
                multiplicity);

        TaskHandle th = tasks.execute(username, "Multiple Gaussian MEP", () -> {
            Thread.sleep(1000);
            RunGaussianResult result = workflow.execute(WorkflowContext.withInput(input));
            moleculeUserRepo.save(username, result.getMolecule());
            return result;
        }, (runGaussianResult, pageParameters) -> {
            pageParameters.add("molecule_name", runGaussianResult.getMolecule().getId().getName());
            return AtomTypesPage.class;
        });
        return th.getId();
    }
}

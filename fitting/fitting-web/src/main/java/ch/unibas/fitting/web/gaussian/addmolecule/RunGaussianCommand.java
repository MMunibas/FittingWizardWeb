package ch.unibas.fitting.web.gaussian.addmolecule;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussInput;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import ch.unibas.fitting.shared.workflows.gaussian.RunGaussianResult;
import ch.unibas.fitting.shared.workflows.gaussian.RunGaussianWorkflow;
import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
import ch.unibas.fitting.web.gaussian.addmolecule.step4.ParameterPage;
import ch.unibas.fitting.web.gaussian.addmolecule.step6.AtomTypesPage;
import ch.unibas.fitting.web.web.PageNavigation;

import javax.inject.Inject;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */

public class RunGaussianCommand implements IAmACommand {
    @Inject
    private IBackgroundTasks tasks;
    @Inject
    private RunGaussianWorkflow workflow;
    @Inject
    private MoleculeUserRepo moleculeUserRepo;
    @Inject
    private IUserDirectory userDir;

    public void execute(String username,
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

        TaskHandle th = tasks.spawnTask(
                username,
                "Gaussian MEP",
                (ctx) -> {
                    RunGaussianResult result = workflow.execute(WorkflowContext.withInput(input));
                    moleculeUserRepo.save(username, result.getMolecule());
                    return result;
                },
                (runGaussianResult, pageParameters) -> {
                    pageParameters.add("molecule_name", runGaussianResult.getMolecule().getId().getName());
                    return AtomTypesPage.class;
                },
                ParameterPage.class);

        PageNavigation.ToProgressForTask(th);
    }
}

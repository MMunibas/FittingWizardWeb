package ch.unibas.fitting.web.gaussian.addmolecule;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.directories.MtpFitDir;
import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussInput;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import ch.unibas.fitting.shared.workflows.gaussian.RunGaussianResult;
import ch.unibas.fitting.shared.workflows.gaussian.RunGaussianWorkflow;
import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.PageContext;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.web.calculation.management.CalculationManagementClient;
import ch.unibas.fitting.web.calculation.management.execution.messages.StartDefinition;
import ch.unibas.fitting.web.gaussian.addmolecule.step4.ParameterPage;
import ch.unibas.fitting.web.gaussian.addmolecule.step6.AtomTypesPage;
import ch.unibas.fitting.web.gaussian.services.MtpFitSessionRepository;
import ch.unibas.fitting.web.web.PageNavigation;
import io.vavr.control.Option;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */

public class RunMtpGenerateFilesCommand implements IAmACommand {
    @Inject
    private IBackgroundTasks tasks;
    @Inject
    private RunGaussianWorkflow workflow;
    @Inject
    private IUserDirectory userDir;

    @Inject
    private CalculationManagementClient client;

    public void execute(String username,
                        String moleculeName,
                        PageContext pageContext,
                        Integer netCharge,
                        String quantum,
                        Integer nCores,
                        Integer multiplicity) {

        MtpFitDir mtpFitDir = userDir.getMtpFitDir(username);

        final MultipoleGaussInput input = new MultipoleGaussInput(
                mtpFitDir,
                moleculeName,
                netCharge,
                quantum,
                nCores,
                multiplicity);

        TaskHandle th = tasks.spawnTask(
                username,
                "Gaussian MEP",
                (ctx) -> {
                    workflow.execute(WorkflowContext.withInput(input));
                    return null;
                },
                (runGaussianResult, pageParameters) -> {
                    pageParameters.add("molecule_name", moleculeName);
                    return AtomTypesPage.class;
                },
                ParameterPage.class,
                Option.of(pageContext));

        PageNavigation.ToProgressForTask(th);
    }

    public void executeNew(String username,
                           String moleculeName,
                           PageContext pageContext,
                           Integer netCharge,
                           String quantum,
                           Integer nCores,
                           Integer multiplicity) {

        var params = new HashMap<String, Object>();

        var response = client.spawnTask(
                "Generate MTP files",
                username,
                new NavigationInfo(),
                new StartDefinition(
                        "mtp-part1",
                        params,
                        new File("output"),
                        new File("input")
                )
        );

        PageNavigation.ToProgressForCalculation(response);
    }
}

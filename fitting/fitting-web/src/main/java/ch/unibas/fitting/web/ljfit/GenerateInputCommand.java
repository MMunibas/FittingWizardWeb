package ch.unibas.fitting.web.ljfit;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.CharmmOutputDir;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import ch.unibas.fitting.shared.workflows.charmm.GenerateInputWorkflowInput;
import ch.unibas.fitting.shared.workflows.charmm.CharmmInputContainer;
import ch.unibas.fitting.shared.workflows.charmm.IGenerateInputWorkflow;
import ch.unibas.fitting.shared.workflows.charmm.MockGenerateInputWorkflow;
import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.ljfit.CharmmRepository;
import ch.unibas.fitting.web.ljfit.step2.ShowGeneratedInputPage;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.util.UUID;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class GenerateInputCommand implements IAmACommand {

    private Logger LOGGER = org.apache.log4j.Logger.getLogger(this.getClass());

    @Inject
    private IBackgroundTasks tasks;
    @Inject
    private IUserDirectory userDirectory;
    @Inject
    private CharmmRepository charmmRepository;
    @Inject
    private IGenerateInputWorkflow workflow;

    public UUID generateInput(String username, File parFile, File rtfFile, File molFile,
                              File liquidFile, File solventFile, File lpunFile, Double lambda) {

        LOGGER.debug("GenerateInputCommand generateInput");
        CharmmOutputDir charmmDir = userDirectory.getCharmmOutputDir(username);

        GenerateInputWorkflowInput input = new GenerateInputWorkflowInput(
                charmmDir,
                parFile,
                rtfFile,
                molFile,
                liquidFile,
                solventFile,
                lpunFile,
                lambda);

        TaskHandle taskHandle = tasks.execute(username,
                "Charmm Generate Input",
                () -> {

                    charmmRepository.removeContainer(username);

                    CharmmInputContainer output = workflow.execute(WorkflowContext.withInput(input));

                    charmmRepository.saveContainer(username, output);
                    Thread.sleep(1000);

                    return null;
                },
                (fit, pp) -> ShowGeneratedInputPage.class);

        LOGGER.debug("GenerateInputCommand generateInput DONE.");
        return taskHandle.getId();
    }
}

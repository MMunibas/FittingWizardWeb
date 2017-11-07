package ch.unibas.fitting.web.ljfit.ui.step1;

import ch.unibas.fitting.shared.directories.LjFitSessionDir;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import ch.unibas.fitting.shared.workflows.charmm.GenerateInputWorkflowInput;
import ch.unibas.fitting.shared.workflows.charmm.CharmmInputContainer;
import ch.unibas.fitting.shared.workflows.charmm.IGenerateInputWorkflow;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.ljfit.services.CharmmRepository;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitSession;
import ch.unibas.fitting.web.ljfit.services.LjFitRepository;
import ch.unibas.fitting.web.ljfit.ui.commands.OpenLjFitSessionCommand;
import ch.unibas.fitting.web.ljfit.ui.step3.ShowGeneratedInputPage;
import ch.unibas.fitting.web.web.PageCommand;
import ch.unibas.fitting.web.web.PageNavigation;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class CreateNewLjFitSessionCommand extends PageCommand {

    private Logger LOGGER = org.apache.log4j.Logger.getLogger(this.getClass());

    @Inject
    private IBackgroundTasks tasks;
    @Inject
    private IUserDirectory userDirectory;
    @Inject
    private CharmmRepository charmmRepository;
    @Inject
    private IGenerateInputWorkflow workflow;

    @Inject
    private LjFitRepository ljFitRepository;

    @Inject
    private OpenLjFitSessionCommand openLjFitSession;

    public void execute(String username, LjFitSession session) {

        TaskHandle taskHandle = tasks.execute(username,
                "Generating new LJ fit session...",
                () -> {
                    LOGGER.debug("CreateNewLjFitSessionCommand execute");

                    // delete existing
                    userDirectory.deleteLjFitSession(username);
                    // create new
                    // save json
                    ljFitRepository.save(username, session);
                    // generate input files for initial sigma and epsilon

                    LjFitSessionDir sessionDir = userDirectory.getLjFitSessionDir(username);

                    GenerateInputWorkflowInput input = new GenerateInputWorkflowInput(
                            sessionDir,
                            session);

                    charmmRepository.removeContainer(username);

                    CharmmInputContainer output = workflow.execute(WorkflowContext.withInput(input));

                    charmmRepository.saveContainer(username, output);

                    LOGGER.debug("CreateNewLjFitSessionCommand execute DONE.");

                    return null;
                },
                (fit, pp) ->  {
                    openLjFitSession.execute(username);
                    return null;
                },
                CreateNewSessionPage.class);

        PageNavigation.ToProgressForTask(taskHandle);
    }
}

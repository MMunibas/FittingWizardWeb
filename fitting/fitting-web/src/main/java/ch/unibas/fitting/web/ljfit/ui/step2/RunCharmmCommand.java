package ch.unibas.fitting.web.ljfit.ui.step2;

import ch.unibas.fitting.shared.charmm.web.CharmmResult;
import ch.unibas.fitting.shared.charmm.web.IRunCharmmWorkflowNew;
import ch.unibas.fitting.shared.workflows.charmm.CharmmInputContainer;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.ljfit.services.CharmmRepository;
import ch.unibas.fitting.web.ljfit.ui.step3.ShowGeneratedInputPage;
import ch.unibas.fitting.web.ljfit.ui.step3.ShowOutputPage;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by mhelmer on 24.06.2016.
 */
public class RunCharmmCommand {
    @Inject
    private IRunCharmmWorkflowNew runCharmmWorkflowNew;

    @Inject
    private CharmmRepository charmmRepository;

    @Inject
    private IBackgroundTasks backgroundTasks;

    public UUID run(String username) {

        TaskHandle th = backgroundTasks.execute(username, "Run CHARMM",
                () -> {
                    Optional<CharmmInputContainer> c = charmmRepository.getContainerFor(username);
                    if (c.isPresent()) {
                        charmmRepository.removeResultFor(username);
                        CharmmResult result = runCharmmWorkflowNew.executeCharmm(c.get());
                        charmmRepository.saveResult(username, result);
                    }
                    return null;
                },
                (t, pageParameters) -> ShowOutputPage.class,
                ShowGeneratedInputPage.class);

        return th.getId();
    }
}

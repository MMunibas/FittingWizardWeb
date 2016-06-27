package ch.unibas.fitting.web.ljfit.step2;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;
import ch.unibas.fitting.shared.charmm.web.CharmmResult;
import ch.unibas.fitting.shared.charmm.web.IRunCharmmWorkflowNew;
import ch.unibas.fitting.shared.charmm.web.RunCharmmWorkflowNew;
import ch.unibas.fitting.shared.workflows.charmm.CharmmInputContainer;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.ljfit.CharmmRepository;
import ch.unibas.fitting.web.ljfit.step3.ShowOutputPage;

import javax.inject.Inject;
import java.util.List;
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

        TaskHandle th = backgroundTasks.execute(username, "",
                () -> {

                    Optional<CharmmInputContainer> c = charmmRepository.getContainerFor(username);
                    if (c.isPresent()) {
                        charmmRepository.removeResultFor(username);
                        CharmmResult result = runCharmmWorkflowNew.executeCharmm(c.get().getGasInput(),
                                c.get().getLiquidInput(),
                                c.get().getAllGenerators());
                        charmmRepository.saveResult(username, result);
                    }
                    return null;
                }, (t, pageParameters) -> ShowOutputPage.class);

        return th.getId();
    }
}

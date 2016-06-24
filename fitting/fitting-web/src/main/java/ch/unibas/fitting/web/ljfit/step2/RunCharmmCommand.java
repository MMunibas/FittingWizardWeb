package ch.unibas.fitting.web.ljfit.step2;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;
import ch.unibas.fitting.shared.charmm.web.RunCharmmWorkflowNew;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.ljfit.step3.ShowOutputPage;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by mhelmer on 24.06.2016.
 */
public class RunCharmmCommand {
    @Inject
    private RunCharmmWorkflowNew runCharmmWorkflowNew;

    @Inject
    private IBackgroundTasks backgroundTasks;

    public UUID run(String username) {

        TaskHandle th = backgroundTasks.execute(username, "",
                () -> {
                    CHARMM_Input_GasPhase gas = null;
                    CHARMM_Input_PureLiquid liquid = null;
                    List<CHARMM_Generator_DGHydr> dgHydrList = null;

                    runCharmmWorkflowNew.executeCharmm(gas, liquid, dgHydrList);
                    return null;
                }, (t, pageParameters) -> ShowOutputPage.class);

        return th.getId();
    }
}

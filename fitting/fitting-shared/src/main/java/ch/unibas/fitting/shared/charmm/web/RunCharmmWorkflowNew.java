package ch.unibas.fitting.shared.charmm.web;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_PureLiquid;
import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.workflows.charmm.CharmmInputContainer;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by mhelmer on 24.06.2016.
 */
public class RunCharmmWorkflowNew implements IRunCharmmWorkflowNew {
    protected static final Logger LOGGER = Logger.getLogger(RunCharmmWorkflowNew.class);

    private final CHARMM_Den_Vap charmm_den_vap;

    @Inject
    public RunCharmmWorkflowNew(Settings settings) {
        this.charmm_den_vap= new CHARMM_Den_Vap(settings);
    }

    public CharmmResult executeCharmm(CharmmInputContainer container) {
        LOGGER.debug("Executing Charm Workflow");

        CHARMM_Output_GasPhase gasOut = charmm_den_vap.execute(container.getGasInput());
        CHARMM_Output_PureLiquid pureLiquidOut = charmm_den_vap.execute(container.getLiquidInput());

        for (CHARMM_Generator_DGHydr script : container.getAllGenerators()){
            LOGGER.debug("Executing CHARMM_Generator_DGHydr: " + script.Whoami());
            script.run();
        }

        CharmmResultParserOutput output = CharmmResultParser.parseOutput(
                gasOut,
                pureLiquidOut,
                container.getGasVdw(),
                container.getGasMtp(),
                container.getSolvVdw(),
                container.getSolvMtp());

        LOGGER.debug("Executing Charm Workflow DONE.");
        return new CharmmResult(gasOut, pureLiquidOut, output);
    }
}

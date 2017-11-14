package ch.unibas.fitting.shared.charmm.web;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;
import ch.unibas.fitting.shared.charmm.scripts.ClusterParameter;
import ch.unibas.fitting.shared.workflows.charmm.CharmmInputContainer;

import java.util.List;

/**
 * Created by tschmidt on 27.06.2016.
 */
public interface IRunCharmmWorkflowNew {
    CharmmResult executeCharmm(CharmmInputContainer inputContainer, ClusterParameter clusterParameter);
}

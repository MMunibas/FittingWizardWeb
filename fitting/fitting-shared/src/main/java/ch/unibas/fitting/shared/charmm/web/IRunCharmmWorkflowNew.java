package ch.unibas.fitting.shared.charmm.web;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;

import java.util.List;

/**
 * Created by tschmidt on 27.06.2016.
 */
public interface IRunCharmmWorkflowNew {
    public CharmmResult executeCharmm(CHARMM_Input_GasPhase gasPhase,
                                      CHARMM_Input_PureLiquid pureLiquid,
                                      List<CHARMM_Generator_DGHydr> DGHydr);
}

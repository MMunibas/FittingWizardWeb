package ch.unibas.fitting.shared.charmm;

import ch.unibas.fitting.shared.charmm.generate.CHARMM_InOut;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output;
import javafx.event.ActionEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhelmer on 24.06.2016.
 */
public class RunCharmmWorkflowNew {

    protected List<CHARMM_Input>  inp = new ArrayList<>();
    protected List<CHARMM_Output> out = new ArrayList<>();

    private CHARMM_Generator_DGHydr in_gas_vdw = null, in_gas_mtp = null,
            in_solv_vdw = null, in_solv_mtp = null;

    protected List<File> CHARMM_inFile = new ArrayList<>();
    protected List<File> CHARMM_outFile = new ArrayList<>();

    protected void runCHARMM(ActionEvent event) {

        List<CHARMM_InOut> myList = new ArrayList<>();
        myList.addAll(inp);
        myList.addAll(out);
        myList.add(in_gas_vdw);
        myList.add(in_gas_mtp);
        myList.add(in_solv_vdw);
        myList.add(in_solv_mtp);

        //navigateTo(RunningCHARMM_DenVap.class, myList);
    }
}

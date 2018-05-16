package ch.unibas.fitting.shared.workflows.charmm;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_GasPhase;
import ch.unibas.fitting.shared.directories.LjFitRunDir;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class CharmmInputContainer {
    private final LjFitRunDir charmmRunDir;
    private final CHARMM_Input_GasPhase gasInput;
    private final CHARMM_Input_PureLiquid solvInput;
    private final CHARMM_Generator_DGHydr gasVdw;
    private final CHARMM_Generator_DGHydr gasMtp;
    private final CHARMM_Generator_DGHydr solvVdw;
    private final CHARMM_Generator_DGHydr solvMtp;

    public CharmmInputContainer(LjFitRunDir charmmRunDir,
                                CHARMM_Input_GasPhase gasInput,
                                CHARMM_Input_PureLiquid solvInput,
                                CHARMM_Generator_DGHydr gasVdw,
                                CHARMM_Generator_DGHydr gasMtp,
                                CHARMM_Generator_DGHydr solvVdw,
                                CHARMM_Generator_DGHydr solvMtp) {
        this.charmmRunDir = charmmRunDir;
        this.gasInput = gasInput;
        this.solvInput = solvInput;
        this.gasVdw = gasVdw;
        this.gasMtp = gasMtp;
        this.solvVdw = solvVdw;
        this.solvMtp = solvMtp;
    }

    public LjFitRunDir getCharmmRunDir() {
        return charmmRunDir;
    }

    public CHARMM_Input_GasPhase getGasInput() {
        return gasInput;
    }

    public CHARMM_Input_PureLiquid getLiquidInput() {
        return solvInput;
    }

    public CHARMM_Generator_DGHydr getGasVdw() {
        return gasVdw;
    }

    public CHARMM_Generator_DGHydr getGasMtp() {
        return gasMtp;
    }

    public CHARMM_Generator_DGHydr getSolvVdw() {
        return solvVdw;
    }

    public CHARMM_Generator_DGHydr getSolvMtp() {
        return solvMtp;
    }

    public List<CHARMM_Generator_DGHydr> getAllGenerators() {
        return Arrays.asList(
                getGasVdw(),
                getGasMtp(),
                getSolvVdw(),
                getSolvMtp()
        );
    }
}

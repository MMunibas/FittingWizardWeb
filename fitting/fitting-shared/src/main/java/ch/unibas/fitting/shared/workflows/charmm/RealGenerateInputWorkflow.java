package ch.unibas.fitting.shared.workflows.charmm;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;
import ch.unibas.fitting.shared.directories.CharmmRunFileContainer;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by mhelmer on 27.06.2016.
 */
public class RealGenerateInputWorkflow implements IGenerateInputWorkflow {

    private static final Logger LOGGER = Logger.getLogger(RealGenerateInputWorkflow.class);

    @Override
    public CharmmInputContainer execute(WorkflowContext<GenerateInputWorkflowInput> status) {

        GenerateInputWorkflowInput input = status.getParameter();
        CharmmRunFileContainer charmmRunDir = status.getParameter().getCharmmOutputDir().createRunDir();

        CharmmInputContainer preparedInput = prepareInput(input, charmmRunDir);

        preparedInput.getGasInput().generate();
        preparedInput.getLiquidInput().generate();

        preparedInput.getGasMtp().generate();
        preparedInput.getGasVdw().generate();

        preparedInput.getSolvMtp().generate();
        preparedInput.getSolvVdw().generate();

        return preparedInput;
    }

    protected CharmmInputContainer prepareInput(GenerateInputWorkflowInput input, CharmmRunFileContainer charmmRunDir) {
        File liquidFile = input.getLiquidFile();
        File lpunFile = input.getLpunFile();
        File molFile = input.getMolFile();
        File rtfFile = input.getRtfFile();
        File parFile = input.getParFile();
        File solventFile = input.getSolventFile();
        double lambda = input.getLambda();

        File gas_dir = charmmRunDir.getGasDir();
        File gas_vdw_dir  = charmmRunDir.getGasVdwDir();
        File gas_mtp_dir  = charmmRunDir.getGasMtpDir();

        File solv_dir = charmmRunDir.getSolvDir();
        File solv_vdw_dir = charmmRunDir.getSolvVdwDir();
        File solv_mtp_dir = charmmRunDir.getSolvMtpDir();

        File gasFile = new File(gas_dir.getAbsolutePath(), "gas_phase.inp");
        CHARMM_Input_GasPhase  gasInp = new CHARMM_Input_GasPhase(
                molFile.getAbsolutePath(),
                rtfFile.getAbsolutePath(),
                parFile.getAbsolutePath(),
                lpunFile.getAbsolutePath(),
                gasFile);

        File liqFile = new File(solv_dir.getAbsolutePath(), "pure_liquid.inp");
        CHARMM_Input_PureLiquid liqInp = new CHARMM_Input_PureLiquid(
                liquidFile.getAbsolutePath(),
                rtfFile.getAbsolutePath(),
                parFile.getAbsolutePath(),
                lpunFile.getAbsolutePath(),
                liqFile);

        CHARMM_Generator_DGHydr in_gas_vdw = new CHARMM_Generator_DGHydr(
                molFile.getAbsolutePath(),
                rtfFile.getAbsolutePath(),
                parFile.getAbsolutePath(),
                lpunFile.getAbsolutePath(),
                "vdw",
                0.0,
                lambda,
                1.0,
                gas_vdw_dir);

        CHARMM_Generator_DGHydr in_gas_mtp = new CHARMM_Generator_DGHydr(
                molFile.getAbsolutePath(),
                rtfFile.getAbsolutePath(),
                parFile.getAbsolutePath(),
                lpunFile.getAbsolutePath(),
                "mtp",
                0.0,
                lambda,
                1.0,
                gas_mtp_dir);

        // solvent
        CHARMM_Generator_DGHydr in_solv_vdw = new CHARMM_Generator_DGHydr(
                molFile.getAbsolutePath(),
                solventFile.getAbsolutePath(),
                rtfFile.getAbsolutePath(),
                rtfFile.getAbsolutePath(),
                parFile.getAbsolutePath(),
                lpunFile.getAbsolutePath(),
                "vdw",
                0.0,
                lambda,
                1.0,
                solv_vdw_dir);
        // solvent
        CHARMM_Generator_DGHydr in_solv_mtp = new CHARMM_Generator_DGHydr(
                molFile.getAbsolutePath(),
                solventFile.getAbsolutePath(),
                rtfFile.getAbsolutePath(),
                rtfFile.getAbsolutePath(),
                parFile.getAbsolutePath(),
                lpunFile.getAbsolutePath(),
                "mtp",
                0.0,
                lambda,
                1.0,
                solv_mtp_dir);

        return new CharmmInputContainer(gasInp, liqInp, in_gas_vdw, in_gas_mtp, in_solv_vdw, in_solv_mtp);
    }
}

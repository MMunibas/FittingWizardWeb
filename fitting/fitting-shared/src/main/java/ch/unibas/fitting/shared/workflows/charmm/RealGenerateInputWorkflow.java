package ch.unibas.fitting.shared.workflows.charmm;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;
import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.LjFitRunDir;
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitRunInput;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

/**
 * Created by mhelmer on 27.06.2016.
 */
public class RealGenerateInputWorkflow implements IGenerateInputWorkflow {

    private static final Logger LOGGER = Logger.getLogger(RealGenerateInputWorkflow.class);
    private final Settings settings;

    @Inject
    public RealGenerateInputWorkflow(Settings settings) {
        this.settings = settings;
    }

    @Override
    public CharmmInputContainer execute(
            WorkflowContext<GenerateInputWorkflowInput> context) {

        UploadedFiles uploaded = context.getParameter().uploadedFiles;
        LjFitRunInput input = context.getParameter().runInput;
        LjFitRunDir charmmRunDir = context.getParameter().runDir;

        CharmmInputContainer preparedInput = prepareInput(
                uploaded,
                input,
                charmmRunDir);

        preparedInput.getGasInput().generate();
        preparedInput.getLiquidInput().generate();

        preparedInput.getGasMtp().generate();
        preparedInput.getGasVdw().generate();

        preparedInput.getSolvMtp().generate();
        preparedInput.getSolvVdw().generate();

        return preparedInput;
    }

    protected CharmmInputContainer prepareInput(
            UploadedFiles uploaded,
            LjFitRunInput input,
            LjFitRunDir charmmRunDir) {

        File liquidFile = uploaded.liquidFile;

        File molFile = uploaded.molFile;
        File rtfFile = uploaded.rtfFile;
        File solventFile = uploaded.solventFile;
        File parFile = scaleParFile(uploaded, charmmRunDir, input);
        File lpunFile = uploaded.lpunFile;
        File liquidDensityLpun = prepareLiquidDensityLpunFile(uploaded, charmmRunDir);
        double lambda_spacing = input.lambda_size_electrostatic;


        var gasFile = new File(charmmRunDir.getDensity_dir(), "gas_phase.inp");
        var  gasInp = new CHARMM_Input_GasPhase(
                molFile,
                rtfFile,
                parFile,
                lpunFile,
                gasFile);

        var liqFile = new File(charmmRunDir.getDensity_dir(), "pure_liquid.inp");
        var liqInp = new CHARMM_Input_PureLiquid(
                liquidFile,
                rtfFile,
                parFile,
                liquidDensityLpun,
                liqFile,
                input.temperature);

        var in_gas_vdw = new CHARMM_Generator_DGHydr(
                molFile,
                rtfFile,
                parFile,
                lpunFile,
                "vdw",
                0.0,
                lambda_spacing,
                1.0,
                charmmRunDir.getGasVdwDir(),
                settings);

        var in_gas_mtp = new CHARMM_Generator_DGHydr(
                molFile,
                rtfFile,
                parFile,
                lpunFile,
                "mtp",
                0.0,
                lambda_spacing,
                1.0,
                charmmRunDir.getGasMtpDir(),
                settings);

        // solvent
        var in_solv_vdw = new CHARMM_Generator_DGHydr(
                molFile,
                solventFile,
                rtfFile,
                rtfFile,
                parFile,
                lpunFile,
                "vdw",
                0.0,
                lambda_spacing,
                1.0,
                charmmRunDir.getSolvVdwDir(),
                settings);
        // solvent
        var in_solv_mtp = new CHARMM_Generator_DGHydr(
                molFile,
                solventFile,
                rtfFile,
                rtfFile,
                parFile,
                lpunFile,
                "mtp",
                0.0,
                lambda_spacing,
                1.0,
                charmmRunDir.getSolvMtpDir(),
                settings);

        return new CharmmInputContainer(
                charmmRunDir,
                gasInp,
                liqInp,
                in_gas_vdw,
                in_gas_mtp,
                in_solv_vdw,
                in_solv_mtp);
    }

    protected File prepareLiquidDensityLpunFile(
            UploadedFiles uploaded,
            LjFitRunDir charmmRunDir) {

        List<String> args = List.of(
                "--lpun",
                FilenameUtils.normalize(uploaded.lpunFile.getAbsolutePath()),
                "--pdb",
                FilenameUtils.normalize(uploaded.liquidFile.getAbsolutePath())
        );

        File script = new File(settings.getScriptsDir(), "prepare-lpun.py");
        PythonScriptRunner runner = new PythonScriptRunner();
        runner.setWorkingDir(charmmRunDir.getDirectory());
        runner.exec(script, args);

        return new File(charmmRunDir.getDirectory(), pureLiquidFileName());
    }

    protected File scaleParFile(UploadedFiles uploadedFiles,
                              LjFitRunDir charmmRunDir,
                              LjFitRunInput input) {


        List<String> args = List.of(
                "--tps",
                FilenameUtils.normalize(uploadedFiles.rtfFile.getAbsolutePath()),
                "--par",
                FilenameUtils.normalize(uploadedFiles.parFile.getAbsolutePath()),
                "--slu",
                FilenameUtils.normalize(uploadedFiles.molFile.getAbsolutePath()),
                "--lambda_sigma",
                Double.toString(input.lambdaSigma),
                "--lambda_epsilon",
                Double.toString(input.lambdaEpsilon)
        );

        File script = new File(settings.getScriptsDir(), "scale-par.py");
        PythonScriptRunner runner = new PythonScriptRunner();
        runner.setWorkingDir(charmmRunDir.getDirectory());
        runner.exec(script, args);

        return scaledFileName(uploadedFiles, charmmRunDir);
    }

    protected String pureLiquidFileName() {
        return "pureliquid.lpun";
    }

    protected File scaledFileName(UploadedFiles uploadedFiles,
                                  LjFitRunDir charmmRunDir) {
        return new File(
                charmmRunDir.getDirectory(),
                String.format("%s_scaled.%s",
                        FilenameUtils.getBaseName(uploadedFiles.parFile.getName()),
                        FilenameUtils.getExtension(uploadedFiles.parFile.getName())));
    }
}

package ch.unibas.fitting.shared.charmm.web;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_PureLiquid;
import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhelmer on 24.06.2016.
 */
public class CHARMM_Den_Vap {

    protected final Logger LOGGER;

    private static final String ScriptNameKey = "scripts.submitCHARMM_Den_Vap";
    private final File scriptFile;
    private final Settings settings;

    public CHARMM_Den_Vap(Settings settings)
    {
        this.settings = settings;
        LOGGER = Logger.getLogger(getClass());
        this.scriptFile = new File(settings.getScriptsDir(), settings.getValue(ScriptNameKey));
    }

    public CHARMM_Output_GasPhase execute(CHARMM_Input_GasPhase input) {
        LOGGER.debug("execute CHARMM_Input_GasPhase");
        File output = execute("gas_phase.out", input);
        return new CHARMM_Output_GasPhase(output);
    }

    public CHARMM_Output_PureLiquid execute(CHARMM_Input_PureLiquid input) {
        LOGGER.debug("execute CHARMM_Input_PureLiquid");
        File output = execute("pure_liquid.out", input);
        return new CHARMM_Output_PureLiquid(output);
    }

    public File execute(String outputFilename, CHARMM_Input input) {
        PythonScriptRunner runner = new PythonScriptRunner();

        File baseDir = input.getWorkDir();
        runner.setWorkingDir(baseDir);
        File charmmout = new File(baseDir, outputFilename);

        LOGGER.debug("Saving to output file " + charmmout.getName());

        List<String> args = prepare_Python(input, charmmout);
        runner.exec(scriptFile, args);

        return charmmout;
    }

    private List<String> prepare_Python(CHARMM_Input input, File output) {

        String inpPath = input.getOut().getName();
        String parPath = input.getPar().getName();
        String topPath = input.getTop().getName();
        String lpunPath = input.getLpun().getName();
        String pdbpath = input.getCrd().getName();
        String outPath = output.getAbsolutePath();

        LOGGER.debug("Preparing PYTHON call with parameters : " + inpPath + " " + outPath + " " + parPath + " " + topPath + " " + lpunPath);

        List<String> args = new ArrayList<>();
        args.add("-inp");   args.add(inpPath);
        args.add("-out");   args.add(outPath);
        args.add("-par");   args.add(parPath);
        args.add("-top");   args.add(topPath);
        args.add("-lpun");  args.add(lpunPath);
        args.add("-pdb");  args.add(pdbpath);
        args.add("-np");    args.add(Integer.toString(4));
        args.add("-cfg");   args.add(FilenameUtils.normalize(settings.getCharmmConfig().getAbsolutePath()));
        return args;
    }
}

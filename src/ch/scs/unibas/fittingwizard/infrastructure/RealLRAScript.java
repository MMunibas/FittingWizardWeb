package ch.scs.unibas.fittingwizard.infrastructure;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import ch.scs.unibas.fittingwizard.infrastructure.base.ScriptUtilities;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import ch.scs.unibas.fittingwizard.Settings;
import ch.scs.unibas.fittingwizard.application.scripts.lra.ILRAScript;
import ch.scs.unibas.fittingwizard.application.scripts.lra.LRAScriptInput;
import ch.scs.unibas.fittingwizard.application.scripts.lra.LRAScriptOutput;
import ch.scs.unibas.fittingwizard.infrastructure.base.PythonScriptRunner;


public class RealLRAScript  implements ILRAScript {

	private final static Logger logger = Logger.getLogger(RealLRAScript.class);

	private final String LRAScriptNameKey = "scripts.atom_types";

	public static final String LPunExtension = "_l.pun";

	private final PythonScriptRunner runner;
    private File lraScriptFile;

    public RealLRAScript(Settings settings) {
        runner = new PythonScriptRunner();
        lraScriptFile = new File(settings.getScriptsDir(), settings.getValue(LRAScriptNameKey));
	}

	@Override
	public LRAScriptOutput execute(LRAScriptInput input) {

		List<String> args = Arrays.asList("-in", input.getSdfFile().toString());

        File lPunFile = new File(FilenameUtils.removeExtension(input.getSdfFile().toString()) + LPunExtension);
        ScriptUtilities.deleteFileIfExists(lPunFile);

        runner.exec(lraScriptFile, args);

        ScriptUtilities.verifyFileExistence(lPunFile);

		return new LRAScriptOutput(lPunFile);
	}
	
}

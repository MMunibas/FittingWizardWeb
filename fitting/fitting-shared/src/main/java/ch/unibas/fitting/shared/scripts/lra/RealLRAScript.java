/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.lra;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import ch.unibas.fitting.shared.scripts.base.ScriptUtilities;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;


public class RealLRAScript  implements ILRAScript {

	private final static Logger logger = Logger.getLogger(RealLRAScript.class);

	private final String LRAScriptNameKey = "scripts.atom_types";

	public static final String LPunExtension = "_l.pun";

	private final PythonScriptRunner runner;
    private File lraScriptFile;

	@Inject
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

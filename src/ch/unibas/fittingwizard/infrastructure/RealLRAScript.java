/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.infrastructure;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import ch.unibas.fittingwizard.infrastructure.base.ScriptUtilities;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import ch.unibas.fittingwizard.Settings;
import ch.unibas.fittingwizard.application.scripts.lra.ILRAScript;
import ch.unibas.fittingwizard.application.scripts.lra.LRAScriptInput;
import ch.unibas.fittingwizard.application.scripts.lra.LRAScriptOutput;
import ch.unibas.fittingwizard.infrastructure.base.PythonScriptRunner;


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

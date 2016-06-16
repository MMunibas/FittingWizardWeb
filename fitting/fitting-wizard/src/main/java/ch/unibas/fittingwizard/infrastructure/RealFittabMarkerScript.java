/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.infrastructure;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.scripts.base.ScriptExecutionException;
import ch.unibas.fitting.shared.scripts.fittab.FittabScriptInput;
import ch.unibas.fitting.shared.scripts.fittab.FittabScriptOutput;
import ch.unibas.fitting.shared.scripts.fittab.IFittabScript;
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

public class RealFittabMarkerScript implements IFittabScript {

	private final static Logger logger = Logger.getLogger(RealLRAScript.class);

	private static final String FitTabMarkerScriptNameKey = "scripts.prepare_fitting_table";
	private static final String MtpFittabExtension = "_mtpfittab.txt";
	
	private File moleculesDir;

	private final PythonScriptRunner runner;
    private File fitTabMarkerScriptFile;

    public RealFittabMarkerScript(File moleculesDir, Settings settings) {
		this.moleculesDir = moleculesDir;
        this.fitTabMarkerScriptFile = new File(settings.getScriptsDir(), settings.getValue(FitTabMarkerScriptNameKey));
        runner = new PythonScriptRunner();
	}

	@Override
	public FittabScriptOutput execute(FittabScriptInput input) {
	
		String moleculeName = getMoleculeName(input.getCubeFile());
		File specificMoleculeDir = new File(moleculesDir, moleculeName);

        runner.setWorkingDir(specificMoleculeDir);

		List<String> args = Arrays.asList("-cube", input.getCubeFile().getAbsoluteFile().toString(),
										  "-vdw", input.getVdwFile().getAbsoluteFile().toString(),
										  "-pun", input.getLpunFile().getAbsoluteFile().toString());

		File mtpFile = new File(specificMoleculeDir, FilenameUtils.removeExtension(input.getCubeFile().getName()) + MtpFittabExtension);

        runner.exec(fitTabMarkerScriptFile, args);

		if (!mtpFile.exists()) {
			throw new ScriptExecutionException(
					"FittabMarker script did not create " + FilenameUtils.normalize(mtpFile.getAbsolutePath()) + " output file.");
		}

		return new FittabScriptOutput(mtpFile);
	}
	
	private String getMoleculeName(File input) {
		return FilenameUtils.removeExtension(input.getName());
	}

}

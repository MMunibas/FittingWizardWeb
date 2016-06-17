/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.babel;

import ch.unibas.fitting.shared.scripts.base.ScriptExecutionException;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * User: mhelmer Date: 06.12.13 Time: 13:13
 */
public class RealBabelScript implements IBabelScript {

	private final static Logger logger = Logger.getLogger(RealBabelScript.class);

	private static final String sdfExtension = ".sdf";
	private final BabelRunner runner = new BabelRunner();

	@Override
	public BabelOutput execute(BabelInput input) {
		
		String moleculeName = getMoleculeName(input.getGaussianLogFile());
		
		File specificMoleculeDir = input.getMoleculesDir().getDirectoryFor(moleculeName);
		
		setWorkingDir(specificMoleculeDir);
		
		File sdfFile = new File(specificMoleculeDir, moleculeName + sdfExtension);
		
		List<String> args = Arrays.asList("-ig03", input.getGaussianLogFile().getName(),
                "-osdf", sdfFile.getName());

		logger.info("Running babel: " + args.toString());

		try {
			int retval = runner.exec(args);
			if (retval != 0) {
				logger.error("Babel exited with non-zero return value: " + retval);
				throw new ScriptExecutionException("Babel exited with non-zero return value: " + retval);
			}
		} catch (Exception e) {
			logger.error("Babel file conversion failed.");
			throw new ScriptExecutionException("Babel file conversion failed.", e);
		}

		if (!sdfFile.exists()) {
			logger.error("Babel file conversion did not create SDF output file.");
			throw new ScriptExecutionException(
					"Babel file conversion did not create SDF output file.");
		}

		return new BabelOutput(sdfFile);
	}

	private void setWorkingDir(File workingDir) {
		if (! workingDir.exists()) {
			workingDir.mkdir();
		}
		
		runner.setWorkingDir(workingDir);
	}
	
	private String getMoleculeName(File input) {
		return FilenameUtils.removeExtension(input.getName());
	}
	
}

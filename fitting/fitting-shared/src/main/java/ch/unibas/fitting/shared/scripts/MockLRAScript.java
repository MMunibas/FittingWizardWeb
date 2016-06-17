/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.scripts.lra.ILRAScript;
import ch.unibas.fitting.shared.scripts.lra.LRAScriptInput;
import ch.unibas.fitting.shared.scripts.lra.LRAScriptOutput;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * User: mhelmer Date: 05.12.13 Time: 17:25
 */
public class MockLRAScript implements ILRAScript {

	private final List<String> filesToCopy = Arrays.asList("_l.pun");
	private final String lpunExtension = "_l.pun";

	private final static Logger logger = Logger.getLogger(MockLRAScript.class);
	private final File testMoleculesDir;

	public MockLRAScript(Settings settings) {
	    this.testMoleculesDir = settings.getMoleculeTestdataDir();
	}

	@Override
	public LRAScriptOutput execute(LRAScriptInput input) {
		// /esp-fit.pc-mtp/src/calc_LRA.py -in co2
		// This generates a *_l.pun file.

		String moleculeName = getMoleculeName(input.getSdfFile());

		File specificMoleculeDir = input.getMoleculesDir().getDirectoryFor(moleculeName);
		
		File lpunOutputFile = new File(specificMoleculeDir, moleculeName + lpunExtension);

		File precomputedDataDir = new File(testMoleculesDir, moleculeName);

		for (String fileExtension : filesToCopy) {

			File precomputedFile = new File(precomputedDataDir, moleculeName
					+ fileExtension);

			try {
				FileUtils.copyFile(precomputedFile, new File(specificMoleculeDir,
						moleculeName + fileExtension));
			} catch (IOException e) {
				logger.error("Could not copy precomputed file "
						+ precomputedFile);
				throw new RuntimeException("Could not copy precomputed file "
						+ precomputedFile);
			}
		}

		return new LRAScriptOutput(lpunOutputFile);
	}

	private String getMoleculeName(File input) {
		return FilenameUtils.removeExtension(input.getName());
	}

}

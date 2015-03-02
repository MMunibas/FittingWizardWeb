/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.mocks;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import ch.unibas.fittingwizard.application.scripts.fittab.FittabScriptInput;
import ch.unibas.fittingwizard.application.scripts.fittab.FittabScriptOutput;
import ch.unibas.fittingwizard.application.scripts.fittab.IFittabScript;

/**
 * User: mhelmer
 * Date: 05.12.13
 * Time: 17:29
 */
public class MockFittabMarkerScript implements IFittabScript {

	private final String mtpfittabExtension = "_mtpfittab.txt";
	private final List<String> filesToCopy = Arrays.asList(mtpfittabExtension);
	
	private final static Logger logger = Logger.getLogger(MockFittabMarkerScript.class);
    private File moleculesDir;
    private final File moleculeTestdataDir;

	public MockFittabMarkerScript(File moleculesDir, File moleculeTestdataDir) {
    	this.moleculesDir = moleculesDir;
        this.moleculeTestdataDir = moleculeTestdataDir;
	}

	@Override
    public FittabScriptOutput execute(FittabScriptInput input) {
        // example: /esp-fit.pc-mtp/src/mtp_fittab_maker.py -cube co2.cube -vdw co2.vdw -pun co2_l.pun
		
		String moleculeName = getMoleculeName(input.getCubeFile());

		File specificMoleculeDir = new File(moleculesDir, moleculeName);
		
		File mtpfittabOutputFile = new File(specificMoleculeDir, moleculeName + mtpfittabExtension);

		File precomputedDataDir = new File(moleculeTestdataDir, moleculeName);

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

		return new FittabScriptOutput(mtpfittabOutputFile);
	}

	private String getMoleculeName(File input) {
		return FilenameUtils.removeExtension(input.getName());
	}

}

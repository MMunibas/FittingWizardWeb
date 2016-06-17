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
import ch.unibas.fitting.shared.scripts.multipolegauss.IMultipoleGaussScript;
import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussInput;
import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussOutput;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * User: mhelmer Date: 28.11.13 Time: 15:13
 */
public class MockMultipoleGaussScript implements IMultipoleGaussScript {

	private final String logExtension = ".log";
	private final String punExtension = ".pun";
	private final String cubeExtension = ".cube";
	private final String vdwExtension = ".vdw";

	private final List<String> filesToCopy = Arrays.asList(logExtension, punExtension,
														   cubeExtension, vdwExtension);

	private final static Logger logger = Logger.getLogger(MockLRAScript.class);

	private final File moleculeTestdataDir;

    @Inject
	public MockMultipoleGaussScript(Settings settings) {
        this.moleculeTestdataDir = settings.getMoleculeTestdataDir();
	}

	@Override
	public MultipoleGaussOutput execute(MultipoleGaussInput input) {

		File specificMoleculeDir = input.getMoleculesDir().getDirectoryFor(input.getMoleculeName());
		
		File logOutfile  = new File(specificMoleculeDir,  input.getMoleculeName() + logExtension);
		File punOutfile  = new File(specificMoleculeDir,  input.getMoleculeName() + punExtension);
		File cubeOutfile = new File(specificMoleculeDir,  input.getMoleculeName() + cubeExtension);
		File vdwOutfile  = new File(specificMoleculeDir,  input.getMoleculeName() + vdwExtension);

		File precomputedDataDir = new File(moleculeTestdataDir, input.getMoleculeName());
		
		for (String fileExtension: filesToCopy) {
			
			File precomputedFile = new File(precomputedDataDir, input.getMoleculeName() + fileExtension);
			
			try {
				FileUtils.copyFile(precomputedFile, new File(specificMoleculeDir,  input.getMoleculeName() + fileExtension));
			} catch (IOException e) {
				logger.error(e.getStackTrace());
				throw new RuntimeException("Could not copy precomputed file " + precomputedFile);
			}
		}

		return new MultipoleGaussOutput(true, logOutfile, punOutfile, cubeOutfile, vdwOutfile);
	}
}

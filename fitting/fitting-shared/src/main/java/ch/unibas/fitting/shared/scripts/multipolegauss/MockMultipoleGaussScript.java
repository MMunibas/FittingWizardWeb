/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.multipolegauss;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.scripts.base.MockScriptBase;
import ch.unibas.fitting.shared.scripts.lra.MockLRAScript;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * User: mhelmer Date: 28.11.13 Time: 15:13
 */
public class MockMultipoleGaussScript extends MockScriptBase implements IMultipoleGaussScript {

	private final String logExtension = ".log";
	private final String punExtension = ".pun";
	private final String cubeExtension = ".cube";
	private final String vdwExtension = ".vdw";

	private final List<String> filesToCopy = Arrays.asList(logExtension, punExtension,
														   cubeExtension, vdwExtension);

	private final static Logger logger = Logger.getLogger(MockLRAScript.class);

    @Inject
	public MockMultipoleGaussScript(Settings settings) {
        super(settings.getMoleculeTestdataDir());
	}

	@Override
	public MultipoleGaussOutput execute(MultipoleGaussInput input) {

		File moleculeDestinationDir = input.getMoleculesDir().getMoleculeDir(input.getMoleculeName());

		File logOutfile  = new File(moleculeDestinationDir,  input.getMoleculeName() + logExtension);
		File punOutfile  = new File(moleculeDestinationDir,  input.getMoleculeName() + punExtension);
		File cubeOutfile = new File(moleculeDestinationDir,  input.getMoleculeName() + cubeExtension);
		File vdwOutfile  = new File(moleculeDestinationDir,  input.getMoleculeName() + vdwExtension);

		File moleculeTestdataDir = new File(getTestFileBase(), input.getMoleculeName());
		
		for (String fileExtension: filesToCopy) {
			
			File testdataFile = new File(moleculeTestdataDir, input.getMoleculeName() + fileExtension);
			File destinationFile = new File(moleculeDestinationDir,  input.getMoleculeName() + fileExtension);
			copyTestFile(testdataFile, destinationFile);
		}

		return new MultipoleGaussOutput(true, logOutfile, punOutfile, cubeOutfile, vdwOutfile);
	}
}

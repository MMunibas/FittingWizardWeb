/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.fittab;

import ch.unibas.fitting.shared.config.Settings;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.unibas.fitting.shared.scripts.base.MockScriptBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * User: mhelmer
 * Date: 05.12.13
 * Time: 17:29
 */
public class MockFittabMarkerScript extends MockScriptBase implements IFittabScript {

	private final String mtpfittabExtension = "_mtpfittab.txt";
	private final List<String> filesToCopy = Arrays.asList(mtpfittabExtension);

	@Inject
	public MockFittabMarkerScript(Settings settings) {
		super(settings.getMoleculeTestdataDir());
	}

	@Override
    public FittabScriptOutput execute(FittabScriptInput input) {
        // example: /esp-fit.pc-mtp/src/mtp_fittab_maker.py -cube co2.cube -vdw co2.vdw -pun co2_l.pun

		String moleculeName = getMoleculeName(input.getCubeFile());
		File destinationMoleDir = new File(input.getMoleculesDir().getDirectory(), moleculeName);
        String filename = moleculeName + mtpfittabExtension;
		File destinationFile = new File(destinationMoleDir, filename);
        File testdataMolDir = new File(getTestFileBase(), moleculeName);
        File testdataFile = new File(testdataMolDir, filename);

        copyTestFile(testdataFile, destinationFile);

		return new FittabScriptOutput(destinationFile);
	}

	private String getMoleculeName(File input) {
		return FilenameUtils.removeExtension(input.getName());
	}

}

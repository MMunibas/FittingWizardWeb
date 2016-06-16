/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.mocks;

import ch.unibas.fitting.shared.scripts.babel.BabelInput;
import ch.unibas.fitting.shared.scripts.babel.BabelOutput;
import ch.unibas.fitting.shared.scripts.babel.IBabelScript;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 06.12.13
 * Time: 13:13
 */
public class MockBabelScript implements IBabelScript {

	private final String logExtension = ".log";
	private final String sdfExtension = ".sdf";

	private final List<String> filesToCopy = Arrays.asList(".sdf");

	private final static Logger logger = Logger.getLogger(MockBabelScript.class);
	
	private File moleculeDestination;
    private final File moleculesTestData;

    public MockBabelScript(File moleculeDestination, File moleculesTestData) {
		this.moleculeDestination = moleculeDestination;
        this.moleculesTestData = moleculesTestData;
    }

	@Override
	public BabelOutput execute(BabelInput input) {
		
		String moleculeName = getMoleculeName(input.getGaussianLogFile());

		File outputDir = new File(moleculeDestination, moleculeName);
		
		File outputFile = new File(outputDir, moleculeName + sdfExtension);

		File precomputedDataDir = new File(moleculesTestData, moleculeName);

		for (String fileExtension : filesToCopy) {

			File precomputedFile = new File(precomputedDataDir, moleculeName
					+ fileExtension);

			try {
				FileUtils.copyFile(precomputedFile, new File(outputDir, moleculeName + fileExtension));
			} catch (IOException e) {
				logger.error("Could not copy precomputed file "
						+ precomputedFile);
				throw new RuntimeException("Could not copy precomputed file "
						+ precomputedFile);
			}
		}

		return new BabelOutput(outputFile);
		
	}
	
	private String getMoleculeName(File input) {
		return FilenameUtils.removeExtension(input.getName());
	}
}

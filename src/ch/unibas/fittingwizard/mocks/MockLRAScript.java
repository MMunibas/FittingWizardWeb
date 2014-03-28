package ch.unibas.fittingwizard.mocks;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import ch.unibas.fittingwizard.application.scripts.lra.ILRAScript;
import ch.unibas.fittingwizard.application.scripts.lra.LRAScriptInput;
import ch.unibas.fittingwizard.application.scripts.lra.LRAScriptOutput;

/**
 * User: mhelmer Date: 05.12.13 Time: 17:25
 */
public class MockLRAScript implements ILRAScript {

	private final List<String> filesToCopy = Arrays.asList("_l.pun");
	private final String lpunExtension = "_l.pun";

	private final static Logger logger = Logger.getLogger(MockLRAScript.class);
	private File moleculesDir;
    private final File testMoleculesDir;

	public MockLRAScript(File moleculesDir, File testMoleculesDir) {
		this.moleculesDir = moleculesDir;
        this.testMoleculesDir = testMoleculesDir;
	}

	@Override
	public LRAScriptOutput execute(LRAScriptInput input) {
		// /esp-fit.pc-mtp/src/calc_LRA.py -in co2
		// This generates a *_l.pun file.

		String moleculeName = getMoleculeName(input.getSdfFile());

		File specificMoleculeDir = new File(moleculesDir, moleculeName);
		
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

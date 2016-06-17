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
import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.scripts.base.ScriptExecutionException;
import ch.unibas.fitting.shared.scripts.multipolegauss.IMultipoleGaussScript;
import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussInput;
import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussOutput;
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import ch.unibas.fitting.shared.scripts.base.ScriptUtilities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import javax.inject.Inject;

public class RealMultipoleGaussScript implements IMultipoleGaussScript {

	public static final String xyzExtension = ".xyz";
	public static final String logExtension = ".log";
	public static final String fchkExtension = ".fchk";
	public static final String punExtension = ".pun";
	public static final String cubeExtension = ".cube";
	public static final String vdwExtension = ".vdw";
	
	private final static Logger logger = Logger.getLogger(RealMultipoleGaussScript.class);

    private final String MultipoleGaussScriptFileNameKey = "scripts.multipole_gauss";
	
	private final PythonScriptRunner runner;

    private File multipoleGaussScriptFile;

    @Inject
    public RealMultipoleGaussScript(Settings settings) {
    	runner = new PythonScriptRunner();
        multipoleGaussScriptFile = new File(settings.getScriptsDir(), settings.getValue(MultipoleGaussScriptFileNameKey));
	}

	@Override
    public MultipoleGaussOutput execute(MultipoleGaussInput input) {
        MoleculesDir molDir = input.getMoleculesDir();
		runner.setWorkingDir(molDir.getDirectory());

		List<String> args = new ArrayList<>();
		File xyzFile = new File(input.getMoleculeName() + xyzExtension);
        args.add("-xyz");
        args.add(xyzFile.getName());

        args.add("-charge");
        args.add(String.valueOf(input.getNetCharge()));

        args.add("-state");
        args.add(String.valueOf(input.getState()));
        
        args.add("-Gauss");
        args.add(input.getQuantChemDetails());
        
        args.add("-n");
        args.add(String.valueOf(input.getnCores()));

        molDir.deleteMolecule(input.getMoleculeName());
        File specificMoleculeDir = molDir.getDirectoryFor(input.getMoleculeName());

        File logOutfile  = new File(specificMoleculeDir,  input.getMoleculeName() + logExtension);
		File fchkOutfile = new File(specificMoleculeDir,  input.getMoleculeName() + fchkExtension);
		File punOutfile  = new File(specificMoleculeDir,  input.getMoleculeName() + punExtension);
		File cubeOutfile = new File(specificMoleculeDir,  input.getMoleculeName() + cubeExtension);
		File vdwOutfile  = new File(specificMoleculeDir,  input.getMoleculeName() + vdwExtension);

        logger.info("Running python: " + multipoleGaussScriptFile + " " + args.toString());
        int retval = runner.exec(multipoleGaussScriptFile, args);

        if (retval != 0) {
            logger.error("MultipoleGaussian script exited with non-zero return value: " + retval);
            throw new ScriptExecutionException("MultipoleGaussian script exited with non-zero return value: " + retval);
        }

        ScriptUtilities.verifyFileExistence(logOutfile);
        ScriptUtilities.verifyFileExistence(fchkOutfile);
        ScriptUtilities.verifyFileExistence(punOutfile);
        ScriptUtilities.verifyFileExistence(cubeOutfile);
        ScriptUtilities.verifyFileExistence(vdwOutfile);

		return new MultipoleGaussOutput(gaussianRunSuccessfull(logOutfile),
										logOutfile,
										punOutfile,
										cubeOutfile,
										vdwOutfile);
	}

	private boolean gaussianRunSuccessfull(File logFile) {
		// this returns true as the python script checks for
		// successful termination of the gaussian job
		return true;
	}
		
}

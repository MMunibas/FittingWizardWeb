/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.infrastructure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.unibas.fittingwizard.Settings;
import ch.unibas.fittingwizard.application.base.MoleculesDir;
import ch.unibas.fittingwizard.application.scripts.base.ScriptExecutionException;
import ch.unibas.fittingwizard.application.scripts.multipolegauss.IMultipoleGaussScript;
import ch.unibas.fittingwizard.application.scripts.multipolegauss.MultipoleGaussInput;
import ch.unibas.fittingwizard.application.scripts.multipolegauss.MultipoleGaussOutput;
import ch.unibas.fittingwizard.infrastructure.base.PythonScriptRunner;
import ch.unibas.fittingwizard.infrastructure.base.ScriptUtilities;

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
    private final MoleculesDir moleculesDir;

    private File multipoleGaussScriptFile;

    public RealMultipoleGaussScript(MoleculesDir moleculesDir, Settings settings) {
    	this.moleculesDir = moleculesDir;
    	runner = new PythonScriptRunner();
        multipoleGaussScriptFile = new File(settings.getScriptsDir(), settings.getValue(MultipoleGaussScriptFileNameKey));
	}

	@Override
    public MultipoleGaussOutput execute(MultipoleGaussInput input) {
		runner.setWorkingDir(moleculesDir.getDirectory());

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

        moleculesDir.deleteMolecule(input.getMoleculeName());
        File specificMoleculeDir = new File(moleculesDir.getDirectory(), input.getMoleculeName());

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

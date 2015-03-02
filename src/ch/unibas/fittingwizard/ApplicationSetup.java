/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard;

import ch.unibas.fittingwizard.infrastructure.base.BabelRunner;
import ch.unibas.fittingwizard.infrastructure.base.PythonScriptRunner;
import ch.unibas.fittingwizard.presentation.base.dialog.OverlayDialog;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * User: mhelmer
 * Date: 19.12.13
 * Time: 09:45
 */
public class ApplicationSetup {

    private static final Logger logger = Logger.getLogger(ApplicationSetup.class);

    private final Settings settings;

    public ApplicationSetup(Settings settings) {
        this.settings = settings;
    }

    public boolean verify() {
        boolean setupOkay = true;
        if  (settings.isApplicationVerifcationEnabled()) {
            setupOkay = !hasMissingApplicationDependencies();
        }
        return setupOkay;
    }

    public boolean hasMissingApplicationDependencies() {
        String missingStuff = "";
        boolean isMissing = false;
        if (settings.isApplicationVerifcationEnabled()) {
            boolean pythonIsAvailable = PythonScriptRunner.isAvailable();
            if (!pythonIsAvailable) {
                missingStuff += "Python is not available.\n";
                isMissing = true;
            } else {
                boolean isRdKitAvailable = checkIfRdKitIsInstalled();
                if (!isRdKitAvailable) {
                    missingStuff += "RDKit package for python is not available.\n";
                    isMissing = true;
                }
                
                boolean isSciPyAvailable = checkIfSciPyIsInstalled();
                if (!isSciPyAvailable) {
                    missingStuff += "SciPy package for python is not available.\n";
                    isMissing = true;
                }
            }

//            boolean babelIsVailable = BabelRunner.isAvailable();
//            if (!babelIsVailable) {
//                missingStuff += "Babel is not available.\n";
//                isMissing = true;
//            }

            if (isMissing) {
                missingStuff += "\nPlease check your installation. Maybe dependencies are just missing in the PATH.";
                OverlayDialog.showError("Missing application dependency", missingStuff);
            }
        }
        return isMissing;
    }

    public boolean checkIfRdKitIsInstalled() {
    	return checkIfIsInstalled("check_rdkit_dependency.py");
    }
    
    public boolean checkIfSciPyIsInstalled() {
    	return checkIfIsInstalled("check_scipy_dependency.py");
    }
    
    private boolean checkIfIsInstalled(String scriptName) {
    	PythonScriptRunner runner = new PythonScriptRunner();
        try {
            runner.exec(new File(settings.getScriptsDir(), scriptName));
        } catch (Exception e) {
            logger.error("Error while trying to check " + scriptName + " dependencies.");
            return false;
        }
        return true;
    }
}

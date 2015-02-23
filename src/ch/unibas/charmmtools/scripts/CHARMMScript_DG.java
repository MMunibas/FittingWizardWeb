/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.scripts;

import ch.unibas.fittingwizard.Settings;
import java.io.File;

/**
 *
 * @author hedin
 */
public class CHARMMScript_DG extends CHARMMScript_Base {

    private static final String ScriptNameKey = "scripts.submitCHARMM_DG";
    private static final String OutputDirName = "test";
    
    public CHARMMScript_DG(File _sessionDir, Settings _settings)
    {
        super(_sessionDir,_settings,ScriptNameKey);
    }

    @Override
    public void prepare_Python(CHARMM_Input input, File output) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CHARMM_Output execute(CHARMM_Input input) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

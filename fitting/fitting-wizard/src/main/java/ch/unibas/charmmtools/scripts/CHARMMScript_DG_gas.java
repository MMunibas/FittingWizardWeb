/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.scripts;

import ch.unibas.charmmtools.generate.inputs.CHARMM_Input;
import ch.unibas.fitting.shared.config.Settings;
import java.io.File;


public class CHARMMScript_DG_gas extends CHARMMScript_DG {

    public CHARMMScript_DG_gas(File _sessionDir, Settings _settings) {
        super(_sessionDir, _settings);
    }

    @Override
    public void prepare_Python(CHARMM_Input input, File output) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

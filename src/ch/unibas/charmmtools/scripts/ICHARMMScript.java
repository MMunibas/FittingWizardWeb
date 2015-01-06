/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.scripts;

import ch.unibas.fittingwizard.application.scripts.base.IScript;


/**
 *
 * @author hedin
 */
public interface ICHARMMScript extends IScript<CHARMM_input, CHARMM_output> {
    
    // prepare all required data
    public void prepareData();
    
    // prepare python call
    public void preparePython(String inpPath, String parPath, String topPath, String lpunPath);
}

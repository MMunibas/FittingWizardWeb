/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.scripts;

import ch.unibas.charmmtools.generate.outputs.CHARMM_Output;
import ch.unibas.charmmtools.generate.inputs.CHARMM_Input;
import ch.unibas.fittingwizard.application.scripts.base.IScript;


/**
 *
 * @author hedin
 */
public interface ICHARMMScript extends IScript<CHARMM_Input, CHARMM_Output> {
    
    // prepare all required data
    public void prepareData();
    
    /**
     * @return the ScriptFile name
     */
    public String getScriptFileName();
}

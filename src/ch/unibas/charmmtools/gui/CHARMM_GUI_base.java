/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui;

import ch.unibas.charmmtools.scripts.CHARMM_Input;
import ch.unibas.charmmtools.scripts.CHARMM_Output;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.presentation.base.WizardPage;
import java.io.File;

/**
 *
 * @author hedin
 */
public abstract class CHARMM_GUI_base extends WizardPage{
    
    protected RunCHARMMWorkflow charmmWorkflow;
    
    protected CHARMM_Input  inp;
    protected CHARMM_Output out;
    
    protected File CHARMM_inFile, CHARMM_outFile;
    
    public CHARMM_GUI_base(String title, RunCHARMMWorkflow flow) {
        super(title);
        this.charmmWorkflow = flow;
    }


    
}

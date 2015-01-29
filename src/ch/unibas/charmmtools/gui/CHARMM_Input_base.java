/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui;

import ch.unibas.charmmtools.scripts.CHARMM_input;
import ch.unibas.charmmtools.scripts.CHARMM_output;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.presentation.base.WizardPage;
import java.io.File;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public abstract class CHARMM_Input_base extends WizardPage{

//    protected static final Logger logger = Logger.getLogger(CHARMM_Input_base.class);
    
    protected RunCHARMMWorkflow charmmWorkflow;
    protected CHARMM_input  inp;
    protected CHARMM_output out;
    protected File CHARMM_inFile, CHARMM_outFile;
    
    public CHARMM_Input_base(String title, RunCHARMMWorkflow flow) {
        super(title);
        charmmWorkflow = flow;
    }


    
}

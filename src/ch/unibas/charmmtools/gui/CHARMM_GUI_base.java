/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui;

import ch.unibas.charmmtools.generate.inputs.CHARMM_Input;
import ch.unibas.charmmtools.generate.outputs.CHARMM_Output;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.presentation.base.WizardPage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hedin
 */
public abstract class CHARMM_GUI_base extends WizardPage  implements Serializable{
    
    protected transient RunCHARMMWorkflow charmmWorkflow;
    
    protected transient List<CHARMM_Input>  inp = new ArrayList<>();
    protected transient List<CHARMM_Output> out = new ArrayList<>();
    
    protected transient List<File> CHARMM_inFile = new ArrayList<>();
    protected transient List<File> CHARMM_outFile = new ArrayList<>();
    
    public CHARMM_GUI_base(String title, RunCHARMMWorkflow flow) {
        super(title);
        this.charmmWorkflow = flow;
        //this.logger.info("Style for class : '" + this.getClass().toGenericString() + "' is : " + this.getStyle());
    }

    protected abstract void serialize();
    
    protected abstract void unserialize();
    
}

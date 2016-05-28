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
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hedin
 */
public abstract class CHARMM_GUI_base extends WizardPage implements Serializable{
    
    protected RunCHARMMWorkflow charmmWorkflow;
    
    protected List<CHARMM_Input>  inp = new ArrayList<>();
    protected List<CHARMM_Output> out = new ArrayList<>();
    
    protected List<File> CHARMM_inFile = new ArrayList<>();
    protected List<File> CHARMM_outFile = new ArrayList<>();
    
    protected File work_directory;
    
    public CHARMM_GUI_base(String title, RunCHARMMWorkflow flow) {
        super(title);
        this.charmmWorkflow = flow;
        work_directory = charmmWorkflow.getWork_directory();
        //this.logger.info("Style for class : '" + this.getClass().toGenericString() + "' is : " + this.getStyle());
    }

    protected void serialize(String whoami, CHARMM_GUI_base obj )
    {
        
        ObjectOutputStream outser = null;
        try {
            File serFile = new File(whoami + ".ser");
            outser = new ObjectOutputStream(new FileOutputStream(serFile));
            outser.writeObject(obj);
            outser.close();
            logger.info("Serialized data is saved in " + serFile.getAbsolutePath());
            outser.close();
        } catch (Exception ex) {
            logger.error("Error while serializing object of class " + whoami + ex.getMessage());
        }
        
    }
    
    protected void unserialize()
    {
        
    }
    
}

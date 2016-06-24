/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.charmm;


import ch.unibas.fitting.shared.charmm.scripts.ICHARMMScript;
import ch.unibas.fitting.shared.charmm.generate.CHARMM_InOut;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input;

import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output;
import ch.unibas.fitting.shared.workflows.base.Workflow;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author hedin
 */
public class RunCHARMMWorkflow implements Workflow<CHARMM_Input, CHARMM_Output> {
    
    private static final Logger logger = Logger.getLogger(RunCHARMMWorkflow.class);
    
    private final List<ICHARMMScript> charmmScript = new ArrayList<>();
    
    private List<CHARMM_InOut> ioList;
    
    protected File work_directory;
    
    /**
     * if there is only one script registered for this workflow
     * @param chScr a charmm script
     */
    public RunCHARMMWorkflow(ICHARMMScript chScr){
        charmmScript.clear();
        this.charmmScript.add(chScr);
        this.ioList = new ArrayList<>();
        work_directory = chScr.getWorkingDirectory();
    }
        
    /**
     * for adding multiple CHARMM scripts to this workflow
     * @param chScr a charmm script
     */
    public RunCHARMMWorkflow(ICHARMMScript... chScr){
        charmmScript.clear();
        this.charmmScript.addAll(Arrays.asList(chScr));
        this.ioList = new ArrayList<>();
    }
    
    public void setIOList(List<CHARMM_InOut> inpList)
    {
        this.ioList.addAll(inpList);
    }
    
    public List<CHARMM_InOut> getIOList( List<CHARMM_InOut> inpList)
    {
        return ioList;
    }
    
    @Override
    public CHARMM_Output execute(WorkflowContext<CHARMM_Input> status) {
        
        ICHARMMScript myScript = charmmScript.get(0);
                
        logger.info("Executing CHARMM workflow ...");
        status.setCurrentStatus("Executing " + myScript.getScriptFileName() + " ...");
        
        CHARMM_Output out = myScript.execute(status.getParameter());
        
        return out;
    }
    
    public List<CHARMM_Output> execute(List< WorkflowContext<CHARMM_Input> > status) {
        
        List<CHARMM_Output> out = new ArrayList<>();
        
        logger.info("Executing CHARMM workflow ...");
        
        int idx_scr=charmmScript.size(), idx_cont=status.size();
        
        if(idx_scr!=idx_cont)
            throw new Error("When using the multi argument execute() for RunCHARMMWorkflow, it seems"
                    + "that lists of scripts and context have different sizes !");
            
        for(WorkflowContext<CHARMM_Input> cont : status){
            ICHARMMScript myScript = charmmScript.get(idx_scr);
            cont.setCurrentStatus("Executing " + myScript.getScriptFileName() + " ...");
            out.add( myScript.execute(cont.getParameter()) );
            idx_scr++;
        }
        
        return out;
    }
    
    public List<CHARMM_Output> execute(){
        List<CHARMM_Output> out = new ArrayList<>();
        return out;
    }

    /**
     * @return the work_directory
     */
    public File getWork_directory() {
        return work_directory;
    }

}

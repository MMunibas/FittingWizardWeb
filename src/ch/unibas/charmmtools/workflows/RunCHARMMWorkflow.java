/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.workflows;

import ch.unibas.charmmtools.generate.CHARMM_InOut;
import ch.unibas.charmmtools.generate.inputs.CHARMM_Input;
import ch.unibas.charmmtools.generate.outputs.CHARMM_Output;
import ch.unibas.charmmtools.scripts.ICHARMMScript;
import ch.unibas.fittingwizard.application.workflows.base.Workflow;
import ch.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public class RunCHARMMWorkflow extends Workflow<CHARMM_Input, CHARMM_Output> {
    
    private static final Logger logger = Logger.getLogger(RunCHARMMWorkflow.class);
    
    private final List<ICHARMMScript> charmmScript = new ArrayList<>();
    
    private List<CHARMM_InOut> ioList;
    
    /**
     * if there is only one script registered for this workflow
     * @param chScr a charmm script
     */
    public RunCHARMMWorkflow(ICHARMMScript chScr){
        charmmScript.clear();
        this.charmmScript.add(chScr);
        this.ioList = new ArrayList<>();
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

}

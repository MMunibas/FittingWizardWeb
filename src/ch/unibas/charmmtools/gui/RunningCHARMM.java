/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui;

import ch.unibas.charmmtools.scripts.CHARMM_inout;
import ch.unibas.charmmtools.scripts.CHARMM_input;
import ch.unibas.charmmtools.scripts.CHARMM_output;
import ch.unibas.charmmtools.scripts.RealCHARMMScript;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import ch.unibas.fittingwizard.presentation.base.progress.Context;
import ch.unibas.fittingwizard.presentation.base.progress.ProgressPage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hedin
 */
public class RunningCHARMM extends ProgressPage{

    private final RunCHARMMWorkflow cflow;
    private CHARMM_input  inp;
    private CHARMM_output out;
    
    public RunningCHARMM(RunCHARMMWorkflow charmmWorkflow, List<CHARMM_inout> myList) {
        super("Running CHARMM calculation");
        this.cflow = charmmWorkflow;
        this.inp = (CHARMM_input)  myList.get(0);
        this.out = (CHARMM_output) myList.get(1);  
    }

    @Override
    protected boolean run(Context ctx) throws Exception {
        out = cflow.execute(new WorkflowContext<CHARMM_input>() {

            @Override
            public void setCurrentStatus(String status) {
                ctx.setTitle(status);
            }

            @Override
            public CHARMM_input getParameter() {
                return inp;
            }

        });
        
//        Thread.sleep(2000);
        
        return true;
    }

    @Override
    protected void handleCanceled() {
        List<CHARMM_inout> myList = new ArrayList<CHARMM_inout>();
        myList.add(0, inp);
        myList.add(1, out);
        
        logger.info("Run canceled by user : going back to CHARMM input assistant Step1.");
        navigateTo(CHARMM_GUI_Step1.class,myList);
    }

    @Override
    protected void handleFinishedRun(boolean successful) {
        List<CHARMM_inout> myList = new ArrayList<CHARMM_inout>();
        myList.add(0, inp);
        myList.add(1, out);
        
        if(successful)
        {
            logger.info("Going to CHARMM input assistant Step 2");
            navigateTo(CHARMM_GUI_Step2.class,myList);
        }
        else{
            logger.info("CHARMM run failed : going back to CHARMM input assistant Step1.");
            navigateTo(CHARMM_GUI_Step1.class,myList);
        }
        
    }
    
}

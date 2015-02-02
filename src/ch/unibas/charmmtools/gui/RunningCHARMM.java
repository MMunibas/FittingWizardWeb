/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui;

import ch.unibas.charmmtools.gui.step2.CHARMM_GUI_Step2;
import ch.unibas.charmmtools.gui.step1.CHARMM_GUI_Step1;
import ch.unibas.charmmtools.scripts.CHARMM_InOut;
import ch.unibas.charmmtools.scripts.CHARMM_Input;
import ch.unibas.charmmtools.scripts.CHARMM_Output;
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
    private CHARMM_Input  inp;
    private CHARMM_Output out;
    
    public RunningCHARMM(RunCHARMMWorkflow charmmWorkflow, List<CHARMM_InOut> myList) {
        super("Running CHARMM calculation");
        this.cflow = charmmWorkflow;
        this.inp = (CHARMM_Input)  myList.get(0);
        this.out = (CHARMM_Output) myList.get(1);  
    }

    @Override
    protected boolean run(Context ctx) throws Exception {
        out = cflow.execute(new WorkflowContext<CHARMM_Input>() {

            @Override
            public void setCurrentStatus(String status) {
                ctx.setTitle(status);
            }

            @Override
            public CHARMM_Input getParameter() {
                return inp;
            }

        });
        
        return true;
    }

    @Override
    protected void handleCanceled() {
        List<CHARMM_InOut> myList = new ArrayList<CHARMM_InOut>();
        myList.add(0, inp);
        myList.add(1, out);
        
        logger.info("Run canceled by user : going back to CHARMM input assistant Step1.");
        navigateTo(CHARMM_GUI_Step1.class,myList);
    }

    @Override
    protected void handleFinishedRun(boolean successful) {
        List<CHARMM_InOut> myList = new ArrayList<CHARMM_InOut>();
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

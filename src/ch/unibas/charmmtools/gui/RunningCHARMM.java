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
    private List<CHARMM_Input > inp = new ArrayList<>();
    private List<CHARMM_Output> out = new ArrayList<>();
    
    public RunningCHARMM(RunCHARMMWorkflow charmmWorkflow, List<CHARMM_InOut> ioList) {
        super("Running CHARMM calculation");
        this.cflow = charmmWorkflow;
        
//        this.inp = new ArrayList<>();
//        this.out = new ArrayList<>();
        
//        for (CHARMM_InOut ioListIt : ioList) {
//            if (ioListIt instanceof CHARMM_Input) {
//                inp.add((CHARMM_Input) ioListIt);
//            } else if (ioListIt instanceof CHARMM_Output) {
//                out.add((CHARMM_Output) ioListIt);
//            } else {
//                throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : got " + ioListIt.getClass() + " but expected types are " + CHARMM_Input.class + " or " + CHARMM_Output.class);
//            }
//        }
                
        for (CHARMM_InOut ioListIt : ioList) {
            
            Class c = ioListIt.getClass();
            Class sc = c.getSuperclass();
            
            if (sc == CHARMM_Input.class) {
                inp.add((CHARMM_Input) ioListIt);
            } else if (sc == CHARMM_Output.class) {
                out.add((CHARMM_Output) ioListIt);
            } else {
                throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : get " + ioListIt.getClass() + " but expected types are " + CHARMM_Input.class + " or " + CHARMM_Output.class);
            }
        }

    }

    @Override
    protected boolean run(Context ctx) throws Exception {
        
        out.add(0, cflow.execute
            (new WorkflowContext<CHARMM_Input>() {

                @Override
                public void setCurrentStatus(String status) {
                    ctx.setTitle(status);
                }

                @Override
                public CHARMM_Input getParameter() {
                    return inp.get(0);
                }

            })
        );
//        logger.info(out.get(0).getTextOut());
        
        out.add(1, cflow.execute
            (new WorkflowContext<CHARMM_Input>() {

                @Override
                public void setCurrentStatus(String status) {
                    ctx.setTitle(status);
                }

                @Override
                public CHARMM_Input getParameter() {
                    return inp.get(1);
                }

            })
        );
//        logger.info(out.get(1).getTextOut());
        
        return true;
    }

    @Override
    protected void handleCanceled() {
        List<CHARMM_InOut> myList = new ArrayList<>();
        myList.addAll(inp);
        myList.addAll(out);
        
        logger.info("Run canceled by user : going back to CHARMM input assistant Step1.");
        navigateTo(CHARMM_GUI_Step1.class,myList);
    }

    @Override
    protected void handleFinishedRun(boolean successful) {
        List<CHARMM_InOut> myList = new ArrayList<>();
        myList.addAll(inp);
        myList.addAll(out);
        
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

/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui;

import ch.unibas.charmmtools.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.charmmtools.gui.step4.CHARMM_GUI_Step4;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.presentation.base.progress.Context;
import ch.unibas.fittingwizard.presentation.base.progress.ProgressPage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hedin
 */
public class RunningCHARMM_DG extends ProgressPage{

    private final RunCHARMMWorkflow cflow;
    private List<CHARMM_Generator_DGHydr> myList;
    
    public RunningCHARMM_DG(RunCHARMMWorkflow charmmWorkflow, List<CHARMM_Generator_DGHydr> simList) {
        super("Running CHARMM calculation");
                
        this.cflow = charmmWorkflow;
        
        this.myList = new ArrayList<>();
        this.myList.addAll(simList);


    }

    /**
     *
     * @param ctx
     * @return
     * @throws Exception
     */
    @Override
    protected boolean run(Context ctx) throws Exception {
        
        for (CHARMM_Generator_DGHydr script : myList){
            String msg = "Now running " + script.Whoami();
            logger.info(msg);
            this.lblTitle.textProperty().setValue(msg);
            script.run();
        }
        
        return true;
    }

    @Override
    protected void handleCanceled() {
        logger.info("Run canceled by user before normal termination");
        navigateTo(CHARMM_GUI_Step4.class,false);
    }

    @Override
    protected void handleFinishedRun(boolean successful) {
        logger.info("Normal termination");
        navigateTo(CHARMM_GUI_Step4.class,true);
    }

  
}

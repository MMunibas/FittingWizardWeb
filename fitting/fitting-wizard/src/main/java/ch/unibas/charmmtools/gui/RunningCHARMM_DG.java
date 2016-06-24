/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui;

import ch.unibas.fitting.shared.charmm.generate.CHARMM_InOut;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.charmmtools.gui.step1.mdAssistant.CHARMM_GUI_InputAssistant;
import ch.unibas.charmmtools.gui.step3.showResults.CHARMM_GUI_ShowResults;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.gaussian.base.progress.Context;
import ch.unibas.fittingwizard.gaussian.base.progress.ProgressPage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hedin
 */
public class RunningCHARMM_DG extends ProgressPage{

    private final RunCHARMMWorkflow cflow;
    private List<CHARMM_InOut> ioList;
    private List<CHARMM_Generator_DGHydr> dgList;
//    private double myDgContribution = 0.0;
    
    public RunningCHARMM_DG(RunCHARMMWorkflow charmmWorkflow, List<CHARMM_Generator_DGHydr> simList) {
        super("Running CHARMM calculation");
                
        this.cflow = charmmWorkflow;
        
        this.dgList = new ArrayList<>();
        this.dgList.addAll(simList);

        this.ioList = new ArrayList<>();
        this.ioList.addAll(cflow.getIOList(ioList));
        
//        this.setIOList(cflow.getIOList(ioList));

    }
        
//    public RunningCHARMM_DG(RunCHARMMWorkflow charmmWorkflow, List<CHARMM_Generator_DGHydr> simList, List<CHARMM_InOut> inpList) {
//        super("Running CHARMM calculation");
//                
//        this.cflow = charmmWorkflow;
//        
//        this.dgList = new ArrayList<>();
//        this.dgList.addAll(simList);
//
//        this.ioList = new ArrayList<>();
//        this.ioList.addAll(inpList);
//
//    }
    
//    private void setIOList( List<CHARMM_InOut> inpList)
//    {
//        this.ioList.addAll(inpList);
//    }

    /**
     *
     * @param ctx
     * @return
     * @throws Exception
     */
    @Override
    protected boolean run(Context ctx) throws Exception {
        
        for (CHARMM_Generator_DGHydr script : dgList){
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
//        navigateTo(CHARMM_GUI_Step4.class,ioList);
//        navigateTo(CHARMM_GUI_ShowOutput.class, i);
        navigateTo(CHARMM_GUI_InputAssistant.class,null);
    }

    @Override
    protected void handleFinishedRun(boolean successful) {
        logger.info("Normal termination");
//        navigateTo(CHARMM_GUI_Step4.class,ioList);
//        getDgContribution();
        ioList.addAll(dgList);
        navigateTo(CHARMM_GUI_ShowResults.class,ioList);
    }
    
//    private void getDgContribution(){
//        
//        
//        
//    }

  
}

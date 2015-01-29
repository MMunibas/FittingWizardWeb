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
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.presentation.base.progress.Context;
import ch.unibas.fittingwizard.presentation.base.progress.ProgressPage;
import java.util.List;

/**
 *
 * @author hedin
 */
public class RunningCHARMM extends ProgressPage{

    public RunningCHARMM(RunCHARMMWorkflow charmmWorkflow, List<CHARMM_inout> myList) {
        super("Running CHARMM calculation");
    }

    @Override
    protected boolean run(Context ctx) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void handleCanceled() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void handleFinishedRun(boolean successful) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

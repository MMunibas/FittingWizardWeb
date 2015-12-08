/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.presentation.base.progress;

import ch.unibas.fittingwizard.presentation.base.progress.Context;
import ch.unibas.fittingwizard.presentation.base.progress.ProgressPageWithQstat;

/**
 *
 * @author hedin
 */
public class ProgressWithQstatTest extends ProgressPageWithQstat{

    public ProgressWithQstatTest() {
        super("Test window");
    }

    @Override
    protected boolean run(Context ctx) throws Exception {
        return true;
    }

    @Override
    protected void handleCanceled() {
    }

    @Override
    protected void handleFinishedRun(boolean successful) {
    }
    
}

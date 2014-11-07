/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.presentation;

import ch.unibas.fittingwizard.application.Visualization;
import ch.unibas.fittingwizard.presentation.base.WizardPageWithVisualization;

/**
 *
 * @author hedin
 */
public class CHARMM_Input_Page extends WizardPageWithVisualization {

    public CHARMM_Input_Page(Visualization visualization, String title) {
        super(visualization, title);
    }

    @Override
    protected void fillButtonBar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

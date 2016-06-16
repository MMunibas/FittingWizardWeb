/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.base;

import ch.unibas.fittingwizard.gaussian.Visualization;

/**
 * User: mhelmer
 * Date: 10.12.13
 * Time: 11:57
 */
public abstract class WizardPageWithVisualization extends WizardPage {

    protected final Visualization visualization;

    public WizardPageWithVisualization(Visualization visualization, String title) {
        super(title);
        this.visualization = visualization;
    }

    @Override
    public void navigateTo(Class type, Object parameter) {
        closeVisualization();
        super.navigateTo(type, parameter);
    }

    protected void closeVisualization() {
        if (visualization != null)
            visualization.close();
    }
}

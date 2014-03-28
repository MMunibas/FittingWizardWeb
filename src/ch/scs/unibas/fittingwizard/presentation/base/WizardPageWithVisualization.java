package ch.scs.unibas.fittingwizard.presentation.base;

import ch.scs.unibas.fittingwizard.application.Visualization;

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

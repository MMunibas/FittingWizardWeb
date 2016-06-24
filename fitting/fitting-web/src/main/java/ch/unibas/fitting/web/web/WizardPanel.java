package ch.unibas.fitting.web.web;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by tobias on 23.06.16.
 */
public abstract class WizardPanel extends Panel {

    protected final org.apache.log4j.Logger Logger;

    public WizardPanel(String id) {
        super(id);
        Logger = org.apache.log4j.Logger.getLogger(this.getClass());
    }
}

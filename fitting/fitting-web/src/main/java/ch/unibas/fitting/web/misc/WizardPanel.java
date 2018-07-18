package ch.unibas.fitting.web.misc;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by tobias on 23.06.16.
 */
public abstract class WizardPanel extends Panel {

    protected final static Logger LOGGER = Logger.getLogger(WizardPanel.class);

    public WizardPanel(String id) {
        super(id);
    }
}

/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.presentation.base.dialog;

import ch.unibas.fittingwizard.presentation.base.FxmlUtil;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 10:31
 */
public class ModalDialog extends Stage {
    protected final Logger logger;

    public ModalDialog(String title) {
        logger = Logger.getLogger(getClass());
        setTitle(title);
        initModality(Modality.APPLICATION_MODAL);
        createScene();
    }

    private void createScene() {
        logger.debug("Creating scene.");
        Parent fxmlContent = FxmlUtil.getFxmlContent(getClass(), this);
        setScene(new Scene(fxmlContent));
    }
}

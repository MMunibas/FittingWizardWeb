/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database;

import ch.unibas.charmmtools.gui.database.dataModel.DB_model;
import ch.unibas.charmmtools.gui.step1.mdAssistant.CHARMM_GUI_InputAssistant;
import ch.unibas.fittingwizard.Settings;
import ch.unibas.fittingwizard.application.Visualization;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 *
 * @author hedin
 */
public class DB_SelectForCHARMM extends DB_Window {

    private static final String title = "Selecting a compound from database";

    public DB_SelectForCHARMM(Visualization visualization, Settings _settings) {
        super(visualization, title, _settings);
        removeButtonFromButtonBar(button_initialSelection);
    }

    protected DB_model selectCompound(){
        return this.tabview_db.getSelectionModel().getSelectedItem();
    }
    
    @Override
    protected void fillButtonBar() {
        Button select_compound = ButtonFactory.createButtonBarButton("Select compound properties",
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        logger.info("Selected a compound" + selectCompound().toString());
                        navigateTo(CHARMM_GUI_InputAssistant.class, logger);
                    }
                });
        addButtonToButtonBar(select_compound);
    }

}

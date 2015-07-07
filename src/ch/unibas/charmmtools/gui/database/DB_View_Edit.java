/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database;

import ch.unibas.fittingwizard.Settings;
import ch.unibas.fittingwizard.application.Visualization;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * This class allows the user to search for a compound in a given database instead of providing himself guess values for
 * density, deltaH and deltaG
 *
 * @author hedin
 */
public class DB_View_Edit extends DB_Window {

    private static final String title = "Explore database of compounds and their properties";

    public DB_View_Edit(Visualization visualization, Settings _settings) {
        super(visualization, title, _settings);
    }

    @Override
    protected void fillButtonBar() {
        Button add_to_db = ButtonFactory.createButtonBarButton("Add compound to database",
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        logger.info("Adding compound to Database");
                    }
                });
        addButtonToButtonBar(add_to_db);
        add_to_db.setDisable(true);
        
        Button edit_db = ButtonFactory.createButtonBarButton("Edit compound",
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        logger.info("Editing Database");
                    }
                });
        addButtonToButtonBar(edit_db);
        edit_db.setDisable(true);
    }

}

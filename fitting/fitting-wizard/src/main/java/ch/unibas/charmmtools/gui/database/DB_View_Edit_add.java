/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database;

import ch.unibas.charmmtools.gui.database.add.DB_add;
import ch.unibas.charmmtools.gui.database.edit.DB_edit;
import ch.unibas.charmmtools.gui.database.dataModel.DB_model;
import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fittingwizard.gaussian.base.ButtonFactory;
import ch.unibas.fittingwizard.gaussian.base.dialog.OverlayDialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * This class allows the user to search for a compound in a given database
 * instead of providing himself guess values for density, deltaH and deltaG
 *
 * @author hedin
 */
public class DB_View_Edit_add extends DB_Window {

    private static final String title = "Explore database of compounds and their properties";

    public DB_View_Edit_add(Settings _settings) {
        super(title, _settings);
    }

    @Override
    protected void fillButtonBar() {
        Button add_to_db = ButtonFactory.createButtonBarButton("Add compound to database",
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Adding compound to Database");
                DB_model modelToAdd = new DB_model();
                DB_add addWindow = new DB_add(modelToAdd);
                addWindow.add();
                if(addWindow.addingOK())
                {
                    dbi.addCompound(modelToAdd);
                }
            }
        });
        addButtonToButtonBar(add_to_db);

        Button edit_db = ButtonFactory.createButtonBarButton("Edit compound",
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Editing Database");

                if (tabview_db.getItems().size() > 0) {
                    DB_model modelToEdit = tabview_db.getSelectionModel().getSelectedItem();

                    if (modelToEdit == null) {
                        OverlayDialog.informUser("Nothing to edit !", "Please choose a compound in the table before pressing the edit button  !");
                    } else {
                        DB_edit editor = new DB_edit(modelToEdit);
                        editor.edit();
                        if (editor.wasUpdated()) {
                            dbi.updateRecord(modelToEdit);
                        }
                    }
                } else {
                    OverlayDialog.informUser("Nothing to edit !", "Please perform a search before pressing the edit button so that a compound "
                            + "could be selected !");
                }

            }
        });
        addButtonToButtonBar(edit_db);
    }

}

/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database.add;

import ch.unibas.charmmtools.gui.database.dataModel.DB_model;
import ch.unibas.fittingwizard.presentation.base.dialog.ModalDialog;
import ch.unibas.fittingwizard.presentation.base.ui.MainWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

/**
 * For editing a record of the compounds database
 *
 * @author hedin
 */
public class DB_add extends ModalDialog {

    @FXML // fx:id="text_idpubchem"
    private TextField text_idpubchem; // Value injected by FXMLLoader

    @FXML // fx:id="text_name"
    private TextField text_name; // Value injected by FXMLLoader

    @FXML // fx:id="text_formula"
    private TextField text_formula; // Value injected by FXMLLoader

    @FXML // fx:id="text_inchi"
    private TextField text_inchi; // Value injected by FXMLLoader

    @FXML // fx:id="text_smiles"
    private TextField text_smiles; // Value injected by FXMLLoader
    
    @FXML // fx:id="text_mass"
    private TextField text_mass; // Value injected by FXMLLoader

    @FXML // fx:id="text_density"
    private TextField text_density; // Value injected by FXMLLoader

    @FXML // fx:id="text_dh"
    private TextField text_dh; // Value injected by FXMLLoader

    @FXML // fx:id="text_dg"
    private TextField text_dg; // Value injected by FXMLLoader
    
    @FXML
    private TextField text_refdh;

    @FXML
    private TextField text_refdg;

    private final Window primary;

    private DB_model model = null;
    
    private boolean readyToAdd=false;

    public DB_add(DB_model _mod) {
        super("Adding a compound to DB...");

        primary = MainWindow.getPrimaryStage().getScene().getWindow();
        primary.getScene().getRoot().setEffect(new BoxBlur());

        this.model = _mod;
    }

    public void add() {

        showAndWait();
        
        readyToAdd = false;
        
        primary.getScene().getRoot().setEffect(null);
    }
    
    @FXML
    protected void checkNotEmptyTextField(KeyEvent event)
    {
        TextField field = (TextField) event.getSource();
        if (field.getText().length()==0)
        {
            field.setStyle("-fx-border-color:red ;"
                    + " -fx-border-width:2 ;"
                    + " -fx-border-radius:3");
        }
        else
        {
            field.setStyle("-fx-border-color:green ;"
                    + " -fx-border-width:2 ;"
                    + " -fx-border-radius:3");
        }

    }

    @FXML
    protected void insert(ActionEvent event) {
        logger.info("Attempting compound insert in DB...");
        primary.getScene().getRoot().setEffect(null);
        readyToAdd = true;
        close();
    }
    
    @FXML
    protected void cancel(ActionEvent event) {
        logger.info("Cancelled compound insertion in DB...");
        primary.getScene().getRoot().setEffect(null);
        readyToAdd = false;
        close();
    }


}

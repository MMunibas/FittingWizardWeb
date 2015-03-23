/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database;

import ch.unibas.fittingwizard.presentation.base.WizardPage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 *
 * @author hedin
 */
public class CHARMM_GUI_db extends WizardPage {

    private static final String title = "LJ fitting procedure : find compound in database";

    @FXML // fx:id="tabcol_mass"
    private TableColumn<?, ?> tabcol_mass; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_dg"
    private TableColumn<?, ?> tabcol_dg; // Value injected by FXMLLoader

    @FXML // fx:id="text_mass"
    private TextField text_mass; // Value injected by FXMLLoader

    @FXML // fx:id="text_formula"
    private TextField text_formula; // Value injected by FXMLLoader

    @FXML // fx:id="search_bymass"
    private Button search_bymass; // Value injected by FXMLLoader

    @FXML // fx:id="text_smiles"
    private TextField text_smiles; // Value injected by FXMLLoader

    @FXML // fx:id="search_bysmiles"
    private Button search_bysmiles; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_dh"
    private TableColumn<?, ?> tabcol_dh; // Value injected by FXMLLoader

    @FXML // fx:id="search_byformula"
    private Button search_byformula; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_formula"
    private TableColumn<?, ?> tabcol_formula; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_smiles"
    private TableColumn<?, ?> tabcol_smiles; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_name"
    private TableColumn<?, ?> tabcol_name; // Value injected by FXMLLoader

    @FXML // fx:id="tabview_db"
    private TableView<?> tabview_db; // Value injected by FXMLLoader

    @FXML // fx:id="text_mass_threshold"
    private TextField text_mass_threshold; // Value injected by FXMLLoader

    @FXML // fx:id="text_fullname"
    private TextField text_fullname; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_density"
    private TableColumn<?, ?> tabcol_density; // Value injected by FXMLLoader

    @FXML // fx:id="search_byname"
    private Button search_byname; // Value injected by FXMLLoader
    
    private DB_interface dbi;

    public CHARMM_GUI_db() {
        super(title);
        dbi = new DB_interface();
    }

    @Override
    protected void fillButtonBar() {
        
    }

}

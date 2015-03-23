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
import java.util.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * This class allows the user to search for a compound in a given database instead of providing himself guess values for
 * density, deltaH and deltaG
 *
 * @author hedin
 */
public class CHARMM_GUI_db extends WizardPage {

    private static final String title = "LJ fitting procedure : find compound in database";

    @FXML // fx:id="tabcol_mass"
    private TableColumn<DB_model, String> tabcol_mass; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_dg"
    private TableColumn<DB_model, String> tabcol_dg; // Value injected by FXMLLoader

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
    private TableColumn<DB_model, String> tabcol_dh; // Value injected by FXMLLoader

    @FXML // fx:id="search_byformula"
    private Button search_byformula; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_formula"
    private TableColumn<DB_model, String> tabcol_formula; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_smiles"
    private TableColumn<DB_model, String> tabcol_smiles; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_name"
    private TableColumn<DB_model, String> tabcol_name; // Value injected by FXMLLoader

    @FXML // fx:id="tabview_db"
    private TableView<DB_model> tabview_db; // Value injected by FXMLLoader

    @FXML // fx:id="text_mass_threshold"
    private TextField text_mass_threshold; // Value injected by FXMLLoader

    @FXML // fx:id="text_fullname"
    private TextField text_fullname; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_density"
    private TableColumn<DB_model, String> tabcol_density; // Value injected by FXMLLoader

    @FXML // fx:id="search_byname"
    private Button search_byname; // Value injected by FXMLLoader

    private DB_interface dbi;
    
    private ObservableList<DB_model> obsList;

    public CHARMM_GUI_db() {
        super(title);
        dbi = new DB_interface();
    }

    @Override
    public void initializeData() {
        tabcol_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tabcol_formula.setCellValueFactory(new PropertyValueFactory<>("formula"));
        tabcol_smiles.setCellValueFactory(new PropertyValueFactory<>("smiles"));
        tabcol_mass.setCellValueFactory(new PropertyValueFactory<>("mass"));
        tabcol_density.setCellValueFactory(new PropertyValueFactory<>("density"));
        tabcol_dh.setCellValueFactory(new PropertyValueFactory<>("dh"));
        tabcol_dg.setCellValueFactory(new PropertyValueFactory<>("dg"));
        
        obsList = FXCollections.observableArrayList();
        tabview_db.getItems().addAll(obsList);
    }
    
    

    /**
     * Cleans the tableview before adding more stuff
     *
     * @TODO
     */
    protected void cleanTableView() {
        tabview_db.getItems().removeAll(obsList);
        obsList.removeAll(obsList);
    }

    /**
     * When one of the search button is pressed call the appropriate findBy from DB_interace for performing the DB
     * search
     *
     * @param event
     */
    @FXML
    protected void searchButtonPressed(ActionEvent event) {

        // first clean table view before adding something
        cleanTableView();

        if (event.getSource().equals(search_byname)) {
            // search for a compound by name in DB
            obsList.addAll(dbi.findByName(text_fullname.getText()));
            tabview_db.getItems().addAll(obsList);
        } else if (event.getSource().equals(search_byformula)) {
            // search for a compound by formula in DB
            tabview_db.getItems().addAll(
                dbi.findByFormula(text_formula.getText())
            );
        } else if (event.getSource().equals(search_bysmiles)) {
            // search for a compound by SMILES notation in DB
            tabview_db.getItems().addAll(
                dbi.findBySMILES(text_smiles.getText())
            );
        } else if (event.getSource().equals(search_bymass)) {
            // search for a compound by Mass in DB
            tabview_db.getItems().addAll(
                dbi.findByMASS(Double.valueOf(text_mass.getText()), Double.valueOf(text_mass_threshold.getText()))
            );
        } else {
            throw new UnknownError("Unknown Event in searchButtonPressed(ActionEvent event)");
        }

    }

    @Override
    protected void fillButtonBar() {

    }

 

   

}

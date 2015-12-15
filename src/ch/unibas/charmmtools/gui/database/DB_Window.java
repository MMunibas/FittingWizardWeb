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
import ch.unibas.charmmtools.gui.database.interfaces.DB_interface;
import ch.unibas.charmmtools.gui.database.interfaces.MYSQL_DB_interface;
import ch.unibas.charmmtools.gui.database.interfaces.SQLITE_DB_interface;
import ch.unibas.fittingwizard.Settings;
import ch.unibas.fittingwizard.WhereToGo;
import ch.unibas.fittingwizard.presentation.base.WizardPage;
import ch.unibas.fittingwizard.presentation.base.dialog.OverlayDialog;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author hedin
 */
public abstract class DB_Window extends WizardPage {

    protected Settings settings;

    @FXML // fx:id="tabcol_mass"
    protected TableColumn<DB_model, String> tabcol_mass; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_dg"
    protected TableColumn<DB_model, String> tabcol_dg; // Value injected by FXMLLoader

    @FXML // fx:id="text_value"
    protected TextField text_value; // Value injected by FXMLLoader

    @FXML // fx:id="text_formula"
    protected TextField text_formula; // Value injected by FXMLLoader

    @FXML // fx:id="search_byvalue"
    protected Button search_byvalue; // Value injected by FXMLLoader

    @FXML // fx:id="text_smiles"
    protected TextField text_smiles; // Value injected by FXMLLoader

    @FXML // fx:id="search_bysmiles"
    protected Button search_bysmiles; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_dh"
    protected TableColumn<DB_model, String> tabcol_dh; // Value injected by FXMLLoader

    @FXML // fx:id="search_byformula"
    protected Button search_byformula; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_formula"
    protected TableColumn<DB_model, String> tabcol_formula; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_smiles"
    protected TableColumn<DB_model, String> tabcol_smiles; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_name"
    protected TableColumn<DB_model, String> tabcol_name; // Value injected by FXMLLoader

    @FXML // fx:id="tabview_db"
    protected TableView<DB_model> tabview_db; // Value injected by FXMLLoader

    @FXML // fx:id="text_value_threshold"
    protected TextField text_value_threshold; // Value injected by FXMLLoader

    @FXML // fx:id="text_fullname"
    protected TextField text_fullname; // Value injected by FXMLLoader

    @FXML // fx:id="tabcol_density"
    protected TableColumn<DB_model, String> tabcol_density; // Value injected by FXMLLoader

    @FXML // fx:id="search_byname"
    protected Button search_byname; // Value injected by FXMLLoader

    @FXML // fx:id="connectionLabel"
    protected Label connectionLabel; // Value injected by FXMLLoader

    @FXML // fx:id="combo_value"
    private ComboBox<String> combo_value; // Value injected by FXMLLoader

    protected DB_interface dbi;

    protected ObservableList<DB_model> obsList;

    private final ObservableList<String> combo_options
            = FXCollections.observableArrayList(
                    "Mass",
                    "Density",
                    "ΔH",
                    "ΔG"
            );

//    File smi = null;
//    File xyz = null;
//    BabelConverterAPI converter = null;
    public DB_Window(String title, Settings _settings) {

        super(title);
        this.settings = _settings;

        String DB_type = settings.getValue("DB.type");
        String DB_conn = settings.getValue("DB.connect");
        String DB_user = settings.getValue("DB.user");
        String DB_pass = settings.getValue("DB.password");

        // First try MySQL
        if (DB_type.compareToIgnoreCase("mysql") == 0) {

            try {
                dbi = new MYSQL_DB_interface(DB_conn, DB_user, DB_pass);
            } catch (SQLException ex) {
                logger.info("Either MySQL parameters undefined or they are wrong ; ask user what to do.");
                boolean rep = OverlayDialog.askYesOrNo("Unable to connect to the external Database. Try the local one instead ?");
                logger.info("Local DB : user said " + rep);
                if (rep == true) {
                    String DB_type2 = settings.getValue("DB.type.local");
                    String DB_conn2 = settings.getValue("DB.connect.local");
                    dbi = new SQLITE_DB_interface(DB_conn2);
                } else {
                    navigateTo(WhereToGo.class, null);
                }
            }

        } else {
            String DB_type2 = settings.getValue("DB.type.local");
            String DB_conn2 = settings.getValue("DB.connect.local");
            dbi = new SQLITE_DB_interface(DB_conn2);
        }

        this.connectionLabel.setText(dbi.getConnectionName());

//        boolean trySqlite = OverlayDialog.askYesOrNo("Problem when attempting to connect to external DB : trying local database instead ?");
//        try {
//            smi = File.createTempFile("tempMol", ".smi");
//            xyz = File.createTempFile("tempMol", ".xyz");
//            Writer str = new BufferedWriter(new FileWriter(smi));
//            str.write("CCO");
//            str.close();
//        } catch (IOException e) {
//            logger.error("Error while writing temp files : " + e.getMessage());
//        }
//
//        converter = new BabelConverterAPI("smi", "xyz");
//        converter.convert(smi.getAbsolutePath(), xyz.getAbsolutePath());
//        visualization.show(xyz);
    }

    @Override
    public void initializeData() {

        combo_value.setItems(combo_options);
        combo_value.setValue(combo_options.get(0));

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
     * When one of the search button is pressed call the appropriate findBy from
     * DB_interace for performing the DB search
     *
     * @param event
     */
    @FXML
    protected void searchButtonPressed(ActionEvent event) {

        // first clean table view before adding something
        cleanTableView();

        if (event.getSource().equals(search_byname) || event.getSource().equals(text_fullname)) {

            // search for a compound by name in DB
            obsList.addAll(dbi.findByName(text_fullname.getText()));
            tabview_db.getItems().addAll(obsList);

        } else if (event.getSource().equals(search_byformula) || event.getSource().equals(text_formula)) {

            // search for a compound by formula in DB
            obsList.addAll(dbi.findByFormula(text_formula.getText()));
            tabview_db.getItems().addAll(obsList);

        } else if (event.getSource().equals(search_bysmiles) || event.getSource().equals(text_smiles)) {

            // search for a compound by SMILES notation in DB
            obsList.addAll(dbi.findBySMILES(text_smiles.getText()));
            tabview_db.getItems().addAll(obsList);

        } else if (event.getSource().equals(search_byvalue) || event.getSource().equals(text_value) || event.getSource().equals(text_value_threshold)) {

            // search for a compound by value in DB
            obsList.addAll(
                    dbi.findByValue(
                            combo_value.getValue(), Double.valueOf(text_value.getText()), Double.valueOf(text_value_threshold.getText())
                    )
            );
            tabview_db.getItems().addAll(obsList);

        } else {
            throw new UnknownError("Unknown Event in searchButtonPressed(ActionEvent event)");
        }

    }

}

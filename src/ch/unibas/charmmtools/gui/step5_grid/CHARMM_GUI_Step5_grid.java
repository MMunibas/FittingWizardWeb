/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step5_grid;

import ch.unibas.fittingwizard.presentation.base.WizardPage;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

/**
 *
 * @author hedin
 */
public class CHARMM_GUI_Step5_grid extends WizardPage {

    private static final String title = "LJ fitting procedure : preparing fitting grid";

    @FXML
    private TextField textfield_ngrid;
    @FXML
    private Button goButton, genGrid;

    @FXML
    private TableView<gridValuesModel> tableview_gridValues;
    @FXML
    private TableView<fullGridModel> tableview_fullgrid;

    @FXML
    private TableColumn<gridValuesModel, String> column_gridValues;
    private ObservableList<gridValuesModel> list_gridValues;
    private ObservableList<fullGridModel> list_fullgrid;

    private TableColumn<fullGridModel, String> epsCol;
    @FXML
    private List<TableColumn<fullGridModel, String>> columns_fullgrid;
//    private List<ObservableList<fullGridModel>> list_fullGrid;

//    private Callback<TableColumn<gridValuesModel,String>,TableCell<gridValuesModel,String>> cellFactory;
    /**
     * Represents a group of grid values
     */
    public class gridValuesModel {

        private StringProperty value;

        private gridValuesModel(String val) {
            this.value = new SimpleStringProperty(val);
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value.get();
        }

        /**
         * @param val the value to set
         */
        public void setValue(String val) {
            value.set(val);
        }

    }

    public class fullGridModel {

        private StringProperty epsilon;
//        private List<StringProperty> valList = new ArrayList<>();
//        
//        private fullGridModel(String epsi, String... val)
//        {
//            this.epsilon = new SimpleStringProperty(epsi);
//            for(String st : val)
//                valList.add(new SimpleStringProperty(st));
//        }
//        
//        private fullGridModel(String epsi, List<String> val)
//        {
//            this.epsilon = new SimpleStringProperty(epsi);
//            for(String st : val)
//                valList.add(new SimpleStringProperty(st));
//        }
        private StringProperty data;

        public fullGridModel(String epsi, String dat) {
            this.epsilon = new SimpleStringProperty(epsi);
            this.data = new SimpleStringProperty(dat);
        }

        public void setEps(String eps){
            this.epsilon.setValue(eps);
        }
        
        public void setDat(String dat){
            this.data.setValue(dat);
        }
        
        public String getEps(){
            return this.epsilon.get();
        }
        
        public String getDat(){
            return this.data.get();
        }
        
    }

    public CHARMM_GUI_Step5_grid() {
        super(title);
//        this.tableview_gridValues = new TableView<gridValuesModel>();

        this.list_gridValues = FXCollections.observableArrayList();
        this.columns_fullgrid = new ArrayList<>();

        setupGridTable();
    }

    private void setupGridTable() {
//        list_gridValues.add(new gridValuesModel("0.95"));
//        list_gridValues.add(new gridValuesModel("1.00"));
//        list_gridValues.add(new gridValuesModel("1.05"));

//        column_gridValues.setCellValueFactory(
//                new PropertyValueFactory<gridValuesModel,String>("value")
//        );
//        
//        column_gridValues.setCellFactory(new Callback<TableColumn<gridValuesModel, String>, TableCell<gridValuesModel, String>>() {
//            @Override
//            public TableCell<gridValuesModel, String> call(TableColumn<gridValuesModel, String> atomChargeGroupViewModelStringTableColumn) {
//                return new EditingCell<>();
//            }
//        });
//        
//        column_gridValues.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<gridValuesModel, String>>() {
//            @Override
//            public void handle(TableColumn.CellEditEvent<gridValuesModel, String> event) {
//                logger.debug("onEditCommit event occured");
//                event.getRowValue().setValue(event.getNewValue());
//            }
//        });
//        
//        cellFactory = new Callback<TableColumn<gridValuesModel,String>, TableCell<gridValuesModel, String>>() {
//                    public TableCell call(TableColumn p) {
//                        return new EditingCell();
//                    }
//                };
        column_gridValues.setCellValueFactory(
                new PropertyValueFactory<gridValuesModel, String>("value")
        );

//        column_gridValues.setCellFactory(cellFactory);
        column_gridValues.setCellFactory(TextFieldTableCell.forTableColumn());

        column_gridValues.setOnEditCommit(
                new EventHandler<CellEditEvent<gridValuesModel, String>>() {
                    @Override
                    public void handle(CellEditEvent<gridValuesModel, String> t) {
//                    logger.info("hello from handle");
                        ((gridValuesModel) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setValue(t.getNewValue());
                    }
                }
        );

        tableview_gridValues.setItems(list_gridValues);
    }

    private void setupFullGrid() {

        int ngrid = Integer.valueOf(textfield_ngrid.getText());

        tableview_fullgrid.getColumns().clear();

//        list_fullGrid = new ArrayList<>();
//        for( ObservableList<fullGridModel> obl : list_fullGrid)
//        {
//            
//        }
//        columns_fullgrid.add(new TableColumn("epsilons"));

        epsCol = new TableColumn("Epsilons");
        epsCol.setSortable(false);
        epsCol.setCellValueFactory(
                    new PropertyValueFactory<fullGridModel, String>("epsilon")
            );
//        epsCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        epsCol.setOnEditCommit(
//                    new EventHandler<CellEditEvent<fullGridModel, String>>() {
//                        @Override
//                        public void handle(CellEditEvent<fullGridModel, String> t) {
//                            ((fullGridModel) t.getTableView().getItems().get(
//                                    t.getTablePosition().getRow())).setEps(t.getNewValue());
//                        }
//                    }
//            );
        
        for (int i = 0; i < ngrid; i++) {
            String scale = list_gridValues.get(i).getValue();
            columns_fullgrid.add(new TableColumn("sigma*" + scale));
        }

        for (TableColumn col : columns_fullgrid) {
            col.setSortable(false);
            col.setPrefWidth(tableview_fullgrid.getWidth() / ((double) ngrid + 1));
//            col.setCellValueFactory(
//                    new PropertyValueFactory<fullGridModel, String>("data")
//            );
//            col.setCellFactory(TextFieldTableCell.forTableColumn());
//            col.setOnEditCommit(
//                    new EventHandler<CellEditEvent<fullGridModel, String>>() {
//                        @Override
//                        public void handle(CellEditEvent<fullGridModel, String> t) {
//                            ((fullGridModel) t.getTableView().getItems().get(
//                                    t.getTablePosition().getRow())).setDat(t.getNewValue());
//                        }
//                    }
//            );
        }

        tableview_fullgrid.getColumns().add(epsCol);
//        tableview_fullgrid.getColumns().addAll(columns_fullgrid);
        
        list_fullgrid = FXCollections.observableArrayList();
        for (int i = 0; i < ngrid; i++) {
            String scale = list_gridValues.get(i).getValue();
            list_fullgrid.add(new fullGridModel("epsilon*"+scale, "0.0"));
        }
        
        
        tableview_fullgrid.setItems(list_fullgrid);
    }

    @FXML
    private void GoButtonPressed(ActionEvent event) {
        if (event.getSource().equals(goButton)) {
            list_gridValues.clear();
            int ngrid = Integer.valueOf(textfield_ngrid.getText());

            for (int i = 0; i < ngrid; i++) {
                list_gridValues.add(new gridValuesModel("0.00"));
            }
        }
    }

    @FXML
    private void GenButtonPressed(ActionEvent event) {
        if (event.getSource().equals(genGrid)) {
            setupFullGrid();
        }
    }

    @Override
    protected void fillButtonBar() {
    }

}

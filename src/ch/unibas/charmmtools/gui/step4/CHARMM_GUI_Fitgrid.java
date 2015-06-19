/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step4;

import ch.unibas.charmmtools.gui.CHARMM_GUI_base;
import ch.unibas.charmmtools.gui.step1.CHARMM_GUI_InputAssistant;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.application.scripts.base.ScriptExecutionException;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import ch.unibas.fittingwizard.presentation.base.WizardPage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author hedin
 */
public class CHARMM_GUI_Fitgrid extends CHARMM_GUI_base {

    private static final String title = "LJ fitting procedure : preparing fitting grid";

    @FXML
    private TextField textfield_ngrid;
    @FXML
    private Button goButton, genGrid;

    @FXML
    private TableView<gridValuesModel> tableview_gridValues;

    @FXML
    private TableColumn<gridValuesModel, String> column_gridValues;
    private ObservableList<gridValuesModel> list_gridValues;

    @FXML
    private GridPane gpane_fullgrid;

    @FXML // fx:id="textPar"
    private TextField textPar; // Value injected by FXMLLoader

    @FXML // fx:id="buttonPar"
    private Button buttonPar; // Value injected by FXMLLoader

    private File parFile;
    private boolean PAR_selected = false;

    private Button button_reset;
    private Button button_save_files;
    private Button button_goRunSim;
    private Button button_run_all;

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

    public CHARMM_GUI_Fitgrid(RunCHARMMWorkflow flow) {
        super(title,flow);
//        this.tableview_gridValues = new TableView<gridValuesModel>();

        this.list_gridValues = FXCollections.observableArrayList();

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

        int ngrid = Integer.valueOf(textfield_ngrid.getText()) + 1;
        gpane_fullgrid.setGridLinesVisible(true);
        gpane_fullgrid.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        gpane_fullgrid.getColumnConstraints().clear();

        // grid constraints
        for (int i = 0; i < ngrid; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / ngrid);
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / ngrid);
            gpane_fullgrid.getColumnConstraints().add(column);
            gpane_fullgrid.getRowConstraints().add(row);
        }

        // set col 0 and row 0 containing labels
        for (int i = 1; i < ngrid; i++) {
            Label sig = new Label("σ*" + list_gridValues.get(i - 1).getValue());
            sig.setAlignment(Pos.CENTER);
            Label eps = new Label("ε*" + list_gridValues.get(i - 1).getValue());
            eps.setAlignment(Pos.CENTER);
            gpane_fullgrid.add(sig, i, 0);
            gpane_fullgrid.add(eps, 0, i);
        }

        // set content of cells
        // epsilons on rows and sigmas on columns
        for (int i = 1; i < ngrid; i++) {
            for (int j = 1; j < ngrid; j++) {
//                if (i != j) {
//                    continue;
//                }
                GridPane loc = new GridPane();
                loc.add(new Label("ρ"), 0, 0);
                loc.add(new Label("..."), 1, 0);
                loc.add(new Label("ΔH"), 0, 1);
                loc.add(new Label("..."), 1, 1);
                loc.add(new Label("ΔG"), 0, 2);
                loc.add(new Label("..."), 1, 2);
                gpane_fullgrid.add(loc, i, j);
            }
        }

        genGrid.setDisable(true);
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

        genGrid.setDisable(false);
    }

    @FXML
    private void GenButtonPressed(ActionEvent event) {
        if (event.getSource().equals(genGrid)) {
            setupFullGrid();
            button_save_files.setDisable(false);
            genGrid.setDisable(true);
        }
    }

    private void ResetFields() {
        genGrid.setDisable(false);
        list_gridValues.clear();
        textfield_ngrid.clear();
        gpane_fullgrid.getChildren().clear();
        button_save_files.setDisable(true);
        button_run_all.setDisable(true);
    }

    private void SaveFiles() {

        File myDir = new File(this.work_directory,"scaled_par");
        myDir.mkdirs();

//        String[] exts = {"par"};
//        List<File> flist = new ArrayList<>();
//        flist.addAll(FileUtils.listFiles(new File("./test"), exts, false));
//
//        logger.info("Found par files : ");
//        for (File f : flist) {
//            logger.info(f.getAbsolutePath());
//        }
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(myDir);

        String script = new File("./scripts/lj-fit/src/reparametrize-eps-sig-par").getAbsolutePath();

        int ngrid = Integer.valueOf(textfield_ngrid.getText()) + 1;

        //epsilon on rows and sigma on columns
        for (int i = 1; i < ngrid; i++) {
            String e_scale = list_gridValues.get(i - 1).getValue();
            for (int j = 1; j < ngrid; j++) {
//                if (i != j) {
//                    continue;
//                }
                String s_scale = list_gridValues.get(j - 1).getValue();
                pb.command("/bin/bash", script,
                        parFile.getAbsolutePath(), e_scale, s_scale);
                pb.redirectOutput(new File(myDir, "scaled_e" + e_scale + "_s" + s_scale + ".par"));
                logger.info("Running bash script\n" + pb.command()
                        + "\nin directory:\n" + pb.directory()
                        + "\nwith environment:\n" + pb.environment());
                int exitCode = 0;
                try {
                    Process p = pb.start();
                    exitCode = p.waitFor();
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Bash script [%s] failed.", script), e);
                }
                logger.info("Bash return value: " + exitCode);
                if (exitCode != 0) {
                    throw new ScriptExecutionException(
                            String.format("Bash script [%s] did not exit correctly. Exit code: %s",
                                    script,
                                    String.valueOf(exitCode)));
                }
            }
        }

        button_run_all.setDisable(false);

    }

    private void RunAll() {

    }

    @Override
    protected void fillButtonBar() {

        button_goRunSim = ButtonFactory.createButtonBarButton("Go to Input Assistant", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Goin Back to input assistant.");
                navigateTo(CHARMM_GUI_InputAssistant.class, null);
            }
        });
        addButtonToButtonBar(button_goRunSim);

        button_reset = ButtonFactory.createButtonBarButton("Reset", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Resetting all fields.");
                ResetFields();
            }
        });
        addButtonToButtonBar(button_reset);

        button_save_files = ButtonFactory.createButtonBarButton("Save FF parameter files", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Saving modified FF params files");
                SaveFiles();
            }
        });
        addButtonToButtonBar(button_save_files);
        button_save_files.setDisable(true);

        button_run_all = ButtonFactory.createButtonBarButton("Run all simulations", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Running all simulations");
                RunAll();
            }
        });
        addButtonToButtonBar(button_run_all);
        button_run_all.setDisable(true);

    }

    @FXML
    void chooseParFile(ActionEvent event) {
        Window myParent = buttonPar.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("."));
        File selectedFile = null;

        chooser.setTitle("Open File");

        if (event.getSource().equals(buttonPar)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF parameters file (*.par,*.prm)",
                    "*.par", "*.prm"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textPar.setText(selectedFile.getAbsolutePath());
                parFile = new File(selectedFile.getAbsolutePath());
                PAR_selected = true;
            }
        }

    }
    
    
}

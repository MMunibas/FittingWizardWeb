/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step4.ParGrid;

import ch.unibas.fittingwizard.presentation.base.dialog.ModalDialog;
import ch.unibas.fittingwizard.presentation.base.ui.MainWindow;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Window;

public class Choose_Ignored_atoms_Scaling extends ModalDialog {

    @FXML
    private TableView<SelectScalingModel> Table;
    @FXML
    private TableColumn<SelectScalingModel, String> FFatomTypeColumn, MTPatomTypeColumn;
    @FXML
    private TableColumn<SelectScalingModel, Boolean> scalingColumn;
    
    @FXML
    private Button cancelButton, okButton;

    Window primary;

    public Choose_Ignored_atoms_Scaling() {
        super("Choosing atoms types for LJ scaling ...");
        primary = MainWindow.getPrimaryStage().getScene().getWindow();
        primary.getScene().getRoot().setEffect(new BoxBlur());
        setupTable();
    }

    private void setupTable() {
        FFatomTypeColumn.setCellValueFactory(
                new PropertyValueFactory<>("FFatomType")
        );
        
        MTPatomTypeColumn.setCellValueFactory(
                new PropertyValueFactory<>("MTPatomType")
        );
        
        scalingColumn.setCellValueFactory((CellDataFeatures<SelectScalingModel, Boolean> param) -> param.getValue().isSelectedProperty());
        
        scalingColumn.setCellFactory( CheckBoxTableCell.forTableColumn(scalingColumn) );

 }

    public List<SelectScalingModel> editAtomTypes(List<SelectScalingModel> atomTypeIds) {

        fillTable(atomTypeIds);
        showAndWait();
        
        logger.info("Received close signal ; retuning list of atoms to scale");

        return Table.getItems();
        
    }

    private void fillTable(List<SelectScalingModel> atomTypeIds) {
        Table.getItems().clear();
        Table.getItems().addAll(atomTypeIds);
    }

    public void handleDone(ActionEvent event) {
        logger.info("Selection of types to scale done");
        primary.getScene().getRoot().setEffect(null);
        close();
    }
    
    public void handleCancel(ActionEvent event) {
        logger.info("Selection of types to scale cancelled by user");
        primary.getScene().getRoot().setEffect(null);
        close();
    }

    
}

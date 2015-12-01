/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step4;

import ch.unibas.fittingwizard.application.fitting.ChargeValue;
import ch.unibas.fittingwizard.application.molecule.AtomTypeId;
import ch.unibas.fittingwizard.presentation.base.dialog.ModalDialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;

public class Choose_Ignored_atoms_Scaling extends ModalDialog {

    @FXML
    private TableView<SelectScalingModel> Table;
    @FXML
    private TableColumn<SelectScalingModel, String> FFatomTypeColumn, MTPatomTypeColumn;
    @FXML
    private TableColumn<SelectScalingModel, Boolean> scalingColumn;

//    @FXML
//    private TableColumn<AtomTypeViewModel, String> moleculesColumn;
    
    @FXML
    private Button cancelButton, okButton;

//    private LinkedHashSet<ChargeValue> chargeValues;
//    private ArrayList<AtomTypeViewModel> atomTypeViewModels;

    public Choose_Ignored_atoms_Scaling() {
        super("Choosing atoms types for LJ scaling ...");
        setupTable();
//        okButton.setDisable(true);
    }

    private void setupTable() {
        FFatomTypeColumn.setCellValueFactory(
                new PropertyValueFactory<SelectScalingModel, String>("FFatomType")
        );
        
        MTPatomTypeColumn.setCellValueFactory(
                new PropertyValueFactory<SelectScalingModel, String>("MTPatomType")
        );
        
        scalingColumn.setCellValueFactory(
                new Callback<CellDataFeatures<SelectScalingModel,Boolean>,ObservableValue<Boolean>>()
                {
                    @Override
                    public ObservableValue<Boolean> call(CellDataFeatures<SelectScalingModel, Boolean> param)
                    {   
                        return param.getValue().isSelectedProperty();
                    } 
                }
        );
        
        scalingColumn.setCellFactory( CheckBoxTableCell.forTableColumn(scalingColumn) );

 }

    public List<SelectScalingModel> editAtomTypes(List<SelectScalingModel> atomTypeIds) {
//        chargeValues = new LinkedHashSet<>();

//        logger.info("Before opening window : ");
//        for(SelectScalingModel model : atomTypeIds)
//            logger.info(model.getFFAtomType() + " " + model.isSelected());
        
        fillTable(atomTypeIds);
//        updateSaveButtonDisabled();
        showAndWait();
        
        logger.info("Received close signal ; retuning list of atoms to scale");

        return Table.getItems();
        
//        return chargeValues;
    }

    private void fillTable(List<SelectScalingModel> atomTypeIds) {
        Table.getItems().clear();

//        atomTypeViewModels = new ArrayList<>();
//        for (AtomTypeId atomTypeId : atomTypeIds) {
//            atomTypeViewModels.add(new AtomTypeViewModel(atomTypeId));
//        }

        Table.getItems().addAll(atomTypeIds);
    }

//    private void updateSaveButtonDisabled() {
//        boolean allResolved = checkIfDuplicatesResolved();
//        okButton.setDisable(!allResolved);
//    }
//
//    private boolean checkIfDuplicatesResolved() {
//        boolean resolved = true;
//        for (AtomTypeViewModel duplicatesVm : atomTypeViewModels) {
//            if (!duplicatesVm.isChargeResolved()) {
//                resolved = false;
//                break;
//            }
//        }
//        return resolved;
//    }
    public void handleDone(ActionEvent event) {
        logger.info("Selection of types to scale done");
//        if (checkIfDuplicatesResolved()) {
//            createChargeValuesForEditedAtomTypes();
        close();
//        }
        
//        logger.info("When closing window (done) : ");
//        for(SelectScalingModel model : Table.getItems())
//            logger.info(model.getFFAtomType() + " " + model.isSelected());
        
    }

//    private void createChargeValuesForEditedAtomTypes() {
//        logger.info("createChargeValuesForEditedAtomTypes");
//
//        for (AtomTypeViewModel duplicateVm : atomTypeViewModels) {
//            chargeValues.add(new ChargeValue(duplicateVm.getAtomTypeId(), ChargeTypes.charge, duplicateVm.getResolvedCharge()));
//        }
//    }
    
    public void handleCancel(ActionEvent event) {
        logger.info("Selection of types to scale cancelled by user");
//        chargeValues = null;
        close();
        
//        logger.info("When closing window (cancel) : ");
//        for(SelectScalingModel model : Table.getItems())
//            logger.info(model.getFFAtomType() + " " + model.isSelected());
    }

    
}

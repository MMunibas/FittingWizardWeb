package ch.unibas.fittingwizard.presentation.fitting;

import ch.unibas.fittingwizard.application.fitting.ChargeValue;
import ch.unibas.fittingwizard.application.molecule.AtomTypeId;
import ch.unibas.fittingwizard.application.tools.charges.ChargeTypes;
import ch.unibas.fittingwizard.presentation.base.dialog.ModalDialog;
import ch.unibas.fittingwizard.presentation.base.ui.EditingCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

/**
 * User: mhelmer
 * Date: 10.12.13
 * Time: 19:03
 */
public class EditAtomTypeChargesDialog extends ModalDialog {

    @FXML
    private TableView<AtomTypeViewModel> duplicatesTable;
    @FXML
    private TableColumn<AtomTypeViewModel, String> atomTypeColumn;
    @FXML
    private TableColumn<AtomTypeViewModel, String> chargeColumn;
    @FXML
    private TableColumn<AtomTypeViewModel, String> moleculesColumn;

    @FXML
    private Button startButton;

    private LinkedHashSet<ChargeValue> chargeValues;
    private ArrayList<AtomTypeViewModel> atomTypeViewModels;

    public EditAtomTypeChargesDialog() {
        super("Missing or duplicate atom type charges");
        setupDuplicatesTable();
        startButton.setDisable(true);
    }

    private void setupDuplicatesTable() {
        atomTypeColumn.setCellValueFactory(new PropertyValueFactory<AtomTypeViewModel, String>("atomTypeName"));
        chargeColumn.setCellValueFactory(new PropertyValueFactory<AtomTypeViewModel, String>("charge"));
        chargeColumn.setCellFactory(new Callback<TableColumn<AtomTypeViewModel, String>, TableCell<AtomTypeViewModel, String>>() {
            @Override
            public TableCell<AtomTypeViewModel, String> call(TableColumn<AtomTypeViewModel, String> duplicateViewModelStringTableColumn) {
                return new EditingCell<AtomTypeViewModel>();
            }
        });
        chargeColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<AtomTypeViewModel, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<AtomTypeViewModel, String> event) {
                logger.debug("onEditCommit event occured");
                event.getRowValue().setCharge(event.getNewValue());
                updateSaveButtonDisabled();
            }
        });

        moleculesColumn.setVisible(false);
        //moleculesColumn.setCellValueFactory(new PropertyValueFactory<AtomTypeViewModel, String>("molecules"));
    }

    public LinkedHashSet<ChargeValue> editAtomTypes(LinkedHashSet<AtomTypeId> atomTypeIds) {
        chargeValues = new LinkedHashSet<>();

        fillTable(atomTypeIds);
        updateSaveButtonDisabled();
        showAndWait();

        return chargeValues;
    }

    private void fillTable(Set<AtomTypeId> atomTypeIds) {
        duplicatesTable.getItems().clear();

        atomTypeViewModels = new ArrayList<>();
        for (AtomTypeId atomTypeId : atomTypeIds) {
            atomTypeViewModels.add(new AtomTypeViewModel(atomTypeId));
        }

        duplicatesTable.getItems().addAll(atomTypeViewModels);
    }

    private void updateSaveButtonDisabled() {
        boolean allResolved = checkIfDuplicatesResolved();
        startButton.setDisable(!allResolved);
    }

    private boolean checkIfDuplicatesResolved() {
        boolean resolved = true;
        for (AtomTypeViewModel duplicatesVm : atomTypeViewModels) {
            if (!duplicatesVm.isChargeResolved()) {
                resolved = false;
                break;
            }
        }
        return resolved;
    }

    public void handleStartFitting(ActionEvent event) {
        logger.info("handleStartFitting");

        if (checkIfDuplicatesResolved()) {
            createChargeValuesForEditedAtomTypes();
            close();
        }
    }

    private void createChargeValuesForEditedAtomTypes() {
        logger.info("createChargeValuesForEditedAtomTypes");

        for (AtomTypeViewModel duplicateVm : atomTypeViewModels) {
            chargeValues.add(new ChargeValue(duplicateVm.getAtomTypeId(), ChargeTypes.charge, duplicateVm.getResolvedCharge()));
        }
    }

    public void handleCancel(ActionEvent event) {
        logger.info("handleCancel");
        chargeValues = null;
        close();
    }

    public class AtomTypeViewModel {

        private final AtomTypeId atomTypeId;
        private StringProperty charge = new SimpleStringProperty("");
        private Double resolvedCharge = null;

        public AtomTypeViewModel(AtomTypeId atomTypeId) {
            this.atomTypeId = atomTypeId;
        }

        public String getAtomTypeName() {
            return atomTypeId.getName();
        }

        public String getCharge() {
            return charge.getValue();
        }

        public void setCharge(String charge) {
            Double parsedCharge = null;
            if (charge != null && !charge.isEmpty()) {
                parsedCharge = Double.parseDouble(charge);
                charge = String.valueOf(parsedCharge);
            }
            this.charge.setValue(charge);
            resolvedCharge = parsedCharge;
        }

        public boolean isChargeResolved() {
            return resolvedCharge != null;
        }

        public StringProperty chargeProperty() {
            return charge;
        }
//
//        public String getMolecules() {
//            String molecules = "";
//
//            Iterator<MoleculeId> iterator = duplicate.getMolecules().iterator();
//            while (iterator.hasNext()) {
//                MoleculeId next =  iterator.next();
//                molecules += next.getDescription();
//                if (iterator.hasNext()) {
//                    molecules += ", ";
//                }
//            }
//
//            return molecules;
//        }

        public Double getResolvedCharge() {
            return resolvedCharge;
        }

        public AtomTypeId getAtomTypeId() {
            return atomTypeId;
        }
    }
}

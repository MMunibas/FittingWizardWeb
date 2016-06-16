/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.addmolecule;

import ch.unibas.fitting.shared.molecules.Atom;
import ch.unibas.fitting.shared.molecules.AtomType;
import ch.unibas.fitting.shared.molecules.Molecule;
import ch.unibas.fitting.shared.molecules.MoleculeRepository;
import ch.unibas.fittingwizard.gaussian.Visualization;
import ch.unibas.fitting.shared.tools.LPunParser;
import ch.unibas.fitting.shared.xyz.XyzAtom;
import ch.unibas.fittingwizard.gaussian.MoleculeListPage;
import ch.unibas.fittingwizard.gaussian.base.ButtonFactory;
import ch.unibas.fittingwizard.gaussian.base.WizardPageWithVisualization;
import ch.unibas.fittingwizard.gaussian.base.dialog.OverlayDialog;
import ch.unibas.fittingwizard.gaussian.base.ui.EditingCell;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 29.11.13
 * Time: 14:48
 */
public class AtomTypeChargePage extends WizardPageWithVisualization {
    private final LPunParser lPunParser;
    private final MoleculeRepository moleculeRepository;
    private final AtomChargesDto dto;

    @FXML
    private TableView<AtomTypeViewModel> chargesTable;
    @FXML
    private TableColumn<AtomTypeViewModel, String> atomTypeColumn;
    @FXML
    private TableColumn<AtomTypeViewModel, String> chargeColumn;

    private Button saveButton;
    private ArrayList<AtomType> charges;
    private Button prevButton;

    public AtomTypeChargePage(MoleculeRepository moleculeRepository,
                              LPunParser lPunParser,
                              Visualization visualization,
                              AtomChargesDto dto) {
        super(visualization, "Atom types and charges");
        this.moleculeRepository = moleculeRepository;
        this.lPunParser = lPunParser;
        this.dto = dto;
        setupChargesTable();
    }

    private void setupChargesTable() {
        chargesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<AtomTypeViewModel>() {
            @Override
            public void changed(ObservableValue<? extends AtomTypeViewModel> observableValue, AtomTypeViewModel chargeOld, AtomTypeViewModel chargeNew) {
                selectChargeInVisualization(chargeNew);
            }
        });
        atomTypeColumn.setCellValueFactory(new PropertyValueFactory<AtomTypeViewModel, String>("name"));

        chargeColumn.setCellValueFactory(new PropertyValueFactory<AtomTypeViewModel, String>("userCharge"));
        chargeColumn.setCellFactory(new Callback<TableColumn<AtomTypeViewModel, String>, TableCell<AtomTypeViewModel, String>>() {
            @Override
            public TableCell<AtomTypeViewModel, String> call(TableColumn<AtomTypeViewModel, String> atomChargeGroupViewModelStringTableColumn) {
                return new EditingCell<>();
            }
        });
        chargeColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<AtomTypeViewModel, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<AtomTypeViewModel, String> event) {
                logger.debug("onEditCommit event occured");
                event.getRowValue().setUserCharge(event.getNewValue());
                updateSaveButtonDisabled();
            }
        });
    }

    private void updateSaveButtonDisabled() {
        Molecule.UserChargesState state = Molecule.checkChargesState(charges);

        if (state == Molecule.UserChargesState.NoChargesDefined || state == Molecule.UserChargesState.AllChargesDefined)
            saveButton.setDisable(false);
        else {
            saveButton.setDisable(true);
            saveButton.setTooltip(new Tooltip("Either all or no user charges must be defined."));
        }
    }

    @Override
    protected void fillButtonBar() {
        createCancelButton();
        createPreviousButton();
        createSaveButton();
    }

    private void createPreviousButton() {
        prevButton = ButtonFactory.createButtonBarButton("Go back to parameters", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Going back to parameters.");

                MultipoleGaussParameterDto inputDto = new MultipoleGaussParameterDto(dto.getParsedXyzFile());
                navigateTo(MultipoleGaussParameterPage.class, inputDto);
            }
        });
        addButtonToButtonBar(prevButton);
    }

    private void createCancelButton() {
        Button cancelButton = ButtonFactory.createButtonBarButton("Cancel", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Cancel and go back to molecule list.");
                navigateTo(MoleculeListPage.class);
            }
        });
        addButtonToButtonBar(cancelButton);
    }

    private void createSaveButton() {
        saveButton = ButtonFactory.createButtonBarButton("Forward", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Saving molecule and go back to molecule list.");

                Molecule molecule = dto.getMolecule();
                if (molecule == null)
                    molecule = createMolecule();

                boolean saveIsOkay = true;
                Molecule.UserChargesState state = molecule.getUserChargesState();
                if (state == Molecule.UserChargesState.NoChargesDefined) {
                    saveIsOkay = OverlayDialog.askYesOrNo("You did not enter any charges. Is it okay to proceed?");
                } else if (state == Molecule.UserChargesState.Invalid) {
                    OverlayDialog.showError("Invalid user charge state.", "The given combination of user charges is invalid.");
                    saveIsOkay = false;
                }

                if (saveIsOkay) {
                    moleculeRepository.save(molecule);
                    navigateTo(MoleculeListPage.class);
                }
            }
        });
        saveButton.setDisable(false);
        addButtonToButtonBar(saveButton);
    }

    @Override
    public void initializeData() {
        if (dto.getMolecule() == null)
            charges = lPunParser.parse(dto.getParsedXyzFile().getMoleculeName());
        else {
            charges = dto.getMolecule().getAtomTypes();
            prevButton.setVisible(false);
        }

        List<AtomTypeViewModel> atomTypeViewModels = new ArrayList<>();
        for (AtomType atomType : charges) {
            atomTypeViewModels.add(new AtomTypeViewModel(atomType));
        }
        chargesTable.setItems(FXCollections.observableArrayList(atomTypeViewModels));
        openVisualization();
    }

    public void handleShowVisualization(ActionEvent event) {
        logger.info("handleShowVisualization pressed.");
        openVisualization();
    }

    private void openVisualization() {
        visualization.show(dto.getParsedXyzFile().getSource());
    }

    private void selectChargeInVisualization(AtomTypeViewModel selectedAtomType) {
        logger.info("Selection atom for type " + selectedAtomType.getName());

        if (selectedAtomType != null) {
            visualization.selectAtomTypes(selectedAtomType.getAtomType());
        }
    }

    private Molecule createMolecule() {
        ArrayList<Atom> atoms = new ArrayList<>();
        for (XyzAtom xyzAtom : dto.getParsedXyzFile().getAtoms()) {
            atoms.add(new Atom(xyzAtom.getName(), xyzAtom.getX(), xyzAtom.getY(), xyzAtom.getZ()));
        }

        return new Molecule(dto.getParsedXyzFile(), atoms, charges);
    }

    /**
     * Represents a group of all atom types within the same molecule.
     */
    public static class AtomTypeViewModel {

        private static final Logger logger = Logger.getLogger(AtomTypeViewModel.class);

        private final AtomType atomType;

        private final StringProperty userCharge = new SimpleStringProperty();

        public AtomTypeViewModel(AtomType atomType) {
            this.atomType = atomType;
            String value = atomType.getUserQ00() == null ? "" : String.valueOf(atomType.getUserQ00());
            userCharge.setValue(value);
        }

        public String getName() {
            return atomType.getId().getName();
        }

        public AtomType getAtomType() {
            return atomType;
        }

        public String getUserCharge() {
            return userCharge.getValue();
        }

        public StringProperty userChargeProperty() {
            return userCharge;
        }

        public void setUserCharge(String newCharge) {
            logger.debug(String.format("Setting user charge for type %s to %s.", getName(), newCharge));

            Double parsedCharge = null;
            boolean isEmpty = newCharge == null || newCharge.isEmpty();
            if (!isEmpty) {
                parsedCharge = Double.parseDouble(newCharge);
                newCharge = String.valueOf(parsedCharge);
            }
            atomType.setUserQ0(parsedCharge);
            userCharge.setValue(newCharge);
        }
    }
}

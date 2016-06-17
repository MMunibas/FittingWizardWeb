/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.addmolecule;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.MoleculesDir;
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
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 * User: mhelmer
 * Date: 29.11.13
 * Time: 14:48
 */
public class AtomTypeChargePage extends WizardPageWithVisualization {
    private MoleculesDir moleculesDir;
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
                              MoleculesDir moleculesDir,
                              LPunParser lPunParser,
                              Visualization visualization,
                              AtomChargesDto dto) {
        super(visualization, "Atom types and charges");
        this.moleculeRepository = moleculeRepository;
        this.moleculesDir = moleculesDir;
        this.lPunParser = lPunParser;
        this.dto = dto;
        setupChargesTable();
    }

    private void setupChargesTable() {
        chargesTable.getSelectionModel().selectedItemProperty().addListener((observableValue, chargeOld, chargeNew) -> {
            selectChargeInVisualization(chargeNew);
        });
        atomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        chargeColumn.setCellValueFactory(new PropertyValueFactory<>("userCharge"));
        chargeColumn.setCellFactory(atomChargeGroupViewModelStringTableColumn -> new EditingCell<>());
        chargeColumn.setOnEditCommit(event -> {
            logger.debug("onEditCommit event occured");
            event.getRowValue().setUserCharge(event.getNewValue());
            updateSaveButtonDisabled();
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
        prevButton = ButtonFactory.createButtonBarButton("Go back to parameters", actionEvent -> {
            logger.info("Going back to parameters.");

            MultipoleGaussParameterDto inputDto = new MultipoleGaussParameterDto(dto.getParsedXyzFile());
            navigateTo(MultipoleGaussParameterPage.class, inputDto);
        });
        addButtonToButtonBar(prevButton);
    }

    private void createCancelButton() {
        Button cancelButton = ButtonFactory.createButtonBarButton("Cancel", actionEvent -> {
            logger.info("Cancel and go back to molecule list.");
            navigateTo(MoleculeListPage.class);
        });
        addButtonToButtonBar(cancelButton);
    }

    private void createSaveButton() {
        saveButton = ButtonFactory.createButtonBarButton("Forward", actionEvent -> {
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
        });
        saveButton.setDisable(false);
        addButtonToButtonBar(saveButton);
    }

    @Override
    public void initializeData() {
        if (dto.getMolecule() == null)
            charges = lPunParser.parse(moleculesDir,
                    dto.getParsedXyzFile().getMoleculeName());
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
        ArrayList<Atom> atoms = dto.getParsedXyzFile()
                .getAtoms()
                .stream()
                .map(xyzAtom -> new Atom(xyzAtom.getName(), xyzAtom.getX(), xyzAtom.getY(), xyzAtom.getZ()))
                .collect(Collectors.toCollection(ArrayList::new));

        return new Molecule(dto.getParsedXyzFile(), atoms, charges);
    }

}

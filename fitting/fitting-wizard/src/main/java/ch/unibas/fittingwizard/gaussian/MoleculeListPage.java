/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian;

import ch.unibas.fitting.shared.base.MoleculesDir;
import ch.unibas.fitting.shared.fitting.FitRepository;
import ch.unibas.fitting.shared.molecules.Molecule;
import ch.unibas.fitting.shared.molecules.MoleculeId;
import ch.unibas.fitting.shared.molecules.MoleculeRepository;
import ch.unibas.fitting.shared.xyz.XyzFile;
import ch.unibas.fitting.shared.xyz.XyzFileParser;
import ch.unibas.fittingwizard.gaussian.addmolecule.AtomChargesDto;
import ch.unibas.fittingwizard.gaussian.addmolecule.AtomTypeChargePage;
import ch.unibas.fittingwizard.gaussian.addmolecule.SelectCoordinateFilePage;
import ch.unibas.fittingwizard.gaussian.base.ButtonFactory;
import ch.unibas.fittingwizard.gaussian.base.WizardPageWithVisualization;
import ch.unibas.fittingwizard.gaussian.base.dialog.OverlayDialog;
import ch.unibas.fittingwizard.gaussian.fitting.FitResultPage;
import ch.unibas.fittingwizard.gaussian.fitting.FittingParameterPage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;

public class MoleculeListPage extends WizardPageWithVisualization {

    private final MoleculeRepository moleculeRepository;
    private final FitRepository fitRepository;
    private final MoleculesDir moleculeDir;

    @FXML
    private TableView<Molecule> moleculesTable;
    @FXML
    private TableColumn<Molecule, String> nameColumn;

    @FXML
    private Button addButton;
    @FXML
    private Button displayButton;
    @FXML
    private Button editButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button goToResultsButton;
    @FXML
    private Button loadExistingButton;

    private Molecule selectedMolecule;
    private Button fittingParameterButton;

    public MoleculeListPage(Visualization visualization,
                            MoleculeRepository moleculeRepository,
                            FitRepository fitRepository,
                            MoleculesDir moleculesDir) {
        super(visualization, "Molecule list");
        this.moleculeRepository = moleculeRepository;
        this.fitRepository = fitRepository;
        this.moleculeDir = moleculesDir;
        setupMoleculesTable();
        setupButtons();
    }

    private void setupMoleculesTable() {
        moleculesTable.getItems().addListener(new ListChangeListener<Molecule>() {
            @Override
            public void onChanged(Change<? extends Molecule> change) {
                boolean hasMolecules =  change.getList().size() > 0;
                fittingParameterButton.setDisable(!hasMolecules);
            }
        });
        moleculesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Molecule>() {
            @Override
            public void changed(ObservableValue observableValue, Molecule o, Molecule o2) {
                selectedMolecule = o2;
                displayButton.setDisable(selectedMolecule == null);
                editButton.setDisable(selectedMolecule == null);
                removeButton.setDisable(selectedMolecule == null);
            }
        });
        nameColumn.setCellValueFactory(new PropertyValueFactory<Molecule, String>("description"));
    }

    private void setupButtons() {
        goToResultsButton.setDisable(fitRepository.getFitCount() == 0);
    }

    @Override
    public void initializeData() {
        fillMoleculeTable();
    }

    private void fillMoleculeTable() {
        selectedMolecule = null;
        ArrayList<Molecule> molecules = moleculeRepository.loadAll();
        moleculesTable.getItems().clear();
        moleculesTable.getItems().addAll(molecules);
    }

    protected void fillButtonBar() {
        fittingParameterButton = ButtonFactory.createButtonBarButton("Define fitting parameters", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Defining fitting parameter.");

                navigateTo(FittingParameterPage.class);
            }
        });
        fittingParameterButton.setDisable(true);
        addButtonToButtonBar(fittingParameterButton);
    }

    public void handleAddAction(ActionEvent event) {
        logger.info("Adding new molecule.");
        navigateTo(SelectCoordinateFilePage.class);
    }

    public void handleRemoveAction(ActionEvent event) {
        if (selectedMolecule != null) {
            String message = String.format("Do you really want to remove molecule %s?", selectedMolecule.getDescription());
            if (OverlayDialog.askYesOrNo(message)) {
                logger.info("Remove selected molecule.");
                moleculeRepository.remove(selectedMolecule);
                fillMoleculeTable();
            }
        }
    }

    public void handleEditAction(ActionEvent event) {
        logger.info("Edit selected molecule.");
        if (selectedMolecule != null) {
            navigateTo(AtomTypeChargePage.class, new AtomChargesDto(selectedMolecule));
        }
    }

    public void handleDisplayAction(ActionEvent event) {
        logger.info("Display selected molecule.");
        if (selectedMolecule != null) {
            visualization.show(selectedMolecule.getXyzFile().getSource());
        }
    }

    public void handleGoToResults(ActionEvent event) {
        logger.info("Going to fitting results.");
        navigateTo(FitResultPage.class);
    }

    public void handleLoadExisting(ActionEvent event) {
        logger.info("Loading existing molecule.");

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(moleculeDir.getDirectory());
        chooser.setTitle("Please select a molecule.");
        File selectedDir = chooser.showDialog(this.getScene().getWindow());
        if (selectedDir == null) {
            logger.info("User skipped.");
            return;
        }

        String moleculeName = selectedDir.getName();
        if (moleculeRepository.checkIfExists(new MoleculeId(moleculeName))) {
            logger.info("Molecule already loaded.");
            OverlayDialog.informUser("Molecule already loaded!",
                    "The selected molecule was already loaded. Please select another molecule.");
            return;
        }

        File xyzFile = new File(selectedDir.getParentFile(), moleculeName + ".xyz");
        if (!xyzFile.isFile()) {
            logger.error("XYZ file not found. Skipping.");
            String format = String.format("No xyz file (%s) found for selected molecule %s.", xyzFile.getAbsolutePath(), moleculeName);
            OverlayDialog.showError("Could not load molecule", format);
            return;
        }

        xyzFile = copyFilesToMoleculesDir(selectedDir, xyzFile);

        XyzFile parse = null;
        try {
            parse = XyzFileParser.parse(xyzFile);
        } catch (Exception e) {
            OverlayDialog.showError("Could not load molecule", "Error while parsing the xyz file " + xyzFile.getAbsolutePath());
        }

        navigateTo(AtomTypeChargePage.class, new AtomChargesDto(parse));
    }

    private File copyFilesToMoleculesDir(File selectedDir, File xyzFile) {
        boolean isAlreadyInMoleculeDir = moleculeDir.contains(selectedDir);
        if (!isAlreadyInMoleculeDir) {
            logger.info("Molecule files are not in molecules directory. Copying files to directory.");
            try {
                FileUtils.copyDirectoryToDirectory(selectedDir, moleculeDir.getDirectory());
                FileUtils.copyFileToDirectory(xyzFile, moleculeDir.getDirectory());
            } catch (IOException e) {
                throw new RuntimeException("Could not copy files to molecule directory.");
            }
        }
        return xyzFile;
    }
}

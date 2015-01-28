/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.presentation.fitting;

import ch.unibas.charmmtools.gui.CHARMM_Input_Assistant;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import org.apache.commons.io.FilenameUtils;

import ch.unibas.fittingwizard.Settings;
import ch.unibas.fittingwizard.application.Visualization;
import ch.unibas.fittingwizard.application.fitting.Fit;
import ch.unibas.fittingwizard.application.fitting.FitRepository;
import ch.unibas.fittingwizard.application.fitting.FitResult;
import ch.unibas.fittingwizard.application.molecule.AtomType;
import ch.unibas.fittingwizard.application.molecule.Molecule;
import ch.unibas.fittingwizard.application.molecule.MoleculeRepository;
import ch.unibas.fittingwizard.application.scripts.vmd.VmdDisplayInput;
import ch.unibas.fittingwizard.application.tools.charges.ChargeTypes;
import ch.unibas.fittingwizard.application.workflows.ExportFitInput;
import ch.unibas.fittingwizard.application.workflows.ExportFitWorkflow;
import ch.unibas.fittingwizard.application.workflows.RunVmdDisplayWorkflow;
import ch.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import ch.unibas.fittingwizard.infrastructure.base.FieldcompRunner;
import ch.unibas.fittingwizard.infrastructure.base.VmdRunner;
import ch.unibas.fittingwizard.presentation.MoleculeListPage;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import ch.unibas.fittingwizard.presentation.base.WizardPageWithVisualization;
import ch.unibas.fittingwizard.presentation.base.dialog.OverlayDialog;

/**
 * User: mhelmer
 * Date: 29.11.13
 * Time: 17:40
 */
public class FitResultPage extends WizardPageWithVisualization {

    private final ColorCoder colorCoder = new ColorCoder();
    private final MoleculeRepository moleculeRepository;
    private final FitRepository fitRepository;
    private final ExportFitWorkflow exportFitWorkflow;
    private final RunVmdDisplayWorkflow vmdDisplayWorkflow;

    private Molecule selectedMolecule;

    @FXML
    private Label lblRmse;
    @FXML
    private ComboBox<FitViewModel> cbFitResults;
    @FXML
    private ComboBox<MoleculeViewModel> cbMolecules;
    @FXML
    private TableView<FitResultViewModel> atomsTable;
    @FXML
    private TableColumn<FitResultViewModel, String> atomTypeColumn;

    public FitResultPage(MoleculeRepository moleculeRepository,
                         FitRepository fitRepository,
                         Visualization visualization,
                         ExportFitWorkflow exportFitWorkflow,
                         RunVmdDisplayWorkflow vmdDisplayWorkflow) {
        super(visualization, "Fit result");
        this.moleculeRepository = moleculeRepository;
        this.fitRepository = fitRepository;
        this.exportFitWorkflow = exportFitWorkflow;
        this.vmdDisplayWorkflow = vmdDisplayWorkflow;
        setupTable();
    }

    private void setupTable() {
        atomsTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FitResultViewModel>() {
            @Override
            public void changed(ObservableValue<? extends FitResultViewModel> observableValue, FitResultViewModel fitResultViewModel, FitResultViewModel type) {
                setSelectedAtomType(type);
            }
        });

        atomTypeColumn.setCellValueFactory(new PropertyValueFactory<FitResultViewModel, String>("atomTypeName"));

        for (final String chargeType : ChargeTypes.all) {
            TableColumn<FitResultViewModel, FitResultViewModel.FitValue> column = new TableColumn<>(chargeType);

            column.setPrefWidth(80);
            column.setMinWidth(80);
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FitResultViewModel, FitResultViewModel.FitValue>, ObservableValue<FitResultViewModel.FitValue>>() {
                @Override
                public ObservableValue<FitResultViewModel.FitValue> call(TableColumn.CellDataFeatures<FitResultViewModel, FitResultViewModel.FitValue> data) {
                    return data.getValue().getChargeValueFor(chargeType);
                }
            });
            column.setCellFactory(new ColoredCellCallback());

            atomsTable.getColumns().add(column);
        }
    }

    @Override
    protected void fillButtonBar() {
        Button backButton = ButtonFactory.createButtonBarButton("Go back to molecule list", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Going back to molecule list.");
                navigateTo(MoleculeListPage.class);
            }
        });
        addButtonToButtonBar(backButton);

        Button exportButton = ButtonFactory.createButtonBarButton("Export data", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Exporting data.");
                exportFitData();
            }
        });
        exportButton.setDisable(true);
        addButtonToButtonBar(exportButton);
        
        Settings settings = Settings.loadConfig();
        if (settings.getValue("mocks.enabled").equals("false") && VmdRunner.isAvailable() /*&& FieldcompRunner.isAvailable(settings.getScriptsDir())*/) {
	        Button vmdButton = ButtonFactory.createButtonBarButton("Show in VMD", new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent actionEvent) {
	                logger.info("Show in VMD.");
	                showInVmd();
	            }
	        });
	        addButtonToButtonBar(vmdButton);
        }

        Button anotherFit = ButtonFactory.createButtonBarButton("Do another fit", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Going for another fit.");
                navigateTo(FittingParameterPage.class);
            }
        });
        addButtonToButtonBar(anotherFit);
        
        Button gotoCharmmFit = ButtonFactory.createButtonBarButton("Go to LJ fit section", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Now switching to the CHARMM Lennard-Jones fit section.");
                navigateTo(CHARMM_Input_Assistant.class);
            }
        });
        addButtonToButtonBar(gotoCharmmFit); 
    }// end of fillButtonBar

    private void exportFitData() {
        File destination = selectExportDirectory();
        if (destination != null) {
            Fit fit = cbFitResults.getSelectionModel().getSelectedItem().getFit();
            exportFitWorkflow.execute(WorkflowContext.withInput(new ExportFitInput(fit, destination)));

            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(destination);
            } catch (IOException e) {
                logger.error("Could not open export directory.");
            }
        }
    }

    private File selectExportDirectory() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Fit export destination");
        // wrapping is necessary, since directory chooser can not handle some paths.
        // maybe the problem are not normalized paths...
        File defaultExportDir = new File(FilenameUtils.normalize(exportFitWorkflow.getDefaultExportDir().getAbsolutePath()));
        defaultExportDir.mkdir();
        logger.debug("defaultExportDir=" + FilenameUtils.normalize(defaultExportDir.getAbsolutePath()));
        dirChooser.setInitialDirectory(defaultExportDir);
        File file = dirChooser.showDialog(this.getScene().getWindow());
        return file;
    }
    
    private void showInVmd() {
    	if (selectedMolecule != null) {
            Fit fit = cbFitResults.getSelectionModel().getSelectedItem().getFit();
            VmdDisplayInput input = new VmdDisplayInput(selectedMolecule.getId(), fit.getRank(), fit.getId());
            vmdDisplayWorkflow.execute(WorkflowContext.withInput(input));
        } else {
            OverlayDialog.informUser("Attention", "You have not selected a molecule from the molecules dropdown menu.\n" +
                    "It is not possible to show the visualization for ALL molecules!");
        }
        
    }

    @Override
    public void initializeData() {
        initializeMolecules();
        initializeFits();
        selectLatestFit();
    }

    private void initializeMolecules() {
        cbMolecules.getItems().add(new MoleculeViewModel(null));

        ArrayList<Molecule> molecules = moleculeRepository.loadAll();
        for (Molecule molecule : molecules) {
            cbMolecules.getItems().add(new MoleculeViewModel(molecule));
        }
        cbMolecules.getSelectionModel().select(0);
        cbMolecules.autosize();

        cbMolecules.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MoleculeViewModel>() {
            @Override
            public void changed(ObservableValue<? extends MoleculeViewModel> observableValue, MoleculeViewModel molOld, MoleculeViewModel molNew) {
                logger.info("Molecule selection changed to " + (molNew != null ? molNew : "nothing"));
                Molecule molecule = molNew != null ? molNew.getMolecule() : null;
                selectedMolecule = molecule;
                filterTable(molecule);
                updateVisualization();
            }
        });
    }

    private void updateVisualization() {
        if (selectedMolecule != null) {
            visualization.openFile(selectedMolecule.getXyzFile().getSource());
        } else {
            visualization.close();
        }
    }

    private void initializeFits() {
        ArrayList<Fit> fits = fitRepository.loadAll();
        for (Fit fit : fits) {
            cbFitResults.getItems().add(new FitViewModel(fit));
        }
        cbFitResults.autosize();

        cbFitResults.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FitViewModel>() {
            @Override
            public void changed(ObservableValue<? extends FitViewModel> observableValue, FitViewModel fitOld, FitViewModel fitNew) {
                logger.info("Fit selection changed to " + (fitNew != null ? fitNew : "nothing"));
                Fit fit = fitNew.getFit();
                setRmse(fit);
                generateTableData(fit);
                selectAllMolecules();
            }

            private void setRmse(Fit fit) {
                lblRmse.setText(String.valueOf(fit.getRmse()));
            }

            private void selectAllMolecules() {
                cbMolecules.getSelectionModel().selectFirst();
            }
        });
    }

    private void selectLatestFit() {
        cbFitResults.getSelectionModel().selectLast();
    }

    private ArrayList<FitResultViewModel> resultsFromFit = new ArrayList<>();

    private void generateTableData(Fit fit) {
        logger.info("Generating table data for fit " + fit);

        resultsFromFit.clear();
        for (FitResult fitResult : fit.getFitResults()) {
            resultsFromFit.add(new FitResultViewModel(colorCoder, fitResult, fit));
        }
        filterTable(null);
    }

    private void filterTable(Molecule molecule) {
        if (molecule != null) {
            logger.info("Applying filter to table for molecule " + molecule.getId());
        } else {
            logger.info("Clearing filter.");
        }
        atomsTable.getItems().clear();
        for (FitResultViewModel model : resultsFromFit) {
            if (molecule == null || model.hasMolecule(molecule.getId())) {
                atomsTable.getItems().add(model);
            }
        }
    }

    private void setSelectedAtomType(FitResultViewModel type) {
        logger.info("Selected atom type changed to " + (type != null ? type.getAtomTypeName() : "nothing"));
        if (type != null && selectedMolecule != null) {
            AtomType atomType = selectedMolecule.findAtomTypeById(type.getAtomTypeId());
            visualization.selectAtomTypes(atomType);
        }
    }

    public void handleShowVisualization(ActionEvent event) {
        logger.info("Show visualization.");
        // xyz from current molecule or nothing if ALL
        showVisualization();
        // add change handler to change visualization to selected atom type.
    }

    private void showVisualization() {
        if (selectedMolecule != null) {
            visualization.show(selectedMolecule.getXyzFile().getSource());
        } else {
            OverlayDialog.informUser("Attention", "You have not selected a molecule from the molecules dropdown menu.\n" +
                    "It is not possible to show the visualization for ALL molecules!");
        }
    }

    private class FitListCell extends ListCell<Fit> {
        @Override protected void updateItem(Fit item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText("Fit " + item.getId());
            }
        }
    }

    /**
     * View model
     */
    private class FitViewModel {
        private final Fit fit;

        private FitViewModel(Fit fit) {
            this.fit = fit;
        }

        @Override
        public String toString() {
            return "Fit " + fit.getId();
        }

        private Fit getFit() {
            return fit;
        }
    }

    private class MoleculeViewModel {
        private final Molecule molecule;

        public MoleculeViewModel(Molecule molecule) {
            this.molecule = molecule;
        }

        @Override
        public String toString() {
            return molecule != null ? molecule.getId().getDescription() : "ALL";
        }

        private Molecule getMolecule() {
            return molecule;
        }
    }

    /**
     * This class implements the colored cell factory which sets the color according to the atom type view model.
     */
    private class ColoredCellCallback implements Callback<TableColumn<FitResultViewModel, FitResultViewModel.FitValue>, TableCell<FitResultViewModel, FitResultViewModel.FitValue>> {

        @Override
        public TableCell<FitResultViewModel, FitResultViewModel.FitValue> call(TableColumn<FitResultViewModel, FitResultViewModel.FitValue> atomTypeModelDoubleTableColumn) {
            return new TableCell<FitResultViewModel, FitResultViewModel.FitValue>() {
                @Override
                protected void updateItem(FitResultViewModel.FitValue fitValue, boolean b) {
                    super.updateItem(fitValue, b);
                    if (!isEmpty()) {
                        Color col = fitValue.getColor();
                        if (col != null) {
                            setStyle(getCssBackgroundString(col));
                        } else {
                            setStyle("");
                        }

                        String toolTip = fitValue.getToolTip();
                        if (toolTip != null) {
                            setTooltip(new Tooltip(toolTip));
                        }
                        setText(fitValue.getValue());
                    }
                }
            };
        }

        private String getCssBackgroundString(Color col) {
            String hex = getHexForColor(col);
            String style = "-fx-background-color: " + hex  + ";";
            return style;
        }

        private String getHexForColor(Color col) {
            int r = (int) (col.getRed() * 255);
            int g = (int) (col.getGreen() * 255);
            int b = (int) (col.getBlue() * 255);
            return String.format("#%02x%02x%02x", r, g, b);
        }
    }
}

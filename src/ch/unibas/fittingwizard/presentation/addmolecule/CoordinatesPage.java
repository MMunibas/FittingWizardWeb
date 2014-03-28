package ch.unibas.fittingwizard.presentation.addmolecule;

import ch.unibas.fittingwizard.application.Visualization;
import ch.unibas.fittingwizard.application.base.MoleculesDir;
import ch.unibas.fittingwizard.application.xyz.XyzAtom;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import ch.unibas.fittingwizard.application.xyz.XyzFileParser;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import ch.unibas.fittingwizard.presentation.MoleculeListPage;
import ch.unibas.fittingwizard.presentation.base.WizardPageWithVisualization;
import ch.unibas.fittingwizard.presentation.base.dialog.OverlayDialog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


/**
 * User: mhelmer
 * Date: 26.11.13
 * Time: 16:45
 */
public class CoordinatesPage extends WizardPageWithVisualization {

    private final MoleculesDir moleculesDir;
    private final CoordinatesDto dto;

    private XyzFile xyzFile;

    @FXML
    private Label lblFileInfo;
    @FXML
    private TableView<XyzAtom> coordinatesTable;
    @FXML
    private TableColumn<XyzAtom, String> nameColumn;
    @FXML
    private TableColumn<XyzAtom, String> xColumn;
    @FXML
    private TableColumn<XyzAtom, String> yColumn;
    @FXML
    private TableColumn<XyzAtom, String> zColumn;

    public CoordinatesPage(Visualization visualization, MoleculesDir moleculesDir, CoordinatesDto dto) {
        super(visualization, "Coordinates file content");
        this.moleculesDir = moleculesDir;
        this.dto = dto;
        setupCoordinatesTable();
    }

    private void setupCoordinatesTable() {
        coordinatesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<XyzAtom>() {
            @Override
            public void changed(ObservableValue<? extends XyzAtom> observableValue, XyzAtom atomOld, XyzAtom atomNew) {
                if (atomNew != null) {
                    logger.info("Atom selected " + atomNew.getName());
                    visualization.selectAtom(atomNew);
                }
            }
        });
        nameColumn.setCellValueFactory(new PropertyValueFactory<XyzAtom, String>("name"));
        xColumn.setCellValueFactory(new PropertyValueFactory<XyzAtom, String>("x"));
        yColumn.setCellValueFactory(new PropertyValueFactory<XyzAtom, String>("y"));
        zColumn.setCellValueFactory(new PropertyValueFactory<XyzAtom, String>("z"));
    }

    @Override
    protected void fillButtonBar() {
        Button prevButton = ButtonFactory.createButtonBarButton("Previous", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Going back to molecule list.");
                navigateTo(MoleculeListPage.class);
            }
        });
        addButtonToButtonBar(prevButton);
        Button nextButton = ButtonFactory.createButtonBarButton("Next", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Going to input parameters.");

                MultipoleGaussParameterDto inputDto = new MultipoleGaussParameterDto(xyzFile);
                navigateTo(MultipoleGaussParameterPage.class, inputDto);
            }
        });
        addButtonToButtonBar(nextButton);
    }

    @Override
    public void initializeData() {
        logger.info("Loading data.");
        try {
            xyzFile = dto.getXyzFile();
            if (xyzFile == null) {
                File sessionCopy = copyXyzToCurrentSession(dto.getCoordinatesFile());
                xyzFile = XyzFileParser.parse(sessionCopy);
            }
        } catch (Exception e) {
            OverlayDialog.showError("Error in XYZ file.",
                    "There was an error while parsing the XYZ file. Please check the file for errors.");

            navigateTo(SelectCoordinateFilePage.class, dto.getCoordinatesFile());
            return;
        }

        setInfoLabel();
        fillCoordinatesTable();
        openVisualization();
    }

    private void setInfoLabel() {
        String msg = String.format("The file %s contains the following coordinates.", xyzFile.getSource().getName());
        lblFileInfo.setText(msg);
    }

    private void fillCoordinatesTable() {
        coordinatesTable.setItems(FXCollections.observableArrayList(xyzFile.getAtoms()));
    }

    private File copyXyzToCurrentSession(File input) {
        logger.info("Copying XYZ file to current session directory.");
        try {
            // this overwrites existing files.
            FileUtils.copyFileToDirectory(input, moleculesDir.getDirectory());
        } catch (IOException e) {
            throw new RuntimeException("Could not copy file.", e);
        }
        return new File(moleculesDir.getDirectory(), input.getName());
    }

    private void openVisualization() {
        logger.info("openVisualization");
        visualization.show(xyzFile.getSource());
    }

    public void handleShowVisualization(ActionEvent event) {
        openVisualization();
    }
}

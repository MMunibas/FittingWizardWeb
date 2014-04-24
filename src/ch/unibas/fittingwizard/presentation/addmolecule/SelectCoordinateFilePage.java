package ch.unibas.fittingwizard.presentation.addmolecule;

import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import ch.unibas.fittingwizard.presentation.base.WizardPage;
import ch.unibas.fittingwizard.presentation.MoleculeListPage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * User: mhelmer
 * Date: 25.11.13
 * Time: 18:25
 */
public class SelectCoordinateFilePage extends WizardPage {

    @FXML
    private TextField txtCoordinateFile;

    private File selectedFile;

    Button nextButton;
    private ObjectProperty<File> coordinateFile = new SimpleObjectProperty<File>();

    public SelectCoordinateFilePage(File dto) {
        super("Read coordinate file");
        this.selectedFile = dto;
        coordinateFile.addListener(new ChangeListener<File>() {
            @Override
            public void changed(ObservableValue<? extends File> observableValue, File old, File newValue) {
                if (newValue != null && newValue.exists()) {
                    txtCoordinateFile.setText(newValue.getAbsolutePath());
                    SelectCoordinateFilePage.this.selectedFile = newValue;
                    nextButton.setDisable(false);
                } else {
                    SelectCoordinateFilePage.this.selectedFile = null;
                    nextButton.setDisable(true);
                }
            }
        });
    }

    @Override
    protected void fillButtonBar() {
        Button prevButton = ButtonFactory.createButtonBarButton("Previous", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Going back to molecule list.");
                navigateTo(MoleculeListPage.class);
            }});
        addButtonToButtonBar(prevButton);
        nextButton = ButtonFactory.createButtonBarButton("Next", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Going to parse file.");

                CoordinatesDto coordinatesDto = new CoordinatesDto(selectedFile);
                navigateTo(CoordinatesPage.class, coordinatesDto);
            }
        });
        nextButton.setDisable(true);
        addButtonToButtonBar(nextButton);
    }

    @Override
    public void initializeData() {
        if (selectedFile != null)
            coordinateFile.setValue(selectedFile);
    }

    public void handleSelectAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Coordinate file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.dir"))
        );
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Coordinate file", "*.xyz"));
        File file = fileChooser.showOpenDialog(this.getScene().getWindow());
        coordinateFile.set(file);
    }
}

package ch.scs.unibas.fittingwizard.presentation.addmolecule;

import ch.scs.unibas.fittingwizard.application.scripts.multipolegauss.MultipoleGaussInput;
import ch.scs.unibas.fittingwizard.presentation.base.ButtonFactory;
import ch.scs.unibas.fittingwizard.presentation.base.DefaultValues;
import ch.scs.unibas.fittingwizard.presentation.base.WizardPage;
import ch.scs.unibas.fittingwizard.presentation.base.dialog.OverlayDialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * User: mhelmer
 * Date: 26.11.13
 * Time: 18:14
 */
public class MultipoleGaussParameterPage extends WizardPage {
    private final MultipoleGaussParameterDto dto;

    private Button nextButton;

    @FXML
    private TextField txtNetCharge;
    @FXML
    private TextField txtQuantumChemicalDetails;
    @FXML
    private TextField txtNumberOfCores;
    @FXML
    private TextField txtState;

    public MultipoleGaussParameterPage(DefaultValues defaultValues,
                                       MultipoleGaussParameterDto dto) {
        super("Multipole Gaussian MEP Input");
        this.dto = dto;

        initializeDefaults(defaultValues);
    }

    private void initializeDefaults(DefaultValues defaultValues) {
        txtNetCharge.setText(String.valueOf(defaultValues.getNetCharge()));
        txtQuantumChemicalDetails.setText(defaultValues.getQuantumChemicalDetails());
        txtNumberOfCores.setText(String.valueOf(defaultValues.getNumberOfCores()));
        txtState.setText(defaultValues.getState());
    }

    @Override
    protected void fillButtonBar() {
        Button prevButton = ButtonFactory.createButtonBarButton("Previous", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Going back to coordinates.");
                navigateTo(CoordinatesPage.class, new CoordinatesDto(dto.getXyzFile()));
            }
        });
        addButtonToButtonBar(prevButton);

        nextButton = ButtonFactory.createButtonBarButton("Start calculation", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Going to gauss calculation.");

                GaussCalculationDto gaussCalculationDto = new GaussCalculationDto(createGaussInput(), dto.getXyzFile());
                navigateTo(GaussCalculationPage.class, gaussCalculationDto);
            }
        });
        addButtonToButtonBar(nextButton);
    }

    private MultipoleGaussInput createGaussInput() {
        try {
            int charges = Integer.parseInt(txtNetCharge.getText());
            String details = txtQuantumChemicalDetails.getText();
            int cores = Integer.parseInt(txtNumberOfCores.getText());
            int state = Integer.parseInt(txtState.getText());
            return new MultipoleGaussInput(dto.getXyzFile().getMoleculeName(), charges, details, cores, state);
        } catch (Exception e) {
            OverlayDialog.showError("Invalid parameters", "The provided parameters are invalid.");
            throw e;
        }
    }
}

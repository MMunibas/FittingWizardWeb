package ch.unibas.charmmtools.test.gui;

import ch.unibas.charmmtools.files.input.CHARMM_input;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author hedin
 */


public class CHARMM_Input_Assistant implements Initializable {

    private static final Logger logger = Logger.getLogger(CHARMM_Input_Assistant.class);

    /**
     * The TabPane in which several tabs are added
     */
    @FXML
    private TabPane Tab_Pane;
    @FXML
    private Tab Tab_Step1, Tab_Step2;

    /**
     * Everything related to the tab Step1
     */
    @FXML
    private Button button_open_PAR, button_open_RTF, button_open_XYZ;

    @FXML
    private TextArea inpfile_TextArea;

    @FXML
    private TextField textfield_PAR, textfield_RTF, textfield_XYZ;

    @FXML
    private Label RedLabel_Notice;

    @FXML
    private Button button_generate;

    @FXML
    private Button button_reset, button_click_to_edit;

    /**
     * Everything related to the tab Step2
     */
    /**
     * Internal variables
     */
    private boolean PAR_selected = false, RTF_selected = false, XYZ_selected = false;

    /**
     * Here we can add actions done just before showing the window, e.g. disableing some tabs
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tab_Pane.getTabs().remove(Tab_Step2);
    }

    /**
     * Handles the event when one of the 3 button_open_XXX is pressed
     * button_generate is enabled only when the 3 files have been loaded
     *
     * @param event
     */
    @FXML
    protected void OpenButtonPressed(ActionEvent event) {

        Window myParent = inpfile_TextArea.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("./test"));
        File selectedFile = null;

        chooser.setTitle("Open File");

        if (event.getSource().equals(button_open_PAR)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF parameters file", "*.inp", "*.par", "*.prm"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_PAR.setText(selectedFile.getPath());
                PAR_selected = true;
            }
        } else if (event.getSource().equals(button_open_RTF)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF topology file", "*.top", "*.rtf"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_RTF.setText(selectedFile.getPath());
                RTF_selected = true;
            }
        } else if (event.getSource().equals(button_open_XYZ)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XYZ coordinates file", "*.xyz"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_XYZ.setText(selectedFile.getPath());
                XYZ_selected = true;
            }
        } else {
            throw new UnknownError("Unknown Event");
        }

        if (PAR_selected == true && RTF_selected == true && XYZ_selected == true) {
            button_generate.setDisable(false);
        }

    }//end of OpenButtonPressed action

    /**
     * Try to generate an input file with standard parameters, it can be edited later
     *
     * @param event
     */
    @FXML
    protected void GenerateInputFile(ActionEvent event) {

        CHARMM_input inp = null;
        try {
            inp = new CHARMM_input(textfield_XYZ.getText(), textfield_RTF.getText(), textfield_PAR.getText());
            inpfile_TextArea.setText(inp.getContentOfInputFile());
            RedLabel_Notice.setVisible(true);
        } catch (IOException ex) {
            logger.error(ex);
        }

        /**
         * If success enable button for going to step2 tab
         */
        button_click_to_edit.setDisable(false);

    }

    /**
     *
     * @param event
     */
    @FXML
    protected void ResetFields(ActionEvent event) {
        //clear textcontent
        inpfile_TextArea.clear();
        textfield_PAR.clear();
        textfield_RTF.clear();
        textfield_XYZ.clear();

        //disable elements of tab 1
        PAR_selected = false;
        RTF_selected = false;
        XYZ_selected = false;
        RedLabel_Notice.setVisible(false);
        button_generate.setDisable(true);
        button_click_to_edit.setDisable(true);

        // related to tab2
        //Tab_Step2.setDisable(true);
        Tab_Pane.getTabs().removeAll(Tab_Step2);
    }

    /**
     *
     * @param event
     */
    @FXML
    protected void GoToStep1(ActionEvent event) {
        Tab_Pane.getSelectionModel().select(Tab_Step1);
        Tab_Step2.setDisable(true);
    }

    /**
     *
     * @param event
     */
    @FXML
    protected void GoToStep2(ActionEvent event) {
        Tab_Step2.setDisable(false);
        Tab_Pane.getTabs().addAll(Tab_Step2);
        Tab_Pane.getSelectionModel().select(Tab_Step2);
    }

}//end of controller class

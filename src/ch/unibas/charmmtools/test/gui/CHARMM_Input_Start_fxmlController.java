package ch.unibas.charmmtools.test.gui;

import ch.unibas.charmmtools.files.input.CHARMM_input;
import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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


public class CHARMM_Input_Start_fxmlController{

    private static final Logger logger = Logger.getLogger(CHARMM_Input_Start_fxmlController.class);

    @FXML
    private Button button_open_PAR, button_open_RTF, button_open_XYZ;

    @FXML
    private TextArea inpfile_TextArea;

    @FXML
    private TextField textfield_PAR, textfield_RTF, textfield_XYZ;

    @FXML
    private Label RedLabel_Notice;

    @FXML
    protected void OpenButtonPressed(ActionEvent event) {

        Window myParent = inpfile_TextArea.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        File selectedFile = null;

        chooser.setTitle("Open File");
        //chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));

        if (event.getSource().equals(button_open_PAR)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF parameters file", "*.inp", "*.par", "*.prm"));
            selectedFile = chooser.showOpenDialog(myParent);
            textfield_PAR.setText(selectedFile.getAbsolutePath());
        } else if (event.getSource().equals(button_open_RTF)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF topology file", "*.top", "*.rtf"));
            selectedFile = chooser.showOpenDialog(myParent);
            textfield_RTF.setText(selectedFile.getAbsolutePath());
        } else if (event.getSource().equals(button_open_XYZ)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XYZ coordinates file", "*.xyz"));
            selectedFile = chooser.showOpenDialog(myParent);
            textfield_XYZ.setText(selectedFile.getAbsolutePath());
        } else {
            throw new UnknownError("Unknown Event");
        }

    }//end of OpenButtonPressed action

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

    }

}//end of controller class

package ch.unibas.charmmtools.test.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author hedin
 */


public class CHARMM_Input_Simple_fxmlController{

    @FXML
    private Button button_open_PAR, button_open_RTF, button_open_XYZ;

    @FXML
    private FileChooser choose_PAR, choose_RTF, choose_XYZ;

    @FXML
    private TextArea inpfile_TextArea;

    File PAR_File, RTF_File, XYZ_File;

    @FXML
    private void OpenButtonPressed(ActionEvent event) {
        if (event.getSource().equals(button_open_PAR)) {
//            choose_PAR.showOpenDialog(null);
        } else if (event.getSource().equals(button_open_RTF)) {
//            choose_RTF.showOpenDialog(null);
        } else if (event.getSource().equals(button_open_XYZ)) {
//            choose_XYZ.showOpenDialog(null);
        } else {
            throw new UnknownError("PLOP");
        }

//        inpfile_TextArea.appendText(event.getSource().toString() + "\n");
    }
}

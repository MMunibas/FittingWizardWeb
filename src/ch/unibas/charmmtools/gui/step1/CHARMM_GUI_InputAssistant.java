/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step1;

import ch.unibas.babelBinding.BabelConverterAPI;
import ch.unibas.charmmtools.gui.CHARMM_GUI_base;
import ch.unibas.charmmtools.gui.RunningCHARMM_DenVap;
import ch.unibas.charmmtools.generate.CHARMM_InOut;
import ch.unibas.charmmtools.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.charmmtools.generate.inputs.CHARMM_Input;
import ch.unibas.charmmtools.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.charmmtools.generate.inputs.CHARMM_Input_PureLiquid;
import ch.unibas.charmmtools.gui.MyTab;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.io.FilenameUtils;

public class CHARMM_GUI_InputAssistant extends CHARMM_GUI_base{

    private static final String title = "LJ fitting procedure : preparing CHARMM input files";

    /**
     * All FXML variables
     */
    @FXML
    private Button button_open_PAR, button_open_RTF, button_open_COR_gas,
            button_open_COR_liquid, button_open_COR_solv, button_open_LPUN;

    @FXML
    private TextField textfield_PAR, textfield_RTF, textfield_COR_gas,
            textfield_COR_liquid, textfield_COR_solv, textfield_LPUN;

//    @FXML
//    private Label RedLabel_Notice;
    @FXML
    private Button button_generate;

//    @FXML
//    private TextArea textarea_left, textarea_right;
    // those buttons are NOT exposed to FXML but handles locally with fillbuttonbar
    private Button button_reset;
//    private Button button_save_to_file;
    private Button button_run_CHARMM;
    private Button button_default;

    //where the generated input files are added
    @FXML
    private TabPane tab_pane;

    @FXML
    private TextField lambda_space;

    @FXML
    private Label autoFilledLabel;

    @FXML // fx:id="button_search_DB"
    private Button button_search_DB; // Value injected by FXMLLoader

    /**
     * Internal variables
     */
    private boolean PAR_selected, RTF_selected, COR_selected_gas,
            COR_selected_liquid, COR_selected_solv, LPUN_selected;

    private List<MyTab> tab_list = new ArrayList<>();

    private CHARMM_Generator_DGHydr in_gas_vdw = null, in_gas_mtp = null,
            in_solv_vdw = null, in_solv_mtp = null;

    public CHARMM_GUI_InputAssistant(RunCHARMMWorkflow chWflow) {
        super(title, chWflow);
    }

    public CHARMM_GUI_InputAssistant(RunCHARMMWorkflow chWflow, List<File> flist) {
        super(title, chWflow);

        logger.info("Creating a new instance of CHARMM_GUI_InputAssistant with a list<File> as parameter.");

        for (File f : flist) {
            String path = f.getAbsolutePath();
            String ext = FilenameUtils.getExtension(path);

            logger.info("File " + path + " is of type '" + ext + "' ");

            switch (ext) {
                case "xyz":
                    textfield_COR_gas.setText(convertWithBabel(f).getAbsolutePath());
                    COR_selected_gas = true;
                    logger.info("xyz override detected ; converting to a CHARMM compatible format");
                    break;
                case "lpun":
                    textfield_LPUN.setText(path);
                    LPUN_selected = true;
                    logger.info("lpun override detected");
                    break;
                default:
                    break;
            }
        }

        autoFilledLabel.setVisible(true);
    }

    private File convertWithBabel(File xyz) {
        String parent = FilenameUtils.getFullPath(xyz.getAbsolutePath());
        String basename = FilenameUtils.getBaseName(xyz.getAbsolutePath());

        File pdbOut = new File(parent + basename + ".pdb");

        BabelConverterAPI babelc = new BabelConverterAPI("xyz", "pdb");
        babelc.convert(xyz.getAbsolutePath(), pdbOut.getAbsolutePath());

        if (pdbOut.exists()) {
            try {
                logger.info("Content of pdb file obtained from babel : \n"
                        + Files.readAllBytes(Paths.get(pdbOut.getAbsolutePath()))
                );
            } catch (IOException ex) {
            }
        }

        return pdbOut;
    }

//    public CHARMM_GUI_InputAssistant(RunCHARMMWorkflow chWflow, List<CHARMM_InOut> ioList) {
//
//        super(title, chWflow);
//
//        for (CHARMM_InOut ioListIt : ioList) {
//
//            Class c = ioListIt.getClass();
//            Class sc = c.getSuperclass();
//
//            if (sc == CHARMM_Input.class) {
//                inp.add((CHARMM_Input) ioListIt);
//            } else if (sc == CHARMM_Output.class) {
//                out.add((CHARMM_Output) ioListIt);
//            } else {
//                throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : get "
//                        + ioListIt.getClass() + " but expected types are " + CHARMM_Input.class
//                        + " or " + CHARMM_Output.class);
//            }
//        }
//
////        textarea_left.setText(inp.get(0).getText());
////        textarea_left.setEditable(true);
////
////        textarea_right.setText(inp.get(1).getText());
////        textarea_right.setEditable(true);
////
////        RedLabel_Notice.setText("Error while running CHARMM ! Please modify input file(s) !");
////        RedLabel_Notice.setVisible(true);
////        button_save_to_file.setDisable(false);
//
//        textfield_PAR.setDisable(true);
//        textfield_RTF.setDisable(true);
//        textfield_COR_gas.setDisable(true);
//        textfield_COR_liquid.setDisable(true);
//        textfield_LPUN.setDisable(true);
//
//        button_generate.setDisable(true);
//
//        button_open_PAR.setDisable(true);
//        button_open_RTF.setDisable(true);
//        button_open_COR_gas.setDisable(true);
//        button_open_COR_liquid.setDisable(true);
//        button_open_LPUN.setDisable(true);
//
////        coor_type_gas.setDisable(true);
////        coor_type_liquid.setDisable(true);
////        
////        later_PAR.setDisable(true);
////        later_RTF.setDisable(true);
////        later_COR_gas.setDisable(true);
////        later_COR_liquid.setDisable(true);
////        later_LPUN.setDisable(true);
//    }
    @FXML
    protected void setDefault(ActionEvent e) {
        textfield_PAR.setText(new File("test", "nma.par").getAbsolutePath());
        textfield_RTF.setText(new File("test", "nma.rtf").getAbsolutePath());
        textfield_COR_gas.setText(new File("test", "solute.pdb").getAbsolutePath());
        textfield_COR_liquid.setText(new File("test", "pureliquid.pdb").getAbsolutePath());
        textfield_COR_solv.setText(new File("test", "solvent.pdb").getAbsolutePath());
        textfield_LPUN.setText(new File("test", "fit_0_nma.lpun").getAbsolutePath());

        PAR_selected = RTF_selected = COR_selected_gas = COR_selected_liquid = COR_selected_solv = LPUN_selected = true;
        this.button_generate.setDisable(false);
    }

    /**
     * Here we can add actions done just before showing the window
     */
    @Override
    public void initialize() {

//        later_PAR.setAllowIndeterminate(false);
//        later_RTF.setAllowIndeterminate(false);
//        later_COR_gas.setAllowIndeterminate(false);
//        later_COR_liquid.setAllowIndeterminate(false);
//        later_LPUN.setAllowIndeterminate(false);
//
//        avail_coor_types = FXCollections.observableArrayList();
//        avail_coor_types.addAll(/*"*.xyz", "*.cor", */"*.pdb");
//        
//        coor_type_gas.setItems(avail_coor_types);
//        coor_type_gas.setValue("*.pdb");
//        
//        coor_type_liquid.setItems(avail_coor_types);
//        coor_type_liquid.setValue("*.pdb");
        // set to false those booleans indicating if a file has been selected
        PAR_selected = false;
        RTF_selected = false;
        COR_selected_gas = false;
        COR_selected_liquid = false;
        COR_selected_solv = false;
        LPUN_selected = false;

        this.tab_pane.getTabs().clear();
    }

    /**
     * Enable or Disable the button_generate if required
     */
    private void validateButtonGenerate() {
        button_generate.setDisable(true);

        if (PAR_selected == true && RTF_selected == true && COR_selected_gas == true
                && COR_selected_liquid == true && COR_selected_solv == true && LPUN_selected == true) {
            button_generate.setDisable(false);
        }
    }

    /**
     * Handles the event when one of the 3 button_open_XXX is pressed button_generate is enabled only when the 3 files
     * have been loaded
     *
     * @param event
     */
    @FXML
    protected void OpenButtonPressed(ActionEvent event) {

        Window myParent = button_generate.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("./test"));
        File selectedFile = null;

        chooser.setTitle("Open File");

        if (event.getSource().equals(button_open_PAR)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF parameters file (*.par,*.prm)",
                    "*.par", "*.prm"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_PAR.setText(selectedFile.getAbsolutePath());
                PAR_selected = true;
            }
        } else if (event.getSource().equals(button_open_RTF)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF topology file (*.top,*.rtf)",
                    "*.top", "*.rtf"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_RTF.setText(selectedFile.getAbsolutePath());
                RTF_selected = true;
            }
        } else if (event.getSource().equals(button_open_COR_gas)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Coordinates file (*.pdb)", "*.pdb"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_COR_gas.setText(selectedFile.getAbsolutePath());
                COR_selected_gas = true;
            }
        } else if (event.getSource().equals(button_open_COR_liquid)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Coordinates file (*.pdb)", "*.pdb"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_COR_liquid.setText(selectedFile.getAbsolutePath());
                COR_selected_liquid = true;
            }
        } else if (event.getSource().equals(button_open_COR_solv)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Coordinates file (*.pdb)", "*.pdb"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_COR_solv.setText(selectedFile.getAbsolutePath());
                COR_selected_solv = true;
            }
        } else if (event.getSource().equals(button_open_LPUN)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("LPUN file", "*.lpun"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_LPUN.setText(selectedFile.getAbsolutePath());
                LPUN_selected = true;
            }
        } else {
            throw new UnknownError("Unknown Event in OpenButtonPressed(ActionEvent event)");
        }

        this.validateButtonGenerate();

    }//end of OpenButtonPressed action

    /**
     * Try to generate an input file with standard parameters, it can be edited later
     */
    @FXML
    protected void GenerateInputFile() {

//        try {
        String folderPath = new File("test").getAbsolutePath();

        // get filenames
//        String corname_gas = ResourceUtils.getRelativePath(textfield_COR_gas.getText(), folderPath);
//        String corname_liquid = ResourceUtils.getRelativePath(textfield_COR_liquid.getText(), folderPath);
//        String rtfname = ResourceUtils.getRelativePath(textfield_RTF.getText(), folderPath);
//        String parname = ResourceUtils.getRelativePath(textfield_PAR.getText(), folderPath);
//        String lpunname = ResourceUtils.getRelativePath(textfield_LPUN.getText(), folderPath);
        String corname_gas = textfield_COR_gas.getText();
        String corname_liquid = textfield_COR_liquid.getText();
        String rtfname = textfield_RTF.getText();
        String parname = textfield_PAR.getText();
        String lpunname = textfield_LPUN.getText();
        String time = Long.toString(Instant.now().getEpochSecond());

        File gas_vdw_dir = new File("test/gas_" + time + "/vdw");
        File gas_mtp_dir = new File("test/gas_" + time + "/mtp");
        File solv_vdw_dir = new File("test/solv_" + time + "/vdw");
        File solv_mtp_dir = new File("test/solv_" + time + "/mtp");

        gas_vdw_dir.mkdirs();
        gas_mtp_dir.mkdirs();
        solv_vdw_dir.mkdirs();
        solv_mtp_dir.mkdirs();
//        } catch (IOException ex) {
//            logger.error(ex);
//        }

        File gasFile = null;
        CHARMM_Input gasInp = null;
        try {
            gasFile = new File(gas_vdw_dir.getParent(), "gas_phase.inp");
            gasInp = new CHARMM_Input_GasPhase(corname_gas, rtfname, parname, lpunname, gasFile);
            tab_list.add(
                    new MyTab("ρ/ΔH Gas Phase",
                            new String(Files.readAllBytes(Paths.get(gasFile.getAbsolutePath())))
                    )
            );
            inp.add(gasInp);
        } catch (IOException ex) {
            logger.error("Error while generating " + gasFile.getAbsolutePath() + " : " + ex);
        }

        File liqFile = null;
        CHARMM_Input liqInp = null;
        try {
            liqFile = new File(solv_vdw_dir.getParent(), "pure_liquid.inp");
            liqInp = new CHARMM_Input_PureLiquid(corname_liquid, rtfname, parname, lpunname, liqFile);
            tab_list.add(
                    new MyTab("ρ/ΔH Pure Liquid",
                            new String(Files.readAllBytes(Paths.get(liqFile.getAbsolutePath())))
                    )
            );
            inp.add(liqInp);
        } catch (IOException ex) {
            logger.error("Error while generating " + liqFile.getAbsolutePath() + " : " + ex);
        }

//            RedLabel_Notice.setVisible(true);
        String corname_solv = textfield_COR_solv.getText();
        double lamb_spacing_val = Double.valueOf(lambda_space.getText());

        in_gas_vdw = new CHARMM_Generator_DGHydr(corname_gas, rtfname, parname, lpunname, "vdw",
                0.0, lamb_spacing_val, 1.0, gas_vdw_dir);
//            CHARMM_inFile.addAll(in_gas_vdw.getMyFiles());
//
        in_gas_mtp = new CHARMM_Generator_DGHydr(corname_gas, rtfname, parname, lpunname, "mtp",
                0.0, lamb_spacing_val, 1.0, gas_mtp_dir);
//            CHARMM_inFile.addAll(in_gas_mtp.getMyFiles());
//
        in_solv_vdw = new CHARMM_Generator_DGHydr(corname_gas, corname_solv, rtfname, rtfname,
                parname, lpunname, "vdw", 0.0, lamb_spacing_val, 1.0, solv_vdw_dir);
//            CHARMM_inFile.addAll(in_solv_vdw.getMyFiles());
//
        in_solv_mtp = new CHARMM_Generator_DGHydr(corname_gas, corname_solv, rtfname, rtfname,
                parname, lpunname, "mtp", 0.0, lamb_spacing_val, 1.0, solv_mtp_dir);

        try {
            for (File fi : in_gas_vdw.getMyFiles()) {
                tab_list.add(new MyTab(
                        "Gas & VDW", new String(Files.readAllBytes(Paths.get(fi.getAbsolutePath())))
                ));
            }

            for (File fi : in_gas_mtp.getMyFiles()) {
                tab_list.add(new MyTab(
                        "Gas & MTP", new String(Files.readAllBytes(Paths.get(fi.getAbsolutePath())))
                ));
            }

            for (File fi : in_solv_vdw.getMyFiles()) {
                tab_list.add(new MyTab(
                        "Solv & VDW", new String(Files.readAllBytes(Paths.get(fi.getAbsolutePath())))
                ));
            }

            for (File fi : in_solv_mtp.getMyFiles()) {
                tab_list.add(new MyTab(
                        "Solv & MTP", new String(Files.readAllBytes(Paths.get(fi.getAbsolutePath())))
                ));
            }
        } catch (IOException ex) {
            logger.error("Error while generating DG files : " + ex);
        }

        tab_pane.getTabs().addAll(tab_list);

        button_run_CHARMM.setDisable(false);

        /**
         * If success enable button for saving
         */
//        button_save_to_file.setDisable(false);
    }

    /**
     * Handles the event when one of the 4 checkBox is selected. If the 4 are set to true button_generate is enabled as
     * the 4 required files will be chosen later
     *
     * @param event
     */
//    @FXML
//    protected void CheckBoxActions(ActionEvent event) {
//
//        if (event.getSource().equals(later_PAR)) {
//            PAR_selected = later_PAR.isSelected();
//            button_open_PAR.setDisable(later_PAR.isSelected());
//            textfield_PAR.setDisable(later_PAR.isSelected());
//        } else if (event.getSource().equals(later_RTF)) {
//            RTF_selected = later_RTF.isSelected();
//            button_open_RTF.setDisable(later_RTF.isSelected());
//            textfield_RTF.setDisable(later_RTF.isSelected());
//        } else if (event.getSource().equals(later_COR_gas)) {
//            COR_selected_gas = later_COR_gas.isSelected();
//            button_open_COR_gas.setDisable(later_COR_gas.isSelected());
//            textfield_COR_gas.setDisable(later_COR_gas.isSelected());
//            coor_type_gas.setDisable(later_COR_gas.isSelected());
//        } else if (event.getSource().equals(later_COR_liquid)) {
//            COR_selected_liquid = later_COR_liquid.isSelected();
//            button_open_COR_liquid.setDisable(later_COR_liquid.isSelected());
//            textfield_COR_liquid.setDisable(later_COR_liquid.isSelected());
//            coor_type_liquid.setDisable(later_COR_liquid.isSelected());
//        } else if (event.getSource().equals(later_LPUN)) {
//            LPUN_selected = later_LPUN.isSelected();
//            button_open_LPUN.setDisable(later_LPUN.isSelected());
//            textfield_LPUN.setDisable(later_LPUN.isSelected());
//        } else {
//            throw new UnknownError("Unknown Event");
//        }
//
//        this.validateButtonGenerate();
//    }
    /**
     *
     * @param event
     */
    protected void ResetFields(ActionEvent event) {

//        later_PAR.setDisable(false);
//        later_RTF.setDisable(false);
//        later_COR_gas.setDisable(false);
//        later_COR_liquid.setDisable(false);
//        later_LPUN.setDisable(false);
        //clear textcontent
//        textarea_left.clear();
//        textarea_right.clear();
        textfield_PAR.clear();
        textfield_RTF.clear();
        textfield_COR_gas.clear();
        textfield_COR_liquid.clear();
        textfield_COR_solv.clear();
        textfield_LPUN.clear();

        //disable some elements 
        PAR_selected = false;
        RTF_selected = false;
        COR_selected_gas = false;
        COR_selected_liquid = false;
        COR_selected_solv = false;
        LPUN_selected = false;

//        later_PAR.setSelected(false);
//        button_open_PAR.setDisable(later_PAR.isSelected());
//        textfield_PAR.setDisable(later_PAR.isSelected());
//        later_RTF.setSelected(false);
//        button_open_RTF.setDisable(later_RTF.isSelected());
//        textfield_RTF.setDisable(later_RTF.isSelected());
//        later_COR_gas.setSelected(false);
//        button_open_COR_gas.setDisable(later_COR_gas.isSelected());
//        textfield_COR_gas.setDisable(later_COR_gas.isSelected());
//        coor_type_gas.setDisable(later_COR_gas.isSelected());
//        later_COR_liquid.setSelected(false);
//        button_open_COR_liquid.setDisable(later_COR_liquid.isSelected());
//        textfield_COR_liquid.setDisable(later_COR_liquid.isSelected());
//        coor_type_liquid.setDisable(later_COR_liquid.isSelected());
//        later_LPUN.setSelected(false);
//        button_open_LPUN.setDisable(later_LPUN.isSelected());
//        textfield_LPUN.setDisable(later_LPUN.isSelected());
//        RedLabel_Notice.setVisible(false);
        button_generate.setDisable(true);
//        button_save_to_file.setDisable(true);
        button_run_CHARMM.setDisable(true);

//        button_save_to_file.setText("Click to save");
//        button_run_CHARMM.setText("Run CHARMM");
        inp.clear();
        out.clear();
        CHARMM_inFile.clear();
        CHARMM_outFile.clear();

        tab_list.clear();
        tab_pane.getTabs().clear();

        lambda_space.setText("0.1");

        autoFilledLabel.setVisible(false);

    }

//    protected void SaveToFile(ActionEvent event) {
//        Window myParent = button_save_to_file.getScene().getWindow();
//        FileChooser chooser = new FileChooser();
//        chooser.setInitialDirectory(new File("./test"));
//
//        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM input file", "*.inp"));
//
//        for (CHARMM_Input ip : inp) {
//
//            if (ip.getClass() == CHARMM_Input_GasPhase.class) {
//                chooser.setInitialFileName("gas_phase.inp");
//                chooser.setTitle("Please save the file for Gas Phase calculation");
//            } else if (ip.getClass() == CHARMM_Input_PureLiquid.class) {
//                chooser.setInitialFileName("pure_liquid.inp");
//                chooser.setTitle("Please save the file for Pure Liquid calculation");
//            }
//
//            File selectedFile = chooser.showSaveDialog(myParent);
//            CHARMM_inFile.add(selectedFile);
//
//            BufferedWriter buffw = null;
//
//            if (selectedFile != null) {
//                try {
//                    buffw = new BufferedWriter(new FileWriter(selectedFile));
//                    buffw.write(ip.getText());
//                    buffw.close();
//                } catch (IOException ex) {
//                    logger.error("IOException raised whene generating CHARMM inputfile : " + ex.getMessage());
//                }
//            } else {
//                logger.error("Error while setting file name or save path for CHARMM input file.");
//            }
//
//            ip.setInp(selectedFile);
//
//        }
//
//        //now that it is saved it may be runned
////        this.RedLabel_Notice.setText("You can now try to run the simulation(s)");
//        this.button_run_CHARMM.setDisable(false);
//
//        /**
//         * Allow running charmm script
//         */
//        button_run_CHARMM.setDisable(false);
//
//    }
    protected void runCHARMM(ActionEvent event) {

        List<CHARMM_InOut> myList = new ArrayList<>();
        myList.addAll(inp);
        myList.addAll(out);
        myList.add(in_gas_vdw);
        myList.add(in_gas_mtp);
        myList.add(in_solv_vdw);
        myList.add(in_solv_mtp);
        navigateTo(RunningCHARMM_DenVap.class, myList);

    }

    @FXML
    protected void searchInDB(ActionEvent event) {

        // TODO : popup window ?
//        Stage stage = new Stage();
//        Parent root = this;
//        stage.setScene(new Scene(root));
//        stage.setTitle("My modal window");
//        stage.initModality(Modality.WINDOW_MODAL);
//        stage.initOwner(
//                ((Node) event.getSource()).getScene().getWindow());
//        stage.show();
        
        //another (failing) attempt of popup like
//        Parent root = FxmlUtil.getFxmlContent(DB_SelectForCHARMM.class, this);
//        Stage stage = new Stage();
//        stage.setTitle("My New Stage Title");
//        stage.setScene(new Scene(root, this.getWidth(), this.getHeight()));
//        stage.show();
        
        //this.serialize(this.getClass().getName(), this);
        
//        List<File> flist = new ArrayList<>();
//        navigateTo(DB_SelectForCHARMM.class,flist);
    }

    @Override
    protected void fillButtonBar() {

        button_reset = ButtonFactory.createButtonBarButton("Reset", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Resetting all fields.");
                ResetFields(actionEvent);
            }
        });
        addButtonToButtonBar(button_reset);

//        button_save_to_file = ButtonFactory.createButtonBarButton("Click to save files ", new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                logger.info("Saving CHARMM input script(s) to file(s).");
//                SaveToFile(actionEvent);
//            }
//        });
//        addButtonToButtonBar(button_save_to_file);
//        button_save_to_file.setDisable(true);
        button_run_CHARMM = ButtonFactory.createButtonBarButton("Run CHARMM ρ - ΔH - ΔG simulations",
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        logger.info("Running CHARMM input script.");
                        runCHARMM(actionEvent);
                    }
                });
        addButtonToButtonBar(button_run_CHARMM);
        button_run_CHARMM.setDisable(true);
    }

}//end of controller class

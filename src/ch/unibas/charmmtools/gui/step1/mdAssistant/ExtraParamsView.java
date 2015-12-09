/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step1.mdAssistant;

import ch.unibas.fittingwizard.presentation.base.dialog.ModalDialog;
import ch.unibas.fittingwizard.presentation.base.ui.MainWindow;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Window;

/**
 *
 * @author hedin
 */
public class ExtraParamsView extends ModalDialog{
    
    @FXML // fx:id="Table"
    private TableView<ExtraParamsModel> Table; // Value injected by FXMLLoader
    
    @FXML // fx:id="ParamColumn"
    private TableColumn<ExtraParamsModel, String> ParamColumn; // Value injected by FXMLLoader
        
    @FXML // fx:id="ValueColumn"
    private TableColumn<ExtraParamsModel, String> ValueColumn; // Value injected by FXMLLoader

    private final Window primary;
    
    CheckBox cb;
    
    public ExtraParamsView()
    {
        super("Modifying extra parameters for CHARMM calculations ...");
        primary = MainWindow.getPrimaryStage().getScene().getWindow();
        primary.getScene().getRoot().setEffect(new BoxBlur());
        setup();
    }
    
    private void setup()
    {
        ParamColumn.setCellValueFactory(
                new PropertyValueFactory<>("parameter")
        );
        
        ValueColumn.setCellValueFactory(
                new PropertyValueFactory<>("value")
        );

        ValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        ValueColumn.setOnEditCommit((CellEditEvent<ExtraParamsModel, String> t) -> {
            ((ExtraParamsModel) t.getTableView().getItems().get(
                    t.getTablePosition().getRow())).setValue(t.getNewValue());
        });
        
    }
    
    private void fillTable(List<ExtraParamsModel>  models)
    {
        Table.getItems().clear();
        Table.getItems().addAll(models);
    }
    
    public List<ExtraParamsModel> edit(List<ExtraParamsModel> model_in)
    {
        fillTable(model_in);
        
        showAndWait();
        
        return Table.getItems();
    }
    
    @FXML
    void handleCancel(ActionEvent event) {
        logger.info("Modification of CHARMM parameters cancelled");
        primary.getScene().getRoot().setEffect(null);
        close();
    }

    @FXML
    void handleDone(ActionEvent event) {
        logger.info("Modification of CHARMM parameters done");
        primary.getScene().getRoot().setEffect(null);
        close();
    }
    
}

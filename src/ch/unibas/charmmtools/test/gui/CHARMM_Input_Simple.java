/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.test.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public class CHARMM_Input_Simple extends Application {

    private static final Logger logger = Logger.getLogger(CHARMM_Input_Simple.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        launch(args);
//        CHARMM_input input = new CHARMM_input("test_coordinates.xyz", "test_topol.rtf", "test_params.par");
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("CHARMM_Input_Simple_fxml.fxml"));

        Scene scene = new Scene(root, 300, 275);

        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }

} //end of class

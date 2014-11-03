/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.test;

//import ch.unibas.charmmtools.files.input.CHARMM_input;
//import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public class CHARMM_Input_Test extends Application {

    private static final Logger logger = Logger.getLogger(CHARMM_Input_Test.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        logger.info("Application starting.");

        primaryStage.setTitle("My CHARMM GUI");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        logger.info("Application stopped.");
        System.exit(0);
    }

} //end of class

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//
//        try {
//            // TODO code application logic here
//
//            CHARMM_input input = new CHARMM_input("test_coordinates.xyz", "test_topol.rtf", "test_params.par");
//        } catch (IOException ex) {
//            Logger.getLogger(CHARMM_Input_Test.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }


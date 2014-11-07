/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.test;

import ch.unibas.fittingwizard.Settings;
import ch.unibas.fittingwizard.presentation.CHARMM_Input_Page;
import ch.unibas.fittingwizard.presentation.MoleculeListPage;
import ch.unibas.fittingwizard.presentation.base.Wizard;
import ch.unibas.fittingwizard.presentation.base.WizardPageFactory;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public class CHARMM_Input_Test extends Application {

    private static final Logger logger = Logger.getLogger(CHARMM_Input_Test.class);

//    private Settings settings;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            logger.info("Application starting.");

            Parent root = this.setup(primaryStage);

            Scene scene = new Scene(root, 1024, 768);
//          this.loadStylesheets(scene);
            primaryStage.setScene(scene);
            primaryStage.setTitle("My CHARMM GUI");
            primaryStage.show();
        } catch (Exception e) {
            logger.error("Error in application startup application.", e);
            throw e;
        }

    }//end start

    @Override
    public void stop() throws Exception {
        super.stop();
        logger.info("Application stopped.");
        System.exit(0);
    }

    private Parent setup(Stage primaryStage) {
        WizardPageFactory factory = new WizardPageFactory(primaryStage);
        Wizard wizard = new Wizard(factory);
//        wizard.navigateTo(MoleculeListPage.class, null);
        wizard.navigateTo(CHARMM_Input_Page.class, null);
//        this.settings = factory.getSettings();
        return wizard;
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


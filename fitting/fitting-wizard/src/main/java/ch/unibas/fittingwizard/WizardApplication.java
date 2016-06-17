/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fittingwizard.gaussian.base.Wizard;
import ch.unibas.fittingwizard.gaussian.base.WizardPageFactory;
import ch.unibas.fittingwizard.gaussian.base.ui.MainWindow;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import static org.apache.log4j.PatternLayout.TTCC_CONVERSION_PATTERN;

/**
 * This is the main entry point of the application.
 *
 * @author mhelmer
 *
 */
public class WizardApplication extends Application {

    private static final Logger logger = Logger.getLogger(WizardApplication.class);

    /**
     * For checking that there is a config file and that it contains proper
     * keywords
     */
    private Settings settings;

    @Override
    public void start(Stage primaryStage) {
        setupConsoleLogger();
        MainWindow.setPrimaryStage(primaryStage);

        logger.info("Application starting.");
        try {
            Parent root = this.setupWizard(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.centerOnScreen();
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);
//            primaryStage.setResizable(false);

            this.loadStylesheets(scene);
            primaryStage.setScene(scene);
            String version = this.getVersionFromManifest();
            primaryStage.setTitle("Multipole-electrostatics and Lennard-Jones fitting wizard - " + version);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Icon.png")));

            /**
             * use the settings object to be sure that config file is OK
             */
            boolean isOkay = new ApplicationSetup(settings).verify();
            if (isOkay) {
                primaryStage.show();
            } else {
                logger.error("Exiting application because some dependencies are missing.");
                System.exit(1);
            }

        } catch (Exception e) {
            logger.error("Error in application startup application.", e);
            throw e;
        }
    }

    private String getVersionFromManifest() {
        Class clazz = WizardApplication.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            return "no manifest found";
        }
        String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1)
                + "/META-INF/MANIFEST.MF";
        Manifest manifest = null;
        try {
            manifest = new Manifest(new URL(manifestPath).openStream());
        } catch (IOException e) {
            throw new RuntimeException("Could not load manifest.");
        }
        Attributes attr = manifest.getMainAttributes();
        String value = attr.getValue("Implementation-Version");
        return "v" + value;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        logger.info("Application stopped.");
       
        // TODO find out how to clean up JMOL stuff correctly in order to avoid this
        // JMOL starts some thread which keep the process alive
        System.exit(0);
    }

    private void loadStylesheets(Scene scene) {
        URL u = getClass().getResource("application.css");

        scene.getStylesheets().add(u.toExternalForm());
    }

    private Parent setupWizard(Stage primaryStage) {
        WizardPageFactory factory = new WizardPageFactory(primaryStage);
        Wizard wizard = new Wizard(factory);
        wizard.navigateTo(WhereToGo.class, null);
        this.settings = factory.getSettings();
        return wizard;
    }

    private static void setupConsoleLogger() {
        
        BasicConfigurator.configure();
        
        FileAppender app = null;
        File log = new File("logs/wizard.log");
        log.mkdir();
        
        try {
            app = new FileAppender(new PatternLayout(TTCC_CONVERSION_PATTERN), log.getAbsolutePath() ,false);
        } catch (IOException ex) {

        }
        
        BasicConfigurator.configure(app);
        log.deleteOnExit();
        logger.setLevel(Level.WARN);
    }
}

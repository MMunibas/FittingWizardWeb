/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.presentation.base.ui;

import javafx.stage.Stage;

/**
 * TODO find out if JavaFX provides a mean to retrieve the main window
 * User: mhelmer
 * Date: 11.12.13
 * Time: 11:02
 */
public class MainWindow {
    public static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        MainWindow.primaryStage = primaryStage;
    }
}

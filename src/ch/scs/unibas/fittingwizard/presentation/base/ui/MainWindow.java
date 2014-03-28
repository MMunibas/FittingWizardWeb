package ch.scs.unibas.fittingwizard.presentation.base.ui;

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

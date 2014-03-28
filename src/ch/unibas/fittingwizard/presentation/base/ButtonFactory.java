package ch.unibas.fittingwizard.presentation.base;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * User: mhelmer
 * Date: 26.11.13
 * Time: 11:24
 */
public class ButtonFactory {
    public static Button createButtonBarButton(String title, EventHandler<ActionEvent> onButtonClick) {
        Button button = new Button(title);
        button.setOnAction(onButtonClick);
        button.getStyleClass().add("buttonStyle");
        button.setPrefHeight(40);
        button.setMinWidth(100);
        return button;
    }
}

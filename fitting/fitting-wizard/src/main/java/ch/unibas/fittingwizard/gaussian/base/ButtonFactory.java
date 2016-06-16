/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.base;

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

/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

/**
 *
 * @author hedin
 */
// inner class defining an extended tab type
public class MyTab extends Tab {

    private TextArea myTextContent = new TextArea();

    public MyTab() {
        myTextContent.setFont(Font.font("Monospaced", 14));
        this.setContent(myTextContent);
    }

    public MyTab(String text) {
        super(text);
        myTextContent.setFont(Font.font("Monospaced", 14));
        this.setContent(myTextContent);
    }
    
    public MyTab(String text, String textAreaContent) {
        super(text);
        myTextContent.setFont(Font.font("Monospaced", 14));
        this.setContent(myTextContent);
        this.setContentText(textAreaContent);
    }

    public void setContentText(String txt) {
        this.myTextContent.setText(txt);
    }

    public void clearContent() {
        this.myTextContent.clear();
    }

    public String getContentText() {
        return this.myTextContent.getText();
    }

    public void setContentEditable(boolean isEditable) {
        this.myTextContent.setEditable(isEditable);
    }
}

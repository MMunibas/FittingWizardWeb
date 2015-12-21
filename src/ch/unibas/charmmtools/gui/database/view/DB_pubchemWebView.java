/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database.view;

import ch.unibas.fittingwizard.presentation.base.dialog.ModalDialog;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *
 * @author hedin
 */
public class DB_pubchemWebView extends ModalDialog {

    private final String url;

    private final Scene parent;

    private WebView browser;
    private WebEngine webEngine;

    public DB_pubchemWebView(String _url, Scene _parent) {

        super("PubChem view of " + _url);

        url = _url;
        parent = _parent;

        parent.getRoot().setEffect(new BoxBlur());
    }

    public void view() {
        webEngine.load(url);

        this.setAlwaysOnTop(true);

        this.showAndWait();

        parent.getRoot().setEffect(null);

    }

    @Override
    protected void createScene() {
        browser = new WebView();
        webEngine = browser.getEngine();
        this.setScene(new Scene(browser, 800, 600));
    }

}

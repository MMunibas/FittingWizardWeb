package ch.scs.unibas.fittingwizard.presentation.base;

/**
 * User: mhelmer
 * Date: 25.11.13
 * Time: 11:18
 */

import javafx.scene.layout.StackPane;

/** basic wizard infrastructure class */
public class Wizard extends StackPane {

    private final WizardPageFactory wizardPageFactory;

    public Wizard(WizardPageFactory wizardPageFactory) {
        this.wizardPageFactory = wizardPageFactory;
        getStyleClass().add("wizard");
    }

    public <T extends WizardPage> void navigateTo(Class<T> type, Object parameter)  {
        WizardPage next = wizardPageFactory.create(type, parameter);
        getChildren().clear();
        getChildren().add(next);
        // ensures that the page has a wizard (e.g. for showing error dialogs)
        next.initializeData();
    }
}

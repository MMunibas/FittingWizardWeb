package ch.scs.unibas.fittingwizard.presentation.base;

/**
 * User: mhelmer
 * Date: 25.11.13
 * Time: 11:17
 */

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;

/** basic wizard page class */
public abstract class WizardPage<TParam> extends VBox {

    protected final Logger logger;

    private HBox buttonBar;

    public WizardPage(String title) {
        logger = Logger.getLogger(getClass());
        logger.info("Creating.");
        Label lblTitle = LabelBuilder.create()
                .text(title)
                .style("-fx-font-weight: bold; -fx-padding: 0 0 5 0; -fx-font-size: 20px")
                .build();
        getChildren().add(lblTitle);
        setId(title);
        setSpacing(5);
        getStyleClass().add("wizardPage");

        Region spring = new Region();
        VBox.setVgrow(spring, Priority.ALWAYS);
        this.buttonBar = createButtonBar();
        getChildren().addAll(getContent(), spring, buttonBar);
        fillButtonBar();
    }

    /**
     * This method may be used to initialize text fields and other UI stuff.
     * The constructor of the created page is not executed yet, so all passed
     * services are not initialized yet. Use {@link #initializeData()} to load data
     * and do other service related stuff.
     */
    public void initialize() {
    }

    /**
     * This method may be used to initialize the controller with data.
     * When this method is called, all services and FXML fields will be initialized.
     */
    public void initializeData() {
    }

    protected abstract void fillButtonBar();

    private HBox createButtonBar() {
        Region spring = new Region();
        HBox.setHgrow(spring, Priority.ALWAYS);
        HBox buttonBar = new HBox(5);
        buttonBar.getChildren().add(spring);
        this.buttonBar = buttonBar;
        return buttonBar;
    }

    protected void addButtonToButtonBar(Button button) {
        buttonBar.getChildren().add(button);
    }

    /**
     * Gets the content of the view. <br/>
     * By default the controller tries to load a FXML file with the following naming convention:<br/>
     * <b>ControllerSimpleName + .fxml</b>
     * @return
     */
    protected Parent getContent() {
        return FxmlUtil.getFxmlContent(getTypeForFxml(), this);
    }

    protected Class getTypeForFxml() {
        return getClass();
    }

    public <T extends WizardPage<TParam>> void navigateTo(Class<T> type) {
        navigateTo(type, null);
    }

    public <T extends WizardPage<TParam>> void navigateTo(Class<T> type, TParam parameter) {
        getWizard().navigateTo(type, parameter);
    }

    Wizard getWizard() {
        return (Wizard) getParent();
    }
}

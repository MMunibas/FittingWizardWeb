package ch.scs.unibas.fittingwizard.presentation.base;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 10:33
 */
public class FxmlUtil {
    private static final Logger logger = Logger.getLogger(FxmlUtil.class);

    public static Parent getFxmlContent(Class type, Object controller) {
        String resourceName = type.getSimpleName() + ".fxml";
        logger.info("Loading FXML " + resourceName);
        URL url = type.getResource(resourceName);
        Parent content = null;
        try {
            FXMLLoader loader = new FXMLLoader(url);
            if (controller != null) {
                loader.setController(controller);
            }
            content = (Parent) loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content;
    }
}

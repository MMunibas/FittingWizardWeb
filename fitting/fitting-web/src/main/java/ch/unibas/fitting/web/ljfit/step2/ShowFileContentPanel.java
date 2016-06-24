package ch.unibas.fitting.web.ljfit.step2;

import ch.unibas.fitting.web.web.WizardPanel;
import org.apache.wicket.markup.html.basic.MultiLineLabel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Created by tobias on 23.06.16.
 */
public class ShowFileContentPanel extends WizardPanel {

    public ShowFileContentPanel(String id, File file) {
        super(id);

        add(new MultiLineLabel("content", readFileContent(file)));
    }

    private String readFileContent(File file) {
        String content = "";

        Random random = new Random();
        try {
            content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (IOException ex) {
            Logger.error("Error while reading " + file.getAbsolutePath() + " : " + ex);
        }

        return content;
    }
}

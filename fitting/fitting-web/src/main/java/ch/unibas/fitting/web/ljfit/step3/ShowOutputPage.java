package ch.unibas.fitting.web.ljfit.step3;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.web.ljfit.step1.InputAssistantPage;
import ch.unibas.fitting.web.ljfit.step2.ShowFileContentPanel;
import ch.unibas.fitting.web.ljfit.step4.ShowResultsPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.io.File;

/**
 * Created by tschmidt on 16.06.2016.
 */
public class ShowOutputPage extends HeaderPage {

    private IModel<String> gasPhaseError;
    private IModel<String> liquidPhaseError;

    @Inject
    private IUserDirectory userDir;

    public ShowOutputPage() {
        checkForErrors();

        add(new AjaxLink("proceed") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(ShowResultsPage.class);
            }

        });

        add(new AjaxLink("backToInput") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(InputAssistantPage.class);
            }

        });

        File dir = userDir.getFitOutputDir(getCurrentUsername()).getFitMtpOutputDir();

        add(new Label("gasPhaseError", gasPhaseError));
        add(new Label("liquidPhaseError", liquidPhaseError));
        add(new ShowFileContentPanel("gasPhasePanel", new File(dir, "fit_0_fit_results.txt")));
        add(new ShowFileContentPanel("liquidPhasePanel",new File(dir, "fit_0_output.txt")));
    }

    private void checkForErrors() {
        // TODO: get actual errors
        gasPhaseError = Model.of("No error while running CHARMM");
        liquidPhaseError = Model.of("No error while running CHARMM");
    }
}

package ch.unibas.fitting.web.ljfit.step2;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.web.ljfit.step1.InputAssistantPage;
import ch.unibas.fitting.web.ljfit.step3.ShowOutput;
import ch.unibas.fitting.web.web.HeaderPage;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tschmidt on 16.06.2016.
 */
public class ShowGeneratedInputPage extends HeaderPage {

    @Inject
    private IUserDirectory userDir;

    List<ITab> tabs = new ArrayList<>();

    public ShowGeneratedInputPage() {

        add(new AjaxLink("runCharmm") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(ShowOutput.class);
            }

        });

        add(new AjaxLink("backToInput") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(InputAssistantPage.class);
            }

        });

        setDefaultModel(new Model<String>("tabpanel"));

        File dir = userDir.getFitOutputDir(getCurrentUsername()).getFitMtpOutputDir();


        File dummyFile = new File("");
        tabs.add(new AbstractTab(new Model<String>("first tab")) {
            @Override
            public Panel getPanel(String panelId) {
                return new ShowFileContentPanel(panelId, new File(dir, "fit_0_fit_results.txt"));
            }
        });

        tabs.add(new AbstractTab(new Model<String>("second tab")) {
            @Override
            public Panel getPanel(String panelId) {
                return new ShowFileContentPanel(panelId, new File(dir, "fit_0_output.txt"));
            }
        });

        tabs.add(new AbstractTab(new Model<String>("third tab")) {
            @Override
            public Panel getPanel(String panelId) {
                return new ShowFileContentPanel(panelId, new File(dir, "fit_1_fit_results.txt"));
            }
        });

        add(new AjaxTabbedPanel("tabs", tabs));
    }
}

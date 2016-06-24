package ch.unibas.fitting.web.ljfit.step2;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.directories.UserDirectory;
import ch.unibas.fitting.web.gaussian.FitUserRepo;
import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
import ch.unibas.fitting.web.web.HeaderPage;


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

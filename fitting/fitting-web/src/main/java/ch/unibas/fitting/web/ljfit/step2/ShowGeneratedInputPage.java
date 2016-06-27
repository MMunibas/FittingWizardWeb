package ch.unibas.fitting.web.ljfit.step2;

import ch.unibas.fitting.shared.workflows.charmm.CharmmInputContainer;
import ch.unibas.fitting.web.gaussian.progress.ProgressPage;
import ch.unibas.fitting.web.ljfit.CharmmRepository;
import ch.unibas.fitting.web.ljfit.step1.InputAssistantPage;
import ch.unibas.fitting.web.web.HeaderPage;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tschmidt on 16.06.2016.
 */
public class ShowGeneratedInputPage extends HeaderPage {

    @Inject
    private RunCharmmCommand runCharmmCommand;
    @Inject
    private CharmmRepository charmmRepository;

    List<ITab> tabs = new ArrayList<>();

    public ShowGeneratedInputPage() {

        add(new AjaxLink("runCharmm") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                UUID id = runCharmmCommand.run(getCurrentUsername());
                PageParameters pp = new PageParameters();
                pp.add("task_id", id);
                setResponsePage(ProgressPage.class, pp);
            }
        });

        add(new AjaxLink("backToInput") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(InputAssistantPage.class);
            }
        });

        setDefaultModel(new Model<String>("tabpanel"));

        Optional<CharmmInputContainer> container = charmmRepository.getContainerFor(getCurrentUsername());
        if (container.isPresent()) {
            CharmmInputContainer c = container.get();
            addTab("ρ/ΔH Gas Phase", c.getGasInput().getOut());
            addTab("ρ/ΔH Pure Liquid", c.getLiquidInput().getOut());

            c.getGasVdw().listOutputFiles().forEach(file -> addTab("Gas & VDW", file));
            c.getGasMtp().listOutputFiles().forEach(file -> addTab("Gas & MTP", file));

            c.getSolvVdw().listOutputFiles().forEach(file -> addTab("Solv & VDW", file));
            c.getSolvMtp().listOutputFiles().forEach(file -> addTab("Solv & MTP", file));
        }

        add(new AjaxTabbedPanel("tabs", tabs));
    }

    private void addTab(String name, File file) {
        tabs.add(new AbstractTab(Model.of(name)) {
            @Override
            public Panel getPanel(String panelId) {
                return new ShowFileContentPanel(panelId, file);
            }
        });
    }
}

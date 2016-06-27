package ch.unibas.fitting.web.ljfit.step3;

import ch.unibas.fitting.shared.charmm.web.CharmmResult;
import ch.unibas.fitting.web.ljfit.CharmmRepository;
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
import java.util.Optional;

/**
 * Created by tschmidt on 16.06.2016.
 */
public class ShowOutputPage extends HeaderPage {

    private IModel<String> gasPhaseError;
    private IModel<String> liquidPhaseError;

    @Inject
    private CharmmRepository charmmRepository;

    public ShowOutputPage() {

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

        gasPhaseError = Model.of("No error while running CHARMM");
        add(new Label("gasPhaseError", gasPhaseError));

        liquidPhaseError = Model.of("No error while running CHARMM");
        add(new Label("liquidPhaseError", liquidPhaseError));

        Optional<CharmmResult> result = charmmRepository.getResultFor(getCurrentUsername());

        if (result.isPresent()) {
            CharmmResult r = result.get();

            // TODO set to RED color
            if (r.hasGasPhaseError())
                gasPhaseError.setObject("ERROR !!!!!!!!");

            add(new ShowFileContentPanel("gasPhasePanel", r.getGasPhaseOutputFile()));

            // TODO set to RED color
            if (r.hasLiquidPhaseError())
                liquidPhaseError.setObject("ERROR !!!!!!!!!");

            add(new ShowFileContentPanel("liquidPhasePanel", r.getLiquidPhaseOutputFile()));
        } else {

            add(new Label("gasPhasePanel", "no result available"));
            add(new Label("liquidPhasePanel", "no result available"));
        }
    }
}

package ch.unibas.fitting.web.ljfit.ui.step3;

import ch.unibas.fitting.shared.charmm.web.CharmmResult;
import ch.unibas.fitting.web.ljfit.services.CharmmRepository;
import ch.unibas.fitting.web.ljfit.ui.step1.CreateNewSessionPage;
import ch.unibas.fitting.web.ljfit.ui.step2.ShowResultsPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by tschmidt on 16.06.2016.
 */
public class ShowOutputPage extends HeaderPage {

    private final String noErrorString = "No error while running CHARMM";
    private final String errorString = "Error while running CHARMM";

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
                setResponsePage(CreateNewSessionPage.class);
            }

        });

        Label gasPhaseErrorLabel = new Label("gasPhaseError", "");
        Label liquidPhaseErrorLabel = new Label("liquidPhaseError", "");

        Optional<CharmmResult> result = charmmRepository.getResultFor(getCurrentUsername());

        if (result.isPresent()) {
            CharmmResult r = result.get();
            
            if (r.hasGasPhaseError()) {
                gasPhaseErrorLabel = createErrorLabel("gasPhaseError");

            } else {
                gasPhaseErrorLabel = createNoErrorLabel("gasPhaseError");
            }

            add(new ShowFileContentPanel("gasPhasePanel", r.getGasPhaseOutputFile()));

            if (r.hasLiquidPhaseError()) {
                liquidPhaseErrorLabel = createErrorLabel("liquidPhaseError");
            } else {
                liquidPhaseErrorLabel = createNoErrorLabel("liquidPhaseError");
            }

            add(new ShowFileContentPanel("liquidPhasePanel", r.getLiquidPhaseOutputFile()));
        } else {
            add(new Label("gasPhasePanel", "no result available"));
            add(new Label("liquidPhasePanel", "no result available"));
        }

        add(gasPhaseErrorLabel);
        add(liquidPhaseErrorLabel);
    }

    private Label createErrorLabel(String id) {
        Label label = new Label(id, errorString);
        label.add(new AttributeModifier("class", "alert alert-danger"));
        return label;
    }

    private Label createNoErrorLabel(String id) {
        Label label = new Label(id, noErrorString);
        label.add(new AttributeModifier("class", "alert alert-success"));
        return label;
    }
}

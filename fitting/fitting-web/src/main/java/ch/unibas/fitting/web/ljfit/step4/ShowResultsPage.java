package ch.unibas.fitting.web.ljfit.step4;

import ch.unibas.fitting.shared.charmm.web.CharmmResult;
import ch.unibas.fitting.shared.charmm.web.CharmmResultCalculator;
import ch.unibas.fitting.shared.charmm.web.ResultCalculatorOutput;
import ch.unibas.fitting.web.ljfit.CharmmRepository;
import ch.unibas.fitting.web.ljfit.step1.InputAssistantPage;
import ch.unibas.fitting.web.ljfit.step3.ShowOutputPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by tschmidt on 16.06.2016.
 */
public class ShowResultsPage extends HeaderPage {

    private IModel<Double> temperature = Model.of();
    private IModel<Double> molarMass = Model.of();
    private IModel<Integer> numberOfResidues = Model.of();
    private String density = "";
    private String deltaH = "";
    private String deltaG = "";

    @Inject
    private CharmmRepository charmmRepository;

    public ShowResultsPage() {
        Optional<CharmmResult> result = charmmRepository.getResultFor(getCurrentUsername());
        setInitialValues(result);

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        add(new AjaxLink("download") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                // TODO: generate download link
            }
        });

        add(new AjaxLink("backToInput") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(InputAssistantPage.class);
            }
        });

        add(new AjaxLink("backToOutput") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(ShowOutputPage.class);
            }
        });

        Label densityLabel = new Label("density", new PropertyModel<>(this, "density"));
        densityLabel.setOutputMarkupId(true);
        add(densityLabel);

        Label deltaHLabel = new Label("deltaH", new PropertyModel<>(this, "deltaH"));
        deltaHLabel.setOutputMarkupId(true);
        add(deltaHLabel);

        Label deltaGLabel = new Label("deltaG", new PropertyModel<>(this, "deltaG"));
        deltaGLabel.setOutputMarkupId(true);
        add(deltaGLabel);

        Form formInput = new Form("formInput");
        formInput.add(createRequiredDoubleTextField("temperature", temperature));
        formInput.add(createRequiredDoubleTextField("molarMass", molarMass));
        formInput.add(createRequiredIntegerTextField("numberOfResidues", numberOfResidues));
        formInput.add(new AjaxButton("calculate") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                if (result.isPresent()) {
                    LOGGER.debug("Calculating with temperature: " + temperature.getObject() +
                            " molar mass: " + molarMass.getObject() +
                            " number of residues " + numberOfResidues.getObject());

                    ResultCalculatorOutput calculatedResult = CharmmResultCalculator.calculateResult(
                            numberOfResidues.getObject(),
                            molarMass.getObject(),
                            temperature.getObject(),
                            result.get().getOutput());

                    LOGGER.debug("Calculated values: " +
                            " density: " + calculatedResult.getDensity() +
                            " deltaH: " + calculatedResult.getDeltaH() +
                            " deltaG: " + calculatedResult.getDeltaG());

                    density = Double.toString(calculatedResult.getDensity());
                    deltaH = Double.toString(calculatedResult.getDeltaH());
                    deltaG = Double.toString(calculatedResult.getDeltaG());

                    target.add(densityLabel);
                    target.add(deltaHLabel);
                    target.add(deltaGLabel);
                    target.add(fp);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form)
            {
                super.onError(target, form);
                target.add(fp);
            }
        });
        add(formInput);
    }

    private static NumberTextField createRequiredDoubleTextField(String id, IModel model) {
        NumberTextField numberTextField = new NumberTextField(id, model);
        numberTextField.setRequired(true);
        numberTextField.setStep(NumberTextField.ANY);
        return numberTextField;
    }

    private static NumberTextField createRequiredIntegerTextField(String id, IModel model) {
        NumberTextField numberTextField = new NumberTextField(id, model);
        numberTextField.setRequired(true);
        return numberTextField;
    }

    private void setInitialValues(Optional<CharmmResult> result) {
        if (result.isPresent()) {
            temperature = Model.of(result.get().getOutput().getTemp());
            molarMass = Model.of(0.0);
            numberOfResidues = Model.of(result.get().getOutput().getNres());
        } else {
            temperature = Model.of();
            molarMass = Model.of();
            numberOfResidues = Model.of();
        }
    }
}

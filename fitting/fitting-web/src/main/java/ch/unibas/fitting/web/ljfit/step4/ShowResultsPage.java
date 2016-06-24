package ch.unibas.fitting.web.ljfit.step4;

import ch.unibas.fitting.web.ljfit.step1.InputAssistantPage;
import ch.unibas.fitting.web.ljfit.step3.ShowOutputPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.commons.logging.Log;
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

    public ShowResultsPage() {
        loadInputValues();

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
                Logger.debug("Calculating with temperature: " + temperature.getObject() +
                             " molar mass: " + molarMass.getObject() +
                             " number of residues " + numberOfResidues.getObject());

                //TODO: calculate and show real results
                density = Double.toString(12.1*temperature.getObject());
                deltaH = Double.toString(13.4*molarMass.getObject());
                deltaG = Double.toString(3.4*numberOfResidues.getObject());

                target.add(densityLabel);
                target.add(deltaHLabel);
                target.add(deltaGLabel);
                target.add(fp);

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

    private static NumberTextField createRequiredDoubleTextField(String id, IModel temperature) {
        NumberTextField numberTextField = new NumberTextField(id, temperature);
        numberTextField.setRequired(true);
        numberTextField.setStep(NumberTextField.ANY);
        return numberTextField;
    }

    private static NumberTextField createRequiredIntegerTextField(String id, IModel temperature) {
        NumberTextField numberTextField = new NumberTextField(id, temperature);
        numberTextField.setRequired(true);
        return numberTextField;
    }

    private void loadInputValues() {
        // TODO: load real values
        temperature = Model.of(298.0);
        molarMass = Model.of(94.112);
        numberOfResidues = Model.of(150);
    }
}

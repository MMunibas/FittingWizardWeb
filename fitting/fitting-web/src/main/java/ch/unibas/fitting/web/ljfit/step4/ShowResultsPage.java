package ch.unibas.fitting.web.ljfit.step4;

import ch.unibas.fitting.shared.charmm.web.CharmmResult;
import ch.unibas.fitting.shared.charmm.web.CharmmResultCalculator;
import ch.unibas.fitting.shared.charmm.web.ResultCalculatorOutput;
import ch.unibas.fitting.shared.directories.CharmmOutputDir;
import ch.unibas.fitting.shared.directories.FitOutputDir;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.web.ljfit.CharmmRepository;
import ch.unibas.fitting.web.ljfit.CreateCsvExport;
import ch.unibas.fitting.web.ljfit.step1.InputAssistantPage;
import ch.unibas.fitting.web.ljfit.step3.ShowOutputPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;
import java.io.File;
import java.util.Optional;

/**
 * Created by tschmidt on 16.06.2016.
 */
public class ShowResultsPage extends HeaderPage {

    private IModel<Double> temperature = Model.of(0.0);
    private IModel<Double> molarMass = Model.of(0.0);
    private IModel<Integer> numberOfResidues = Model.of(0);
    private Double density;
    private Double deltaH;
    private Double deltaG;

    @Inject
    private CharmmRepository charmmRepository;
    @Inject
    private CreateCsvExport createCsvExport;
    @Inject
    private IUserDirectory userDirectory;

    public ShowResultsPage() {
        Optional<CharmmResult> result = charmmRepository.getResultFor(getCurrentUsername());
        setInitialValues(result);

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        IModel fileModel = new AbstractReadOnlyModel(){
            public Object getObject() {

                Optional<CharmmResult> result = charmmRepository.getResultFor(getCurrentUsername());

                CharmmOutputDir dir = userDirectory.getCharmmOutputDir(getCurrentUsername());
                File f = createCsvExport.create(dir.getDefaultExportDir(), new CreateCsvExport.Input(
                        result.get().getOutput().getEgas(),
                        result.get().getOutput().getEliq(),
                        temperature.getObject(),
                        molarMass.getObject(),
                        density,
                        deltaH,
                        deltaG
                ));

                return f;
            }
        };

        final DownloadLink link = new DownloadLink("download", fileModel) {
            @Override
            public boolean isVisible() {
                return density != null &&
                        deltaG != null &&
                        deltaH != null;
            }
        };
        link.setOutputMarkupId(true);
        link.setOutputMarkupPlaceholderTag(true);
        add(link);

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

                    density = calculatedResult.getDensity();
                    deltaH = calculatedResult.getDeltaH();
                    deltaG = calculatedResult.getDeltaG();

                    target.add(densityLabel);
                    target.add(deltaHLabel);
                    target.add(deltaGLabel);
                    target.add(fp);
                    target.add(link);
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
        numberTextField.setMinimum(Double.MIN_VALUE);
        return numberTextField;
    }

    private static NumberTextField createRequiredIntegerTextField(String id, IModel model) {
        NumberTextField numberTextField = new NumberTextField(id, model);
        numberTextField.setRequired(true);
        return numberTextField;
    }

    private void setInitialValues(Optional<CharmmResult> result) {
        if (result.isPresent()) {
            temperature.setObject(result.get().getOutput().getTemp());
            numberOfResidues.setObject(result.get().getOutput().getNres());
        }
    }
}

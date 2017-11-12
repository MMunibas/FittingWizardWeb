package ch.unibas.fitting.web.ljfit.ui.step2;

import ch.unibas.fitting.web.ljfit.ui.step2.run.RunFromPage;
import ch.unibas.fitting.web.ljfit.ui.step2.run.RunLjFitsCommand;
import ch.unibas.fitting.web.ljfit.ui.step2.run.RunPair;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class SingleRunPanel extends Panel {

    public IModel<Double> lambda = Model.of(0.0);

    public SingleRunPanel(String id, ModalWindow window,
                          String username,
                          RunLjFitsCommand runLjFitsCommand) {
        super(id);

        EpsilonSigmaPair singlePair = new EpsilonSigmaPair(1.0,1.0, true);

        Form singleForm = new Form("singleForm");
        add(singleForm);

        TextField epsField = new TextField<Double>("epsilon", new PropertyModel(singlePair, "eps"));
        epsField.setRequired(true);
        singleForm.add(epsField);

        TextField sigmaField = new TextField("sigma", new PropertyModel(singlePair, "sigma"));
        sigmaField.setRequired(true);
        singleForm.add(sigmaField);

        NumberTextField lambdaField = new NumberTextField("lambda", lambda);
        lambdaField.setRequired(true);
        lambdaField.setStep(NumberTextField.ANY);
        singleForm.add(lambdaField);

        singleForm.add(new AjaxButton("runSingle") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                RunPair pair = new RunPair(singlePair.getEps(), singlePair.getSigma());

                runLjFitsCommand.execute(username, new RunFromPage(pair, lambda.getObject()));
            }
        });
    }
}
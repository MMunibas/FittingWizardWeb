package ch.unibas.fitting.web.ljfit.ui.step2;

import ch.unibas.fitting.web.ljfit.ui.UiElementFactory;
import ch.unibas.fitting.web.ljfit.ui.step2.clusterparams.ClusterParameterViewModel;
import ch.unibas.fitting.web.ljfit.ui.step2.run.RunFromPage;
import ch.unibas.fitting.web.ljfit.ui.step2.run.RunLjFitsCommand;
import ch.unibas.fitting.web.ljfit.ui.step2.run.RunPair;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class SingleRunPanel extends Panel {

    public IModel<Double> lambda_size_electrostatic = Model.of(0.0);
    public IModel<Double> lambda_size_vdw = Model.of(0.0);

    public SingleRunPanel(String id,
                          String username,
                          RunLjFitsCommand runLjFitsCommand,
                          ClusterParameterViewModel clusterParameter) {
        super(id);

        var singlePair = new EpsilonSigmaPair(
                1.0,
                1.0,
                true);

        Form singleForm = new Form("singleForm");
        add(singleForm);

        TextField epsField = new TextField<Double>("epsilon", new PropertyModel(singlePair, "eps"));
        epsField.setRequired(true);
        singleForm.add(epsField);

        TextField sigmaField = new TextField("sigma", new PropertyModel(singlePair, "sigma"));
        sigmaField.setRequired(true);
        singleForm.add(sigmaField);

        singleForm.add(UiElementFactory.createLambdaValueField("lambda_size_electrostatic", lambda_size_electrostatic));
        singleForm.add(UiElementFactory.createLambdaValueField("lambda_size_vdw", lambda_size_vdw));

        singleForm.add(new AjaxButton("runSingle") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                RunPair pair = new RunPair(singlePair.getSigma(), singlePair.getEps());

                runLjFitsCommand.executeNew(username, new RunFromPage(
                        pair,
                        lambda_size_electrostatic.getObject(),
                        lambda_size_vdw.getObject(),
                        clusterParameter.getNcpus()
                        ));

            }
        });
    }
}
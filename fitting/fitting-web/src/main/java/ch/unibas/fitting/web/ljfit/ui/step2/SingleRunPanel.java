package ch.unibas.fitting.web.ljfit.ui.step2;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

public class SingleRunPanel extends Panel {

    public SingleRunPanel(String id, ModalWindow window, EpsilonSigmaPair singlePair) {
        super(id);

        Form singleForm = new Form("singleForm");
        add(singleForm);

        TextField epsField = new TextField<Double>("epsilon", new PropertyModel(singlePair, "eps"));
        epsField.setRequired(true);
        singleForm.add(epsField);

        TextField sigmaField = new TextField("sigma", new PropertyModel(singlePair, "sigma"));
        sigmaField.setRequired(true);
        singleForm.add(sigmaField);

        singleForm.add(new AjaxButton("runSingle") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {


            }
        });



    }
}
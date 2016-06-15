package ch.unibas.fitting.web.gaussian.step4;

import ch.unibas.fitting.web.gaussian.step5.ProgressPage;
import ch.unibas.fitting.web.web.HeaderPage;
import ch.unibas.fitting.web.web.WizardPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.RangeValidator;

/**
 * Created by martin on 05.06.2016.
 */
public class ParameterPage extends HeaderPage {

    private IModel<Integer> _netCharge = Model.of((Integer)null);

    public ParameterPage() {

        Form form = new Form("form");
        add(form);

        FeedbackPanel fp =new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        add(fp);

        NumberTextField ntf = new NumberTextField<>("netcharge", _netCharge);
        ntf.add(RangeValidator.range(0.0, 10.0));
        form.add(ntf);

        form.add(new AjaxButton("start") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);
                if(isValid())
                    setResponsePage(ProgressPage.class);
            }
        });
    }
}

package ch.unibas.fitting.web.gaussian;

import ch.unibas.fitting.web.web.WizardPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;

/**
 * Created by martin on 05.06.2016.
 */
public class ParameterPage extends WizardPage {
    public ParameterPage() {

        add(new AjaxLink("start") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(UploadPage.class);
            }
        });
    }
}

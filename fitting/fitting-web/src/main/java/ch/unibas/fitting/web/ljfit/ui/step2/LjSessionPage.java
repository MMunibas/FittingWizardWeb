package ch.unibas.fitting.web.ljfit.ui.step2;

import ch.unibas.fitting.web.ljfit.ui.step1.InputAssistantPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

public class LjSessionPage extends HeaderPage {

    public LjSessionPage() {

        add(new AjaxLink("newSession") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(InputAssistantPage.class);
            }
        });



    }
}

package ch.unibas.fitting.web.gaussian.step6;

import ch.unibas.fitting.web.gaussian.step1.OverviewPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public class AtomTypesPage extends HeaderPage {

    public AtomTypesPage() {
        add(new AjaxLink("next") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(OverviewPage.class);
            }
        });
    }
}

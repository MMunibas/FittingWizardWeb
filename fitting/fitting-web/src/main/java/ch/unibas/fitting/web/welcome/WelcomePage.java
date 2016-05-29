package ch.unibas.fitting.web.welcome;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

public class WelcomePage extends WebPage {

    private static final Logger logger = Logger.getLogger(WelcomePage.class);

    private SelectionModel selected;

    public WelcomePage() {
        List<SelectionModel> selections = SelectionModel.createDefaultSelections();

        RadioChoice<SelectionModel> fittingOptions = new RadioChoice<>("types", new PropertyModel<>(this, "selected"), selections);
        fittingOptions.setSuffix("</br>");
        add(fittingOptions);

        Form<?> form = new Form<Void>("form") {
            @Override
            protected void onSubmit() {
                logger.info("Navigating to " + selected.getType().getName());
                setResponsePage(selected.getType());
            }
        };

        add(form);
        form.add(fittingOptions);
    }
}


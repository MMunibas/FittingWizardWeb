package ch.unibas.fitting.web.welcome;

import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.PropertyModel;


import java.util.List;

public class WelcomePage extends HeaderPage {

    private SelectionModel selected;

    public WelcomePage() {

        List<SelectionModel> selections = SelectionModel.createDefaultSelections();

        RadioChoice<SelectionModel> fittingOptions = new RadioChoice<>("types", new PropertyModel<>(this, "selected"), selections);
        fittingOptions.setSuffix("</br>");
        add(fittingOptions);

        Form<?> form = new Form<Void>("form") {
            @Override
            protected void onSubmit() {
                if (selected != null) {
                    LOGGER.debug("Navigating to " + selected.getType().getName());
                    setResponsePage(selected.getType());
                }
            }
        };

        add(form);
        form.add(fittingOptions);
    }
}


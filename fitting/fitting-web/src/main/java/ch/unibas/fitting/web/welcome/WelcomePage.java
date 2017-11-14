package ch.unibas.fitting.web.welcome;

import ch.unibas.fitting.web.gaussian.addmolecule.step1.OverviewPage;
import ch.unibas.fitting.web.ljfit.ui.commands.OpenLjFitSessionCommand;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.PropertyModel;


import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class WelcomePage extends HeaderPage {

    @Inject
    private OpenLjFitSessionCommand startFitSession;

    private String navigationSelection;

    public WelcomePage() {

        List<String> selections = Arrays.asList(new String[] {
                "MTP Fit using Gaussian",
                "LJ Fit using CHARMM" });

        RadioChoice<String> fittingOptions = new RadioChoice<>("types",
                new PropertyModel<>(this, "navigationSelection"), selections);
        fittingOptions.setSuffix("</br>");
        //add(fittingOptions);

        Form<?> form = new Form<Void>("form") {
            @Override
            protected void onSubmit() {

                if ("MTP Fit using Gaussian".equalsIgnoreCase(navigationSelection)) {
                    setResponsePage(OverviewPage.class);
                } else if ("LJ Fit using CHARMM".equalsIgnoreCase(navigationSelection)) {
                    startFitSession.execute(getCurrentUsername());
                }
            }
        };
        form.add(fittingOptions);
        add(form);
    }
}


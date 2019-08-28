package ch.unibas.fitting.web.welcome;

import ch.unibas.fitting.web.mtpfit.commands.OpenMtpFitSession;
import ch.unibas.fitting.web.ljfit.commands.OpenLjFitSessionCommand;
import ch.unibas.fitting.web.misc.HeaderPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.PropertyModel;


import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class WelcomePage extends HeaderPage {

    @Inject
    private OpenLjFitSessionCommand startLjFitSession;
    @Inject
    private OpenMtpFitSession startMtpFitSession;

    private String navigationSelection;

    public WelcomePage() {

        List<String> selections = Arrays.asList(new String[] {
                "MDCM Fit using Gaussian",
                "LJ Fit using CHARMM" });

        RadioChoice<String> fittingOptions = new RadioChoice<>("types",
                new PropertyModel<>(this, "navigationSelection"), selections);
        fittingOptions.setSuffix("</br>");
        //add(fittingOptions);

        Form<?> form = new Form<Void>("form") {
            @Override
            protected void onSubmit() {

                if ("MDCM Fit using Gaussian".equalsIgnoreCase(navigationSelection)) {
                    startMtpFitSession.execute(getCurrentUsername());
                } else if ("LJ Fit using CHARMM".equalsIgnoreCase(navigationSelection)) {
                    startLjFitSession.execute(getCurrentUsername());
                }
            }
        };
        form.add(fittingOptions);
        add(form);
    }
}


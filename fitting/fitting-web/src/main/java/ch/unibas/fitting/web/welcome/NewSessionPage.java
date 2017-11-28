package ch.unibas.fitting.web.welcome;

import ch.unibas.fitting.web.web.UserSession;
import ch.unibas.fitting.web.web.WizardPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 04.06.2016.
 */
public class NewSessionPage extends WizardPage {

    private String username;

    public NewSessionPage() {

        if (session().hasUserName()) {
            setResponsePage(WelcomePage.class);
        }

        Form form = new Form("form");

        RequiredTextField<String> field = new RequiredTextField<String>("username", new PropertyModel<>(this, "username"));
        field.add((IValidator<String>) validate -> {
            Pattern pattern = Pattern.compile(UserSession.UsernamePattern);
            Matcher matcher = pattern.matcher(validate.getValue());
            boolean found = matcher.find();
            if (!found) {
                validate.error(new ValidationError("only A-Z a-z - _ allowed"));
            }
        });
        form.add(field);

        form.add(new Button("start") {
            @Override
            public void onSubmit() {
                if (isValid()) {
                    session().setUsername(username);
                    setResponsePage(WelcomePage.class);
                } else {
                    LOGGER.debug("Form not valid");
                }
            }
        });

        add(form);

        add(new FeedbackPanel("feedback"));
    }
}

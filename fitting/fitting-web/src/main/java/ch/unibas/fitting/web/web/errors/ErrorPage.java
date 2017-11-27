package ch.unibas.fitting.web.web.errors;

import ch.unibas.fitting.web.web.HeaderPage;
import ch.unibas.fitting.web.web.PageNavigation;
import ch.unibas.fitting.web.welcome.WelcomePage;
import io.vavr.control.Option;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Created by mhelmer on 28.06.2016.
 */
public class ErrorPage extends HeaderPage {
    public ErrorPage() {

        Option<ErrorDetails> error = session().getLastError();

        IModel errorMessage = Model.of("No error occured");
        IModel errorDetails = Model.of("");
        IModel errorDate = Model.of("");
        IModel taskTitle = Model.of("");

        error.peek(e -> {
            errorMessage.setObject(e.getMessage());
            errorDate.setObject(e.getDate().toString("dd.MM.YYYY HH:mm:ss"));
            errorDetails.setObject(e.getDetails());
            taskTitle.setObject(e.getTaskTitle());
        });

        add(new AjaxLink("goToOrigin") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                error.flatMap(e -> e.getOrigin())
                    .peek(o -> PageNavigation.ToPage(o))
                    .onEmpty(() -> PageNavigation.ToPage(WelcomePage.class));
            }
        });

        add(new Label("errorMessage", errorMessage));
        add(new Label("errorDate", errorDate));
        add(new Label("taskTitle", taskTitle));
        add(new Label("errorDetails", errorDetails));
    }
}

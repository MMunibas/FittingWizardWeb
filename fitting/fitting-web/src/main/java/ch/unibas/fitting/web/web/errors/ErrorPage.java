package ch.unibas.fitting.web.web.errors;

import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Optional;

/**
 * Created by mhelmer on 28.06.2016.
 */
public class ErrorPage extends HeaderPage {
    public ErrorPage() {

        Optional<ErrorViewModel> error = session().getLastError();

        IModel errorMessage = Model.of("No error occured");
        IModel errorDetails = Model.of("");
        IModel errorDate = Model.of("");
        IModel taskTitle = Model.of("");

        if (error.isPresent()) {
            errorMessage.setObject(error.get().getMessage());
            errorDate.setObject(error.get().getDate().toString("dd.MM.YYYY HH:mm:ss"));
            errorDetails.setObject(error.get().getDetails());
            taskTitle.setObject(error.get().getTaskTitle());
        }

        add(new Label("errorMessage", errorMessage));
        add(new Label("errorDate", errorDate));
        add(new Label("taskTitle", taskTitle));
        add(new Label("errorDetails", errorDetails));
    }
}

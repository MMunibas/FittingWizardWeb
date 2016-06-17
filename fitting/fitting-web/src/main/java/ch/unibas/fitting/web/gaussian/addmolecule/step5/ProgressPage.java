package ch.unibas.fitting.web.gaussian.addmolecule.step5;

import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.gaussian.addmolecule.step4.ParameterPage;
import ch.unibas.fitting.web.gaussian.addmolecule.step6.AtomTypesPage;
import ch.unibas.fitting.web.web.HeaderPage;
import com.google.inject.Inject;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.time.Duration;

import java.util.UUID;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public class ProgressPage extends HeaderPage {

    private final IModel<String> _text;

    private final UUID _taskId;
    private PageParameters pageParameter;

    @Inject
    private IBackgroundTasks _tasks;

    public ProgressPage(PageParameters pp) {
        pageParameter = pp;
        String id = pp.get("task_id").toString();
        if (id != null)
            _taskId = UUID.fromString(id);
        else
            _taskId = null;

        _text = Model.of("Just started ...");

        final Label label = new Label("progress", _text);
        label.setOutputMarkupId(true);
        label.setOutputMarkupPlaceholderTag(true);
        add(label);

        add(new AjaxLink("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                _tasks.cancel(_taskId);
                setResponsePage(ParameterPage.class, pageParameter);
            }
        });

        add(new AbstractAjaxTimerBehavior(Duration.seconds(2)) {

            private int _count;

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                _count++;
                _text.setObject("We are running since " + _count + " seconds");
                target.add(label);

                TaskHandle<String> th = _tasks.getHandle(_taskId);
                if (th.isDone()) {
                    stop(target);

                    if (th.wasSuccessful()) {
                        PageParameters pp = new PageParameters();
                        pp.add("task_id", th.getId());
                        setResponsePage(AtomTypesPage.class, pp);
                    } else {
                        Logger.debug("Task with id=" + _taskId + " failed.");
                        setResponsePage(ParameterPage.class, pageParameter);
                    }
                }
            }
        });
    }
}

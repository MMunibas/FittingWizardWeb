package ch.unibas.fitting.web.gaussian.progress;

import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.gaussian.addmolecule.step4.ParameterPage;
import ch.unibas.fitting.web.web.HeaderPage;
import ch.unibas.fitting.web.welcome.WelcomePage;
import com.google.inject.Inject;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;
import org.joda.time.DateTime;

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

        final Label lblProgress = new Label("progress", _text);
        lblProgress.setOutputMarkupId(true);
        lblProgress.setOutputMarkupPlaceholderTag(true);
        add(lblProgress);

        add(new AjaxLink("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                _tasks.cancel(_taskId);
                setResponsePage(ParameterPage.class, pageParameter);
            }
        });

        add(new AbstractAjaxTimerBehavior(Duration.seconds(2)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                TaskHandle th = _tasks.getHandle(_taskId);

                if (th != null) {
                    org.joda.time.Duration diff = org.joda.time.Duration.millis(DateTime.now().getMillis() - th.getStartTime().getMillis());

                    if (th.isDone()) {
                        stop(target);

                        if (th.wasSuccessful()) {
                            PageParameters pp = new PageParameters();
                            Class page = (Class) th.getNextPageCallback().apply(th.getResult(), pp);
                            setResponsePage(page, pp);
                        } else {
                            Throwable ex = th.getException();
                            Logger.debug("Task with id=" + _taskId + " failed.", ex);
                            setResponsePage(ParameterPage.class, pageParameter);
                        }
                    } else {
                        _text.setObject(th.getTitle() + " is running since " + diff.getStandardSeconds() + " seconds...");
                        target.add(lblProgress);
                    }
                } else {
                    setResponsePage(WelcomePage.class);
                }
            }
        });
    }
}

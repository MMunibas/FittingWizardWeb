package ch.unibas.fitting.web.web.progress;

import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.web.HeaderPage;
import ch.unibas.fitting.web.web.errors.ErrorPage;
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

import java.util.Optional;
import java.util.UUID;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public class ProgressPage extends HeaderPage {

    private final IModel<String> text;
    private final IModel<String> title;

    private final UUID taskId;

    @Inject
    private IBackgroundTasks taskService;

    public ProgressPage(PageParameters pp) {
        String id = pp.get("task_id").toString();
        if (id != null)
            taskId = UUID.fromString(id);
        else
            taskId = null;

        text = Model.of("Just started ...");
        title = Model.of("Processing...");
        Optional<TaskHandle> taskHandle = taskService.getHandle(taskId);
        if (taskHandle.isPresent()) {
            title.setObject("Processing: " + taskHandle.get().getTitle());
            updateRunningTime(taskHandle.get());
        }

        add(new Label("title", title));

        final Label lblProgress = new Label("progress", text);
        lblProgress.setOutputMarkupId(true);
        lblProgress.setOutputMarkupPlaceholderTag(true);
        add(lblProgress);

        add(new AjaxLink("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Optional<Class> page = taskService.cancel(taskId);
                if (page.isPresent()) {
                    setResponsePage(page.get());
                } else {
                    setResponsePage(WelcomePage.class);
                }
            }
        });

        add(new AbstractAjaxTimerBehavior(Duration.seconds(2)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                Optional<TaskHandle> optional = taskService.getHandle(taskId);

                if (!optional.isPresent()) {
                    setResponsePage(WelcomePage.class);
                    return;
                }

                TaskHandle th = optional.get();
                updateRunningTime(th);
                target.add(lblProgress);

                if (th.isDone()) {
                    stop(target);

                    if (th.wasSuccessful()) {
                        taskService.remove(th);
                        PageParameters pp = new PageParameters();
                        Class page = (Class) th.getNextPageCallback().apply(th.getResult(), pp);
                        if (page != null)
                            setResponsePage(page, pp);
                    } else if (th.hasError()) {
                        session().setFailedTask(th);
                        taskService.remove(th);
                        setResponsePage(ErrorPage.class);
                    }
                }
            }
        });
    }

    private void updateRunningTime(TaskHandle th) {
        text.setObject(th.getTitle() + " is running since " + runningTime(th) + " ...");
    }

    private String runningTime(TaskHandle th) {
        org.joda.time.Duration time = th.getRunningTime();
        String txt;
        if (time.getStandardMinutes() > 0) {
            txt = time.getStandardMinutes() + " minutes";
        } else {
            txt = time.getStandardSeconds() + " seconds";
        }
        return txt;
    }
}

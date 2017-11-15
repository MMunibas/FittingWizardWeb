package ch.unibas.fitting.web.web.progress;

import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.web.HeaderPage;
import ch.unibas.fitting.web.web.errors.ErrorPage;
import ch.unibas.fitting.web.welcome.WelcomePage;
import com.google.inject.Inject;
import io.vavr.control.Option;
import org.apache.wicket.Component;
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

    private final IModel<String> title = Model.of("Processing...");
    private final IModel<String> progress = Model.of("Just started ...");
    private final IModel<String> status = Model.of("");

    private final UUID taskId;

    @Inject
    private IBackgroundTasks taskService;

    public ProgressPage(PageParameters pp) {
        String id = pp.get("task_id").toString();
        if (id != null)
            taskId = UUID.fromString(id);
        else
            taskId = null;

        taskService.getHandle(taskId)
                .forEach(th -> updateLabels(th));

        add(new Label("title", title));

        final Component lblProgress;
        add(lblProgress = new Label("progress", progress)
                .setOutputMarkupId(true)
                .setOutputMarkupPlaceholderTag(true));

        final Component lblStatus;
        add(lblStatus = new Label("status", status)
                .setOutputMarkupId(true)
                .setOutputMarkupPlaceholderTag(true));

        add(new AjaxLink("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                taskService.cancel(taskId)
                    .peek(page -> setResponsePage(page))
                    .onEmpty(() -> setResponsePage(WelcomePage.class));
            }
        });

        add(new AbstractAjaxTimerBehavior(Duration.seconds(2)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                target.add(lblProgress);
                target.add(lblStatus);

                taskService.getHandle(taskId)
                    .peek(th -> {
                        updateLabels(th);

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
                    })
                    .onEmpty(() -> setResponsePage(WelcomePage.class));
            }
        });
    }

    private void updateLabels(TaskHandle th) {
        title.setObject("Processing: " + th.getTitle());
        progress.setObject("Running since " + runningTime(th) + " ...");
        status.setObject("Status: " + th.getStatus());
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

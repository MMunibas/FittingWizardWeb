package ch.unibas.fitting.web.calculation;

import akka.actor.ActorRef;
import ch.unibas.fitting.web.calculation.management.CalculationManagementClient;
import ch.unibas.fitting.web.calculation.management.execution.messages.Cancel;
import ch.unibas.fitting.web.calculation.management.execution.messages.ExecutionProgress;
import ch.unibas.fitting.web.calculation.management.task.messages.TaskInfoResponse;
import ch.unibas.fitting.web.web.HeaderPage;
import ch.unibas.fitting.web.welcome.WelcomePage;
import com.google.inject.Inject;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;

public class ActorBasedProgressPage extends HeaderPage {

    private final Model<String> taskTitle = Model.of("");

    private final ListModel<ExecutionProgress> executions = new ListModel<>();

    private String taskId;
    private TaskInfoResponse taskInfo;

    @Inject
    private CalculationManagementClient calculationManagement;

    private Class<? extends IRequestablePage> returnPage = WelcomePage.class;

    private WebMarkupContainer continueButtonContainer;

    public ActorBasedProgressPage(PageParameters pp) {
        taskId= pp.get("task_id").toString();

        add(new Label("title", taskTitle));

        add(new AjaxLink("cancel_all") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                calculationManagement.cancelTask(taskId);
                setResponsePage(returnPage, new PageParameters());
            }
        });

        var execContainer = new WebMarkupContainer("execution_container");
        execContainer.setOutputMarkupId(true);

        continueButtonContainer = new WebMarkupContainer("continue_button_container");
        continueButtonContainer.setOutputMarkupId(true);

        execContainer.add(new ListView<>("executionList", executions) {
            @Override
            protected void populateItem(ListItem item) {
                var calc = ((ExecutionProgress)item.getModelObject());
                item.add(new Label("execId", calc.executionId));
                item.add(new Label("execStatus", calc.state.getStatus()));
                item.add(new Label("execMessage", calc.state.getMessage()));
                item.add(new AjaxLink("execCancel") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        var exec = executions.getObject();
                        exec.remove(calc);
                        calculationManagement.cancelExecution(taskId, calc.executionId);
                        calc.actorRef.tell(new Cancel(), ActorRef.noSender());
                        target.add(execContainer);
                        target.add(continueButtonContainer);
                    }
                });
            }
        });
        add(execContainer);

        continueButtonContainer.add(new AjaxLink("continue_button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                calculationManagement.deleteTask(taskId);
                calculationManagement.cancelTask(taskId);
                setResponsePage(returnPage, new PageParameters());
            }
        });
        add(continueButtonContainer);

        add(new AbstractAjaxTimerBehavior(Duration.seconds(2)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                updateModels();
                target.add(execContainer);
                target.add(continueButtonContainer);
            }
        });
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        updateModels();
    }

    private void updateModels() {
        var task = calculationManagement.getTaskInfo(taskId);
        if(task.title != null)
        taskTitle.setObject(task.title);
        if(task.navigationInfo != null)
            returnPage = task.navigationInfo.returnPage.getOrElse(WelcomePage.class);
        if(task.executions != null) {
            executions.setObject(task.executions);
            if(task.executions.stream().allMatch(ExecutionProgress::isTerminated)){
                continueButtonContainer.setVisible(true);
            }
            else{
                continueButtonContainer.setVisible(false);
            }
        }
        else{
            continueButtonContainer.setVisible(false);
        }
    }
}

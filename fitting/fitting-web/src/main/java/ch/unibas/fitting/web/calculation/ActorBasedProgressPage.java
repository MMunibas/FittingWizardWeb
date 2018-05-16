package ch.unibas.fitting.web.calculation;

import ch.unibas.fitting.shared.javaextensions.Action;
import ch.unibas.fitting.web.application.calculation.CalculationManagementClient;
import ch.unibas.fitting.web.application.calculation.execution.messages.ExecutionProgress;
import ch.unibas.fitting.web.web.HeaderPage;
import com.google.inject.Inject;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;

public class ActorBasedProgressPage extends HeaderPage {

    private final Model<String> taskTitle = Model.of("Initializing");
    private final ListModel<ExecutionProgress> executions = new ListModel<>();

    private final MarkupContainer executionTable;
    private final MarkupContainer continueButton;

    private String taskId;
    private Action continueCallback;

    @Inject
    private CalculationManagementClient calculationManagement;

    public ActorBasedProgressPage(PageParameters pp) {
        var id = pp.get("task_id");
        if (id != null)
            taskId = id.toString();

        add(new Label("title", taskTitle));

        add(new AjaxLink("cancel_all") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (taskId != null) {
                    calculationManagement.cancelTask(taskId);
                }
            }
        });

        add(executionTable = new WebMarkupContainer("executionTable"));
        executionTable.setOutputMarkupId(true);

        executionTable.add(new ListView<>("execution_row", executions) {
            @Override
            protected void populateItem(ListItem item) {
                var calc = ((ExecutionProgress) item.getModelObject());
                item.add(new Label("execId", calc.executionId));
                item.add(new Label("execStatus", calc.status));
                item.add(new Label("execMessage", calc.message));
                item.add(new AjaxLink("execCancel") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (taskId != null) {
                            calculationManagement.cancelExecution(taskId, calc.executionId);
                        }
                    }
                });
            }
        });

        add(continueButton = new AjaxLink("continue_button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (continueCallback != null) {
                    calculationManagement.deleteTask(taskId);
                    continueCallback.execute();
                }
            }
        });
        continueButton.setOutputMarkupId(true);
        continueButton.setVisible(false);

        add(new AbstractAjaxTimerBehavior(Duration.seconds(2)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                updateModels();
                target.add(executionTable);
                target.add(continueButton);
            }
        });
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        updateModels();
    }

    private void updateModels() {
        var taskInfo = calculationManagement.getTaskInfo(taskId);

        if (taskInfo.title != null)
            taskTitle.setObject(taskInfo.title);

        if (taskInfo.executions != null) {
            executions.setObject(taskInfo.executions);

            if (taskInfo.executions.stream().allMatch(e -> e.isCompleted)) {
                continueButton.setVisible(true);
                if (taskInfo.executions.stream().allMatch(e -> e.isFailed) || taskInfo.executions.stream().allMatch(e -> e.isCanceled))
                    continueCallback = taskInfo.navigationInfo.cancelCallback;
                else
                    continueCallback = taskInfo.navigationInfo.doneCallback;
            }
        }
    }
}

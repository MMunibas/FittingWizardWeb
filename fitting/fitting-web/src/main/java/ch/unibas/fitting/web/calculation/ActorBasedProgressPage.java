package ch.unibas.fitting.web.calculation;

import ch.unibas.fitting.web.infrastructure.javaextensions.Action;
import ch.unibas.fitting.web.application.calculation.CalculationManagementClient;
import ch.unibas.fitting.web.application.calculation.execution.RunDetails;
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
    private final ListModel<RunDetails> executions = new ListModel<>();

    private final MarkupContainer executionTable;
    private final AjaxLink continueButton;
    private final AjaxLink cancelAll;

    private String groupId;
    private Action continueCallback;

    @Inject
    private CalculationManagementClient calculationManagement;

    public ActorBasedProgressPage(PageParameters pp) {
        var id = pp.get("group_id");
        if (id != null)
            groupId = id.toString();

        add(new Label("title", taskTitle));

        add(cancelAll = new AjaxLink("cancel_all") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (groupId != null) {
                    calculationManagement.cancelGroup(groupId);
                }
            }
        });
        cancelAll.setOutputMarkupPlaceholderTag(true);
        cancelAll.setOutputMarkupId(true);

        add(executionTable = new WebMarkupContainer("executionTable"));
        executionTable.setOutputMarkupId(true);
        executionTable.add(new ListView<>("execution_row", executions) {
            @Override
            protected void populateItem(ListItem item) {
                var calc = ((RunDetails) item.getModelObject());
                item.add(new Label("calculationId", calc.calculationId));
                item.add(new Label("execStatus", calc.status));
                item.add(new Label("execMessage", calc.message));
                item.add(new AjaxLink("execCancel") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (groupId != null) {
                            calculationManagement.cancelRun(groupId, calc.executionId);
                        }
                    }

                    @Override
                    public boolean isVisible() {
                        return !calc.isCompleted;
                    }
                });
            }
        });

        add(continueButton = new AjaxLink("continue_button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (continueCallback != null) {
                    calculationManagement.finishGroup(groupId);
                    continueCallback.execute();
                }
            }
        });
        continueButton.setOutputMarkupId(true);
        continueButton.setOutputMarkupPlaceholderTag(true);
        continueButton.setVisible(false);

        add(new AbstractAjaxTimerBehavior(Duration.seconds(2)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                updateModels();

                target.add(executionTable);
                target.add(continueButton);
                target.add(cancelAll);
            }
        });
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        updateModels();
    }

    private void updateModels() {
        calculationManagement.getGroupInfo(groupId).forEach(taskInfo -> {
            taskTitle.setObject(taskInfo.title);
            executions.setObject(taskInfo.runs.toJavaList());

            if (taskInfo.runs.forAll(e -> e.isCompleted)) {
                continueButton.setVisible(true);
                cancelAll.setVisible(false);

                if (taskInfo.runs.forAll(e -> !e.isSucceeded))
                    continueCallback = taskInfo.navigationInfo.cancelCallback;
                else
                    continueCallback = taskInfo.navigationInfo.doneCallback;
            }
        });
    }
}

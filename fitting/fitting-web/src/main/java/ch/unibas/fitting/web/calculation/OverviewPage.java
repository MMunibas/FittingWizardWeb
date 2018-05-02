package ch.unibas.fitting.web.calculation;

import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.commands.StartDummyAlgoCommand;
import ch.unibas.fitting.web.calculation.commands.StartDummyLjFitCommand;
import ch.unibas.fitting.web.calculation.management.CalculationManagementClient;
import ch.unibas.fitting.web.calculation.management.execution.messages.ExecutionProgress;
import ch.unibas.fitting.web.web.HeaderPage;
import io.swagger.client.model.CalculationStatus;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;

import javax.inject.Inject;

public class OverviewPage extends HeaderPage {

    @Inject
    private CalculationService calculationService;

    @Inject
    private CalculationManagementClient calculationManagement;

    @Inject
    private StartDummyAlgoCommand startDummyAlgoCommand;
    @Inject
    private StartDummyLjFitCommand startDummyLjFitCommand;

    private Model<String> serviceVersion;
    private Model<String> serviceStatus;
    private Model<String> errorMessageModel;
    private ListModel<String> algorithmsModel;
    private ListModel<CalculationStatus> calculationsModel;
    private ListModel<ExecutionProgress> executionModel;
    private WebMarkupContainer errorMessageContainer;
    public OverviewPage() {
        serviceVersion = new Model<>();
        serviceStatus = new Model<>();
        algorithmsModel = new ListModel<>();
        calculationsModel = new ListModel<>();
        executionModel = new ListModel<>();
        errorMessageModel = new Model<>();
        var overview_page_content = new WebMarkupContainer("overview_page_content");
        overview_page_content.setOutputMarkupId(true);
        overview_page_content.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)){
            @Override
            protected void onPostProcessTarget(final AjaxRequestTarget target)
            {
                updateModels();
                target.add(overview_page_content);
            }
        });

        add(overview_page_content);

        errorMessageContainer = new WebMarkupContainer("errormessage_container");
        errorMessageContainer.add(new Label("errormessage", errorMessageModel));
        overview_page_content.add(errorMessageContainer);

        //service status
        overview_page_content.add(new Label("version", serviceVersion));
        overview_page_content.add(new Label("service_status", serviceStatus));

        //algo list
        overview_page_content.add(new ListView<>("algorithmList", algorithmsModel) {
            @Override
            protected void populateItem(ListItem item) {
                item.add(new Label("algorithmName", (String)item.getModelObject()));
            }
        });

        //calculations
        overview_page_content.add(new AjaxLink("createNewCalculation") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                var calc_id = calculationService.createCalculation();
                var pp = new PageParameters();
                pp.add("calc_id", calc_id);
                setResponsePage(DetailPage.class, pp);
            }
        });
        var calculationListContainer = new WebMarkupContainer("calculation_list_container");
        calculationListContainer.setOutputMarkupId(true);
        calculationListContainer.add(new ListView<>("calculationList", calculationsModel) {
            @Override
            protected void populateItem(ListItem item) {
                var calc = (CalculationStatus)item.getModelObject();
                item.add(new Label("calcId", calc.getId()));
                item.add(new Label("calcStatus", calc.getStatus().getStatus()));
                item.add(new Label("calcMsg", calc.getStatus().getMessage()));
                item.add(new AjaxLink("calcOpen") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        var pp = new PageParameters();
                        pp.add("calc_id", calc.getId());
                        setResponsePage(DetailPage.class, pp);
                    }
                });
                item.add(new AjaxLink("calcDelete") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (!calc.getStatus().getStatus().equals("Running")) {
                            var l = calculationsModel.getObject();
                            l.remove(calc);
                            calculationService.deleteCalculation(calc.getId());
                            calculationsModel.setObject(l);
                            target.add(calculationListContainer);
                        }
                    }
                });
            }
        });
        overview_page_content.add(calculationListContainer);

        // executions
        var execContainer = new WebMarkupContainer("execution_container");
        execContainer.setOutputMarkupId(true);
        execContainer.add(new ListView<>("executionList", executionModel) {
            @Override
            protected void populateItem(ListItem item) {
                var calc = ((ExecutionProgress)item.getModelObject());
                item.add(new Label("execId", calc.executionId));
                item.add(new Label("execStatus", calc.state.getStatus()));
                item.add(new Label("execMessage", calc.state.getMessage()));
                item.add(new AjaxLink("execCancel") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        var executions = executionModel.getObject();
                        executions.remove(calc);
                        calculationManagement.cancelExecution(calc.taskId, calc.executionId);
                        target.add(execContainer);
                    }
                });
            }
        });
        overview_page_content.add(new AjaxLink("createNewExecution") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                startDummyAlgoCommand.execute("someone");
            }
        });
        overview_page_content.add(new AjaxLink("createNewDummyLjFit") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                startDummyLjFitCommand.execute("someone");
            }
        });

        overview_page_content.add(execContainer);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        updateModels();
    }

    private void updateModels() {
        try {
            var info = calculationService.getServiceInfo();
            serviceVersion.setObject(info.getVersion().toString());
            serviceStatus.setObject(info.getServerStatus());
            algorithmsModel.setObject(calculationService.listAlgorithms().toJavaList());
            calculationsModel.setObject(calculationService.listCalculations().toJavaList());
            executionModel.setObject(calculationManagement.listExecutions().responses);
        } catch (Exception ex) {
            errorMessageModel.setObject(ex.getMessage());
            System.out.println("api communication failed");
        }

        errorMessageContainer.setVisible(!(errorMessageModel.getObject()==null || errorMessageModel.getObject().equals("") || errorMessageModel.getObject().equals("")));

    }

}

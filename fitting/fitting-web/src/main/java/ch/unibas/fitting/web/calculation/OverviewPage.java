package ch.unibas.fitting.web.calculation;

import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.commands.StartDummyAlgoCommand;
import ch.unibas.fitting.web.application.calculation.CalculationManagementClient;
import ch.unibas.fitting.web.application.calculation.execution.RunDetails;
import ch.unibas.fitting.web.web.HeaderPage;
import io.swagger.client.model.CalculationStatus;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;

public class OverviewPage extends HeaderPage {

    @Inject
    private CalculationService calculationService;
    @Inject
    private CalculationManagementClient calculationManagement;
    @Inject
    private StartDummyAlgoCommand startDummyAlgoCommand;

    private Model<String> serviceVersion;
    private Model<String> serviceStatus;
    private Model<String> errorMessageModel;
    private ListModel<String> algorithmsModel;
    private ListModel<CalculationStatus> calculationsModel;
    private ListModel<RunDetails> executionModel;
    private WebMarkupContainer errorMessageContainer;

    public OverviewPage() {
        serviceVersion = new Model<>();
        serviceStatus = new Model<>();
        algorithmsModel = new ListModel<>();
        calculationsModel = new ListModel<>();
        executionModel = new ListModel<>();
        errorMessageModel = new Model<>();

        errorMessageContainer = new WebMarkupContainer("errormessage_container");
        errorMessageContainer.add(new Label("errormessage", errorMessageModel));
        add(errorMessageContainer);

        //service status
        add(new Label("version", serviceVersion));
        add(new Label("service_status", serviceStatus));

        //algo list
        add(new ListView<>("algorithmList", algorithmsModel) {
            @Override
            protected void populateItem(ListItem item) {
                item.add(new Label("algorithmName", (String)item.getModelObject()));
            }
        });

        //calculations
        add(new AjaxLink("createNewCalculation") {
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
                        calculationService.deleteCalculation(calc.getId());

                        calculationsModel.getObject().remove(calc);
                        target.add(calculationListContainer);
                    }

                    @Override
                    public boolean isVisible() {
                        return !"Running".equalsIgnoreCase(calc.getStatus().getStatus());
                    }
                });
            }
        });
        add(calculationListContainer);

        // runs
        var execContainer = new WebMarkupContainer("execution_container");
        execContainer.setOutputMarkupId(true);
        execContainer.add(new ListView<>("executionList", executionModel) {
            @Override
            protected void populateItem(ListItem item) {
                var calc = ((RunDetails)item.getModelObject());
                item.add(new Label("execId", calc.executionId));
                item.add(new Label("execStatus", calc.status));
                item.add(new Label("execMessage", calc.message));
                item.add(new AjaxLink("execCancel") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        calculationManagement.cancelRun(calc.groupId, calc.executionId);

                        target.add(execContainer);
                    }
                });
            }
        });
        add(new AjaxLink("createNewExecution") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                startDummyAlgoCommand.execute(getCurrentUsername());
            }
        });

        add(execContainer);
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
            executionModel.setObject(calculationManagement.listExecutions().runDetails.toJavaList());
        } catch (Exception ex) {
            errorMessageModel.setObject(ex.getMessage());
            System.out.println("api communication failed");
        }

        errorMessageContainer.setVisible(!(errorMessageModel.getObject()==null || errorMessageModel.getObject().equals("") || errorMessageModel.getObject().equals("")));
    }

}

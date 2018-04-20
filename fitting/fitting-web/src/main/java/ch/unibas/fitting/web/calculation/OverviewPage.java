package ch.unibas.fitting.web.calculation;

import akka.actor.ActorRef;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.management.CalculationManagementClient;
import ch.unibas.fitting.web.calculation.management.messages.ExecutionProgress;
import ch.unibas.fitting.web.calculation.management.messages.ListExecutionsResponse;
import ch.unibas.fitting.web.calculation.management.messages.StartDefinition;
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
import scala.collection.concurrent.Debug;

import javax.inject.Inject;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class OverviewPage extends HeaderPage {

    @Inject
    private CalculationService calculationService;

    @Inject
    private CalculationManagementClient calculationManagement;

    private Model<String> serviceVersion;
    private Model<String> serviceStatus;
    private ListModel<String> algorithmsModel;
    private ListModel<CalculationStatus> calculationsModel;
    private ListModel<ExecutionProgress> executionModel;

    public OverviewPage() {
        serviceVersion = new Model<>();
        serviceStatus = new Model<>();
        algorithmsModel = new ListModel<>();
        calculationsModel = new ListModel<>();
        executionModel = new ListModel<>();

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
        add(calculationListContainer);

        // executions
        var execContainer = new WebMarkupContainer("execution_container");
        execContainer.setOutputMarkupId(true);
        execContainer.add(new ListView<>("executionList", executionModel) {
            @Override
            protected void populateItem(ListItem item) {
                var calc = ((ExecutionProgress)item.getModelObject());
                item.add(new Label("execId", calc.id));
                item.add(new Label("execStatus", calc.state.getStatus()));
                item.add(new Label("execMessage", calc.state.getMessage()));
                item.add(new AjaxLink("execCancel") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        var l = executionModel.getObject();
                        l.remove(calc);
                        executionModel.setObject(l);
                        target.add(execContainer);
                    }
                });
            }
        });
        add(new AjaxLink("createNewExecution") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                calculationManagement.Start(createDummyAlgorithmStartDefinition());
                target.add(execContainer);
            }
        });

        add(execContainer);
    }

    @Override
    protected void onInitialize() {

        try {
            super.onInitialize();
            var info = calculationService.getServiceInfo();
            serviceVersion.setObject(info.getVersion().toString());
            serviceStatus.setObject(info.getServerStatus());
            algorithmsModel.setObject(calculationService.listAlgorithms().toJavaList());
            calculationsModel.setObject(calculationService.listCalculations().toJavaList());
            executionModel.setObject(calculationManagement.ListExecutions().responses);
        }
        catch (Exception ex){
            Debug.log("api communication failed");
        }
    }

    private StartDefinition createDummyAlgorithmStartDefinition(){
        var algorithmName = "dummy_algorithm";
        Map<String, Object> params = new HashMap<>();
        var fileArray = new File[] {
                new File("C:\\Users\\eknecht\\Desktop\\somefile.json")
        };
        return new StartDefinition(algorithmName, params, fileArray);
    }
}

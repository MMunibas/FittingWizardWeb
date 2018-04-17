package ch.unibas.fitting.web.calculation;

import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.web.HeaderPage;
import io.swagger.client.model.CalculationStatus;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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

    private Model<String> serviceVersion;
    private Model<String> serviceStatus;
    private ListModel<String> algorithmsModel;
    private ListModel<CalculationStatus> calculationsModel;

    public OverviewPage() {

        serviceVersion = new Model<>();
        serviceStatus = new Model<>();
        algorithmsModel = new ListModel<>();
        calculationsModel = new ListModel<>();

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

        add(new ListView<>("calculationList", calculationsModel) {
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
            }
        });
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        var info = calculationService.getServiceInfo();
        serviceVersion.setObject(info.getVersion().toString());
        serviceStatus.setObject(info.getServerStatus());
        algorithmsModel.setObject(calculationService.listAlgorithms().toJavaList());
        calculationsModel.setObject(calculationService.listCalculations().toJavaList());
    }
}

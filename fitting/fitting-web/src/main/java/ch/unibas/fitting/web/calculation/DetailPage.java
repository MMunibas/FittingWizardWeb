package ch.unibas.fitting.web.calculation;

import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.web.HeaderPage;
import io.swagger.client.model.CalculationStatus;
import io.swagger.client.model.Status;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;

public class DetailPage extends HeaderPage {

    @Inject
    private CalculationService calculationService;

    private String calculationId;
    private Model<Status> calculation;
    private Model<String> calc_id;

    public DetailPage(PageParameters pp) {

        calc_id = new Model<>();
        calculation = new Model<>();

        calculationId = pp.get("calc_id").toString();

        //service status
        add(new Label("calc_id", calc_id));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        calc_id.setObject(calculationId);
        calculation.setObject(calculationService.getCalculationStatus(calculationId));
    }
}

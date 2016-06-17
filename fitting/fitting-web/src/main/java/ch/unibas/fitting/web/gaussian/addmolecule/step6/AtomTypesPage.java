package ch.unibas.fitting.web.gaussian.addmolecule.step6;

import ch.unibas.fitting.web.gaussian.addmolecule.step1.OverviewPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public class AtomTypesPage extends HeaderPage {

    private final UUID _taskId;

    public AtomTypesPage(PageParameters pp) {

        String id = pp.get("task_id").toString();
        if (id != null)
            _taskId = UUID.fromString(id);
        else
            _taskId = null;

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        Form form = new Form("form");
        add(form);

        form.add(new AjaxButton("next") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(OverviewPage.class);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);
            }
        });

        form.add(new DataView<ChargesViewModel>("charges", loadAtomTypes())
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<ChargesViewModel> item)
            {
                ChargesViewModel charge = item.getModelObject();

                item.add(new Label("name", charge.getName()));

                NumberTextField<Double> ntf = new NumberTextField<Double>("charge", new PropertyModel<>(charge, "userCharge"));
                item.add(ntf);
            }
        });
    }

    private IDataProvider<ChargesViewModel> loadAtomTypes() {
        return new ListDataProvider<ChargesViewModel>() {

            private List<ChargesViewModel> atomTypes;

            @Override
            protected List getData() {

                if (atomTypes == null) {
                    atomTypes = new ArrayList<>();
                    atomTypes.add(new ChargesViewModel("test", new int[]{0,1,2,3}));
                }

                return atomTypes;
            }
        };
    }
}

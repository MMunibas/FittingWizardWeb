package ch.unibas.fitting.web.gaussian.fit.step1;

import ch.unibas.fitting.web.gaussian.addmolecule.step6.ChargesViewModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class EnterChargesPanel extends Panel {

    private List<ChargesViewModel> atomTypes;

    public EnterChargesPanel(String id, ModalWindow window, List<ChargesViewModel> atomTypes) {
        super(id);
        this.atomTypes = atomTypes;
        Form form = new Form("form");
        add(form);

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        form.add(new ListView<ChargesViewModel>("atomTypes", EnterChargesPanel.this.atomTypes) {
            @Override
            protected void populateItem(ListItem<ChargesViewModel> item) {
                ChargesViewModel mol = item.getModelObject();

                item.add(new Label("type", mol.getName()));

                NumberTextField chargeField = new NumberTextField("charge", new PropertyModel<String>(mol, "charge"));
                chargeField.setStep(NumberTextField.ANY);
                chargeField.setRequired(true);
                item.add(chargeField);
            }
        });

        form.add(new AjaxButton("start") {
            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);
                window.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(fp);
            }
        });

        form.add(new AjaxLink("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                window.close(target);
            }
        });
    }
}

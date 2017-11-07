package ch.unibas.fitting.web.ljfit.ui.step1;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by tschmidt on 23.06.2016.
 */
public class ClusterParameterPanel extends Panel {

    public ClusterParameterPanel(String id, ModalWindow window, ClusterParameterViewModel clusterParameterViewModel) {
        super(id);

        Form form = new Form("form");
        add(form);

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        NumberTextField ncpuRhoDeltaHField = new NumberTextField<>("ncpusDeltaH", new PropertyModel(clusterParameterViewModel, "ncpusDeltaH"));
        ncpuRhoDeltaHField.setStep(1);
        ncpuRhoDeltaHField.setMinimum(1);
        ncpuRhoDeltaHField.setRequired(true);
        form.add(ncpuRhoDeltaHField);

        NumberTextField ncpuDeltaGField = new NumberTextField<>("ncpusDeltaG",  new PropertyModel(clusterParameterViewModel, "ncpusDeltaG"));
        ncpuDeltaGField.setStep(1);
        ncpuDeltaGField.setMinimum(1);
        ncpuDeltaGField.setRequired(true);
        form.add(ncpuDeltaGField);

        TextField clusterField = new TextField("clusterName", new PropertyModel(clusterParameterViewModel, "clusterName"));
        clusterField.setRequired(true);
        form.add(clusterField);

        form.add(new AjaxButton("ok") {
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

        form.add(new AjaxButton("cancel") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                window.close(target);
            }
        });


    }
}

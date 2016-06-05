package ch.unibas.fitting.web.gaussian;

import ch.unibas.fitting.web.web.HeaderPage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.progress.UploadProgressBar;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;


/**
 * Created by martin on 05.06.2016.
 */
public class UploadPage extends HeaderPage {

    private final FileUploadField file;

    public UploadPage() {

        final Component feedback = new FeedbackPanel("feedback").setOutputMarkupId(true);
        add(feedback);

        final Form form = new Form("form")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit()
            {
                FileUpload upload = file.getFileUpload();
                if (upload == null)
                {
                    Logger.debug("No file uploaded");
                }
                else
                {
                    Logger.debug("File-Name: " + upload.getClientFileName() + " File-Size: " +
                            Bytes.bytes(upload.getSize()).toString());
                }
            }
        };
        form.setMaxSize(Bytes.megabytes(2000));
        add(form);

        form.add(file = new FileUploadField("file"));

        form.add(new Label("max", new Model<>(form.getMaxSize().toString())));

        form.add(new org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar("progress", form, file));

        form.add(new AjaxButton("ajaxSubmit")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                Logger.debug("done");
                target.add(feedback);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form)
            {
                target.add(feedback);
            }

        });
    }
}

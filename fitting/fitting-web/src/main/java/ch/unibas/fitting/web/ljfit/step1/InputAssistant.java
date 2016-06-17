package ch.unibas.fitting.web.ljfit.step1;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.web.ljfit.step2.ShowOutput;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.lang.Bytes;

import javax.inject.Inject;
import java.io.File;

/**
 * Created by martin on 29.05.2016.
 */
public class InputAssistant extends HeaderPage {

    @Inject
    private IUserDirectory _userDir;

    private final FileUploadField parUploadFile;
    private final FileUploadField rtfUploadFile;

    public InputAssistant() {

        final Component feedback = new FeedbackPanel("feedback").setOutputMarkupId(true);
        add(feedback);

        final Form form = new Form("form")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit()
            {
                File parFile = UploadFile(parUploadFile.getFileUpload());
                File rtfFile = UploadFile(rtfUploadFile.getFileUpload());

                setResponsePage(new ShowOutput());
            }

            private File UploadFile(FileUpload upload) {
                File f = null;

                if (upload == null)
                {
                    Logger.debug("No file uploaded");
                }
                else
                {
                    Logger.debug("File-Name: " + upload.getClientFileName() + " File-Size: " +
                            Bytes.bytes(upload.getSize()).toString());

                    f = _userDir.getLjfitInputFileName(session().getUsername(), upload.getClientFileName());
                    try {
                        upload.writeTo(f);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return f;
            }
        };

        form.add(parUploadFile = new FileUploadField("parUploadFile"));
        form.add(rtfUploadFile = new FileUploadField("rtfUploadFile"));

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

        add(form);
    }
}

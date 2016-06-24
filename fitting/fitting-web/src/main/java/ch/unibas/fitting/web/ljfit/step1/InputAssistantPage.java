package ch.unibas.fitting.web.ljfit.step1;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;

import javax.inject.Inject;
import java.io.File;

/**
 * Created by martin on 29.05.2016.
 */
public class InputAssistantPage extends HeaderPage {

    @Inject
    private IUserDirectory _userDir;

    private final FileUploadField parUploadFile;
    private final FileUploadField rtfUploadFile;
    private final FileUploadField molUploadFile;
    private final FileUploadField liquidUploadFile;
    private final FileUploadField solventUploadFile;
    private final FileUploadField lpunUploadFile;
    private final IModel<Double> lambda = Model.of(0.1);

    private ExtraParametersPanel extraParametersPage;

    public InputAssistantPage() {

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        ModalWindow extraParametersDialog = new ModalWindow("modalWindow");
        ExtraParameterViewModel extraParameterViewModel = new ExtraParameterViewModel(1, 1, "beethoven");

        extraParametersPage = new ExtraParametersPanel(extraParametersDialog.getContentId(),
                extraParametersDialog, extraParameterViewModel);
        extraParametersDialog.setContent(extraParametersPage);

        extraParametersDialog.setCloseButtonCallback(target -> {
            return true;
        });

        extraParametersDialog.setWindowClosedCallback(target -> {
            Logger.debug("Extra Parameters " + extraParameterViewModel.getNcpusDeltaH() + " " +
                    extraParameterViewModel.getNcpusDeltaG() + " " +
                    extraParameterViewModel.getClusterName());
        });
        add(extraParametersDialog);

        final Form form = new Form("form");
        form.add(parUploadFile = createFileUploadField("parUploadFile"));
        form.add(rtfUploadFile = createFileUploadField("rtfUploadFile"));
        form.add(molUploadFile = createFileUploadField("molUploadFile"));
        form.add(liquidUploadFile = createFileUploadField("liquidUploadFile"));
        form.add(solventUploadFile = createFileUploadField("solventUploadFile"));
        form.add(lpunUploadFile = createFileUploadField("lpunUploadFile"));
        NumberTextField lambdaField = new NumberTextField("lambda", lambda);
        lambdaField.setRequired(true);
        lambdaField.setStep(NumberTextField.ANY);
        form.add(lambdaField);

        add(new AjaxLink("extraParameters") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Logger.debug("opening extra parameters dialog");
                extraParametersDialog.show(target);
            }
        });

        form.add(new AjaxButton("start")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                File parFile = UploadFile(parUploadFile.getFileUpload());
                File rtfFile = UploadFile(rtfUploadFile.getFileUpload());
                File molFile = UploadFile(molUploadFile.getFileUpload());
                File liquidFile = UploadFile(liquidUploadFile.getFileUpload());
                File solventFile = UploadFile(solventUploadFile.getFileUpload());
                File lpunFile = UploadFile(lpunUploadFile.getFileUpload());
                target.add(fp);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form)
            {
                super.onError(target, form);
                target.add(fp);
            }

        });

        add(form);
    }

    private FileUploadField createFileUploadField(String id) {
        FileUploadField upload = new FileUploadField(id);
        upload.setRequired(true);
        return upload;
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
}

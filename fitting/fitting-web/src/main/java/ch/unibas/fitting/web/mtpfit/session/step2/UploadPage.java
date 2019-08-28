package ch.unibas.fitting.web.mtpfit.session.step2;

import ch.unibas.fitting.application.directories.IUserDirectory;
import ch.unibas.fitting.application.calculation.CalculationService;
import ch.unibas.fitting.web.mtpfit.session.step3.AtomsPage;
import ch.unibas.fitting.web.misc.HeaderPage;
import ch.unibas.fitting.web.mtpfit.session.step2.UploadedMDCMFiles;
import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;

import javax.inject.Inject;
import java.io.File;


/**
 * Created by martin on 05.06.2016.
 */
public class UploadPage extends HeaderPage {

    @Inject
    private IUserDirectory _userDir;
    @Inject
    private CalculationService calculationService;

    private final FileUploadField file;
    private final FileUploadField axisUploadFile;

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
                    LOGGER.debug("No file uploaded");
                    return;
                }

                FileUpload aUpload = axisUploadFile.getFileUpload();
                if (aUpload == null)
                {
                    LOGGER.debug("No axis file uploaded");
                    return;
                }

                cleanupMtpSession();

                LOGGER.info("File-Name: " + upload.getClientFileName() + " File-Size: " +
                        Bytes.bytes(upload.getSize()).toString());

                LOGGER.info("Axis File-Name: " + aUpload.getClientFileName() + " File-Size: " +
                        Bytes.bytes(aUpload.getSize()).toString());

                File fitDestination = _userDir.getMtpFitDir(getCurrentUsername())
                        .getMoleculeDir()
                        .getSessionDir();

                File destination = _userDir.getMtpFitDir(getCurrentUsername())
                        .getMoleculeDir()
                        .getXyzFileFor(upload.getClientFileName());

                File axisFile = new File(fitDestination, aUpload.getClientFileName());
                try {
                    aUpload.writeTo(axisFile);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                UploadedMDCMFiles uploadedMDCMFiles = new UploadedMDCMFiles(axisFile);

                try {
                    upload.writeTo(destination);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                PageParameters pp = new PageParameters();
                String moleculeName = FilenameUtils.removeExtension(destination.getName());
                pp.add("molecule_name", moleculeName);
                pp.add("axis_file_name", axisFile.getName());

                setResponsePage(AtomsPage.class, pp);
            }
        };
        form.setMaxSize(Bytes.megabytes(2));
        add(form);
        form.add(file = new FileUploadField("file"));
        form.add(axisUploadFile = new FileUploadField("axisUploadFile"));
        form.add(new Label("max", new Model<>(form.getMaxSize().toString())));
        form.add(new AjaxButton("ajaxSubmit")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                LOGGER.debug("done");
                target.add(feedback);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedback);
            }
        });
    }

    private void cleanupMtpSession() {
        _userDir.getMtpFitDir(getCurrentUsername())
                .readCalculationId()
                .forEach(s -> calculationService.deleteCalculation(s));

        _userDir.deleteMtpFitDir(getCurrentUsername());
    }
}

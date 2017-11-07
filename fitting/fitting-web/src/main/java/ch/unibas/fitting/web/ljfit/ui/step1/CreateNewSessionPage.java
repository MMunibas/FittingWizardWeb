package ch.unibas.fitting.web.ljfit.ui.step1;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitSession;
import ch.unibas.fitting.shared.workflows.ljfit.SessionParameter;
import ch.unibas.fitting.shared.workflows.ljfit.UploadedFiles;
import ch.unibas.fitting.web.ljfit.services.LjFitRepository;
import ch.unibas.fitting.web.ljfit.ui.commands.OpenLjFitSessionCommand;
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
public class CreateNewSessionPage extends HeaderPage {

    @Inject
    private IUserDirectory _userDir;
    @Inject
    private OpenLjFitSessionCommand openLjFitSession;
    @Inject
    private LjFitRepository ljFitRepository;

    private final FileUploadField parUploadFile;
    private final FileUploadField rtfUploadFile;
    private final FileUploadField molUploadFile;
    private final FileUploadField liquidUploadFile;
    private final FileUploadField solventUploadFile;
    private final FileUploadField lpunUploadFile;

    private final IModel<Double> lambda = Model.of(0.1);

    private final IModel<Double> temperature = Model.of(0.0);
    private final IModel<Double> molarMass = Model.of(0.0);
    private final IModel<Double> numberOfResidues = Model.of(0.0);

    private final IModel<Double> expectedDensity = Model.of(0.0);
    private final IModel<Double> expectedDeltaH = Model.of(0.0);
    private final IModel<Double> expectedDeltaG = Model.of(0.0);

    public CreateNewSessionPage() {

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

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

        NumberTextField temperatureField = new NumberTextField("temperature", temperature);
        temperatureField.setRequired(true);
        temperatureField.setStep(NumberTextField.ANY);
        form.add(temperatureField);

        NumberTextField molarMassField = new NumberTextField("molarMass", molarMass);
        molarMassField.setRequired(true);
        molarMassField.setStep(NumberTextField.ANY);
        form.add(molarMassField);

        NumberTextField numberOfResiduesField = new NumberTextField("numberOfResidues", numberOfResidues);
        numberOfResiduesField.setRequired(true);
        numberOfResiduesField.setStep(NumberTextField.ANY);
        form.add(numberOfResiduesField);

        NumberTextField expectedDensityField = new NumberTextField("expectedDensity", expectedDensity);
        expectedDensityField.setRequired(true);
        expectedDensityField.setStep(NumberTextField.ANY);
        form.add(expectedDensityField);

        NumberTextField expectedDeltaHField = new NumberTextField("expectedDeltaH", expectedDeltaH);
        expectedDeltaHField.setRequired(true);
        expectedDeltaHField.setStep(NumberTextField.ANY);
        form.add(expectedDeltaHField);

        NumberTextField expectedDeltaGField = new NumberTextField("expectedDeltaG", expectedDeltaG);
        expectedDeltaGField.setRequired(true);
        expectedDeltaGField.setStep(NumberTextField.ANY);
        form.add(expectedDeltaGField);

        form.add(new AjaxButton("start") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                target.add(fp);

                _userDir.deleteLjFitSession(getCurrentUsername());
                File destionation = _userDir.getLjFitSessionDir(getCurrentUsername()).getUploadDir();
                UploadedFiles uploadedFiles = saveUploadedFiles(destionation);

                SessionParameter parameter = new SessionParameter(
                        lambda.getObject(),
                        temperature.getObject(),
                        molarMass.getObject(),
                        numberOfResidues.getObject(),
                        expectedDensity.getObject(),
                        expectedDeltaH.getObject(),
                        expectedDeltaG.getObject());

                LjFitSession session = new LjFitSession(
                        getCurrentUsername(),
                        parameter,
                        uploadedFiles);

                ljFitRepository.save(getCurrentUsername(), session);
                openLjFitSession.execute(getCurrentUsername());
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(fp);
            }
        });

        add(form);
    }

    private UploadedFiles saveUploadedFiles(File destination) {
        File parFile = uploadFile(destination, parUploadFile.getFileUpload());
        File rtfFile = uploadFile(destination, rtfUploadFile.getFileUpload());
        File molFile = uploadFile(destination, molUploadFile.getFileUpload());
        File liquidFile = uploadFile(destination, liquidUploadFile.getFileUpload());
        File solventFile = uploadFile(destination, solventUploadFile.getFileUpload());
        File lpunFile = uploadFile(destination, lpunUploadFile.getFileUpload());

        return new UploadedFiles(
                parFile,
                rtfFile,
                molFile,
                liquidFile,
                solventFile,
                lpunFile);
    }

    private File uploadFile(File destination, FileUpload upload) {
        LOGGER.debug("File-Name: " + upload.getClientFileName() + " File-Size: " +
                Bytes.bytes(upload.getSize()).toString());

        File f = new File(destination, upload.getClientFileName());
        try {
            upload.writeTo(f);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return f;
    }

    private FileUploadField createFileUploadField(String id) {
        FileUploadField upload = new FileUploadField(id);
        upload.setRequired(true);
        return upload;
    }
}

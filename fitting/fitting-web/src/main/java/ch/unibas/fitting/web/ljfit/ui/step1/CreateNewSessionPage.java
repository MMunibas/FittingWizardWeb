package ch.unibas.fitting.web.ljfit.ui.step1;

import ch.unibas.fitting.web.application.directories.IUserDirectory;
import ch.unibas.fitting.web.application.directories.LjFitSessionDir;
import ch.unibas.fitting.web.application.algorithms.ljfit.LjFitSession;
import ch.unibas.fitting.web.application.algorithms.ljfit.SessionParameter;
import ch.unibas.fitting.web.application.algorithms.ljfit.UploadedFileNames;
import ch.unibas.fitting.web.ljfit.services.LjFitRepository;
import ch.unibas.fitting.web.ljfit.ui.UiElementFactory;
import ch.unibas.fitting.web.ljfit.ui.commands.OpenLjFitSessionCommand;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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
    private final FileUploadField rtfOrTopUploadFile;
    private final FileUploadField molUploadFile;
    private final FileUploadField liquidUploadFile;
    private final FileUploadField solventUploadFile;
    private final FileUploadField lpunUploadFile;
    private final FileUploadField resUploadFile;

    private final IModel<Double> lambda_size_electrostatic = Model.of(0.1);
    private final IModel<Double> lambda_size_vdw = Model.of(0.1);

    private final IModel<Double> temperature = Model.of(298.0);
    private final IModel<Double> molarMass = Model.of();
    private final IModel<Integer> numberOfResidues = Model.of();

    private final IModel<Double> experimentalDensity = Model.of();
    private final IModel<Double> experimentalDeltaH = Model.of();
    private final IModel<Double> experimentalDeltaG = Model.of();

    public CreateNewSessionPage() {

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        final Form form = new Form("form");
        form.add(parUploadFile = createFileUploadField("parUploadFile"));
        form.add(rtfOrTopUploadFile = createFileUploadField("rtfOrTopUploadFile"));
        form.add(molUploadFile = createFileUploadField("molUploadFile"));
        form.add(liquidUploadFile = createFileUploadField("liquidUploadFile"));
        form.add(solventUploadFile = createFileUploadField("solventUploadFile"));
        form.add(lpunUploadFile = createFileUploadField("lpunUploadFile"));
        form.add(resUploadFile = createFileUploadField("resUploadFile"));

        form.add(UiElementFactory.createLambdaValueField("lambda_size_electrostatic", lambda_size_electrostatic));
        form.add(UiElementFactory.createLambdaValueField("lambda_size_vdw", lambda_size_vdw));

        NumberTextField<Double> temperatureField = new NumberTextField<>("temperature", temperature);
        temperatureField.setRequired(true);
        temperatureField.setType(Double.class);
        temperatureField.setStep(NumberTextField.ANY);
        form.add(temperatureField);

        NumberTextField<Double> molarMassField = new NumberTextField<>("molarMass", molarMass);
        molarMassField.setRequired(true);
        molarMassField.setStep(NumberTextField.ANY);
        molarMassField.setType(Double.class);
        molarMassField.setConvertEmptyInputStringToNull(true);
        form.add(molarMassField);

        NumberTextField<Integer> numberOfResiduesField = new NumberTextField<>("numberOfResidues", numberOfResidues);
        numberOfResiduesField.setRequired(true);
        numberOfResiduesField.setStep(1);
        numberOfResiduesField.setConvertEmptyInputStringToNull(true);
        numberOfResiduesField.setType(Integer.class);
        form.add(numberOfResiduesField);

        NumberTextField<Double> expectedDensityField = new NumberTextField<>("experimentalDensity", experimentalDensity);
        expectedDensityField.setRequired(true);
        expectedDensityField.setStep(NumberTextField.ANY);
        expectedDensityField.setConvertEmptyInputStringToNull(true);
        expectedDensityField.setType(Double.class);
        form.add(expectedDensityField);

        NumberTextField<Double> expectedDeltaHField = new NumberTextField<>("experimentalDeltaH", experimentalDeltaH);
        expectedDeltaHField.setRequired(true);
        expectedDeltaHField.setStep(NumberTextField.ANY);
        expectedDeltaHField.setConvertEmptyInputStringToNull(true);
        expectedDeltaHField.setType(Double.class);
        form.add(expectedDeltaHField);

        NumberTextField<Double> expectedDeltaGField = new NumberTextField<>("experimentalDeltaG", experimentalDeltaG);
        expectedDeltaGField.setRequired(true);
        expectedDeltaGField.setStep(NumberTextField.ANY);
        expectedDeltaGField.setConvertEmptyInputStringToNull(true);
        expectedDeltaGField.setType(Double.class);
        form.add(expectedDeltaGField);

        form.add(new AjaxButton("start") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);
                SessionParameter parameter = new SessionParameter(
                        lambda_size_electrostatic.getObject(),
                        lambda_size_vdw.getObject(),
                        temperature.getObject(),
                        molarMass.getObject(),
                        numberOfResidues.getObject(),
                        experimentalDensity.getObject(),
                        experimentalDeltaH.getObject(),
                        experimentalDeltaG.getObject());

                createNewSession(getCurrentUsername(), parameter);
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

    private void createNewSession(String currentUsername, SessionParameter parameter) {
        _userDir.deleteLjFitSession(currentUsername);
        LjFitSessionDir dir = _userDir.createLjFitSessionDir(currentUsername);
        UploadedFileNames uploadedFileNames = saveUploadedFiles(dir.getUploadDir());

        LjFitSession session = new LjFitSession(
                currentUsername,
                parameter,
                uploadedFileNames);

        ljFitRepository.save(currentUsername, session);
    }

    private UploadedFileNames saveUploadedFiles(File destination) {
        File parFile = uploadFile(destination, parUploadFile.getFileUpload());
        File rtfFile = uploadFile(destination, rtfOrTopUploadFile.getFileUpload());
        File molFile = uploadFile(destination, molUploadFile.getFileUpload());
        File liquidFile = uploadFile(destination, liquidUploadFile.getFileUpload());
        File solventFile = uploadFile(destination, solventUploadFile.getFileUpload());
        File lpunFile = uploadFile(destination, lpunUploadFile.getFileUpload());
        File resFile = uploadFile(destination, resUploadFile.getFileUpload());

        return new UploadedFileNames(
                parFile,
                rtfFile,
                molFile,
                liquidFile,
                solventFile,
                lpunFile,
                resFile);
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

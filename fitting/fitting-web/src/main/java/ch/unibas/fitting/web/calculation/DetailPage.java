package ch.unibas.fitting.web.calculation;

import ch.unibas.fitting.shared.javaextensions.Action;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.application.calculation.SerializedParameter;
import ch.unibas.fitting.web.web.HeaderPage;
import io.swagger.client.model.Status;
import io.vavr.Tuple2;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.time.Duration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DetailPage extends HeaderPage {

    @Inject
    private CalculationService calculationService;

    private String calculationId;
    private Model<Status> calculationStatus;
    private ListModel<SerializedParameter> calculationParameters;
    private ListModel<SerializedParameter> runParameters;
    private ListModel<String> algoListModel;
    private ListModel<String> supportedParameterTypesModel;
    private ListModel<String> inputFiles;
    private ListModel<String> outputFiles;
    private ListModel<Tuple2<String, Action>> actions;
    private Model<String> selectedRunParameterType;
    private Model<String> selectedCalcParameterType;
    private Model<SerializedParameter> newRunParameter;
    private Model<SerializedParameter> newCalcParameter;
    private Model<String> newRunParameterKey;
    private Model<String> newCalcParameterKey;
    private Model<String> newRunParameterValue;
    private Model<String> newCalcParameterValue;
    private Model<String> selectedAlgoModel;
    private Model<String> calc_id;

    FileUploadField fu = null;
    private List<String> supportedParameterTypes = new ArrayList<>();
    private TextField run_parameter_input_key;
    private TextField run_parameter_input_value;
    private TextField calculation_parameter_input_key;
    private TextField calculation_parameter_input_value;

    public DetailPage(PageParameters pp) {
        supportedParameterTypes.add(Boolean.class.getSimpleName());
        supportedParameterTypes.add(String.class.getSimpleName());
        supportedParameterTypes.add(Double.class.getSimpleName());

        calc_id = new Model<>();
        calculationStatus = new Model<>();
        selectedAlgoModel = new Model<>();
        selectedRunParameterType = new Model<>();
        selectedCalcParameterType = new Model<>();
        newRunParameter = new Model<>();
        newCalcParameter = new Model<>();
        newRunParameterKey = new Model<>();
        newCalcParameterKey = new Model<>();
        newRunParameterValue = new Model<>();
        newCalcParameterValue = new Model<>();
        supportedParameterTypesModel = new ListModel<>();
        calculationParameters = new ListModel<>();
        inputFiles = new ListModel<>();
        outputFiles = new ListModel<>();
        runParameters = new ListModel<>();
        algoListModel = new ListModel<>();
        actions = new ListModel<>();


        calculationId = pp.get("calc_id").toString();

        add(new Label("calc_id", calc_id));
        add(new Label("calculationStatus", new PropertyModel(calculationStatus, "getStatus")));

        add(new ListView<>("action_buttons", actions) {
            @Override
            protected void populateItem(ListItem item) {
                final Tuple2<String, Action> tuple = (Tuple2<String, Action>)item.getModelObject();
                var btn = new AjaxLink("button") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        tuple._2().execute();
                    }
                };
                btn.add(new Label("button_label", tuple._1));
                item.add(btn);
            }
        });

        /// Run section
        //  StartNewRun
        Form algorithmSelectionForm = new Form("algorithm_selection");
        add(algorithmSelectionForm);
        algorithmSelectionForm.add(new DropDownChoice<>("algo_selector", selectedAlgoModel, algoListModel));
        algorithmSelectionForm.add(new AjaxLink<>("start_algo_btn", selectedAlgoModel) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                var algo = getModelObject();
                var params = runParameters.getObject();
                calculationService.startRun(calculationId, algo, io.vavr.collection.List.ofAll(params));
                reloadPage();
            }
        });

        //  EditParameters
        var runParameterContainer = new WebMarkupContainer("run_parameter_container");
        runParameterContainer.setOutputMarkupId(true);
        runParameterContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
        runParameterContainer.add(new ListView<>("run_parameters", runParameters){
            @Override
            protected void populateItem(ListItem item) {
                var param = (SerializedParameter)item.getModelObject();

                item.add(new Label("run_param_type", param.type));
                item.add(new Label("run_param_key", param.key));
                item.add(new Label("run_param_value", param.value.toString()));
                item.add(new AjaxLink("run_param_delete") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        runParameters.setObject(runParameters.getObject().stream().filter(x->x.key!= param.key).collect(Collectors.toList()));
                    }
                });
            }

        });
        Form runParameterForm = new Form<>("run_parameter_form", newRunParameter){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                var currentParams = runParameters.getObject();
                var model = newRunParameter.getObject();
                switch (model.type) {
                    case "Boolean":
                        currentParams.add(new SerializedParameter(model.key, Boolean.parseBoolean(model.value.toString())));
                        break;
                    case "Double":
                        currentParams.add(new SerializedParameter(model.key, Double.parseDouble(model.value.toString())));
                        break;
                    default:
                        currentParams.add(new SerializedParameter(model.key, model.value));
                        break;
                }
                runParameters.setObject(currentParams);
            }
        };

        runParameterForm.add(new DropDownChoice<>("run_parameter_type_selector",
                new PropertyModel<>(newRunParameter, "type"), supportedParameterTypesModel));
        runParameterForm.add(new TextField<>("run_parameter_input_key", new PropertyModel<String>(newRunParameter, "key")));
        runParameterForm.add(new TextField<>("run_parameter_input_value", new PropertyModel<String>(newRunParameter, "value")));

        runParameterForm.add(runParameterContainer);
        add(runParameterForm);

        /// Calculation section
        var calculationParameterContainer = new WebMarkupContainer("calc_parameter_container");
        calculationParameterContainer.setOutputMarkupId(true);
        calculationParameterContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
        calculationParameterContainer.add(new ListView<>("calculation_parameters", calculationParameters){
            @Override
            protected void populateItem(ListItem item) {
                var param = (SerializedParameter)item.getModelObject();

                item.add(new Label("calc_param_type", param.type));
                item.add(new Label("calc_param_key", param.key));
                item.add(new Label("calc_param_value", param.value.toString()));
                item.add(new AjaxLink("calc_param_delete") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        calculationParameters.setObject(calculationParameters.getObject().stream().filter(x->x.key!= param.key).collect(Collectors.toList()));
                    }
                });
            }

        });
        Form calculationParameterForm = new Form<>("calculation_parameter_form", newCalcParameter){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                var currentParams = calculationParameters.getObject();
                var model = newCalcParameter.getObject();
                switch (model.type) {
                    case "Boolean":
                        currentParams.add(new SerializedParameter(model.key, Boolean.parseBoolean(model.value.toString())));
                        break;
                    case "Double":
                        currentParams.add(new SerializedParameter(model.key, Double.parseDouble(model.value.toString())));
                        break;
                    default:
                        currentParams.add(new SerializedParameter(model.key, model.value));
                        break;
                }
                calculationParameters.setObject(currentParams);
            }
        };
        add(calculationParameterForm);
        calculationParameterForm.add(new DropDownChoice<>("calculation_parameter_type_selector",
                new PropertyModel<>(newCalcParameter, "type"), supportedParameterTypesModel));
        calculationParameterForm.add(new TextField<>("calculation_parameter_input_key", new PropertyModel<String>(newCalcParameter, "key")));
        calculationParameterForm.add(new TextField<>("calculation_parameter_input_value", new PropertyModel<String>(newCalcParameter, "value")));

        calculationParameterForm.add(calculationParameterContainer);
        calculationParameterForm.add(new AjaxLink("calc_param_save") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                calculationService.setCalculationParameters(calculationId, io.vavr.collection.List.ofAll(calculationParameters.getObject()));
            }
        });

        /// Input section
        Form fileUploadForm = new Form("file_upload_form"){
            @Override
            protected void onSubmit() {
                try {
                    calculationService.uploadInputFile(calculationId, fu.getFileUpload());
                    reloadPage();
                } catch (Exception e) {
                    throw new RuntimeException("failed to upload file");
                }
            }
        };
        fileUploadForm.setMultiPart(true);
        fileUploadForm.add(fu = new FileUploadField("upload_input_file"));
        add(fileUploadForm);

        var inputFileContainer = new WebMarkupContainer("input_file_container");
        inputFileContainer.add(new ListView<>("input_file_path", inputFiles) {
            @Override
            protected void populateItem(ListItem item) {
                final String link = ((String)item.getModelObject()).replaceAll("\\\\","/");

                var fileToDownload = calculationService.downloadInputFiles(calculationId, link);
                var filenameparts = link.split("/");
                var filename = filenameparts[filenameparts.length-1];
                var lnk = new DownloadLink("download_input_file", fileToDownload, filename);
                lnk.add(new Label("download_input_file_link", link));
                item.add(lnk);
                item.add(new AjaxLink<>("delete_input_file") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        calculationService.deleteInputFiles(calculationId, link);

                        reloadPage();
                    }
                });
            }
        });
        add(inputFileContainer);

        /// Output section
        var outputFileContainer = new WebMarkupContainer("output_file_container");
        outputFileContainer.add(new ListView<>("output_file_path", outputFiles) {
            @Override
            protected void populateItem(ListItem item) {
                final String link = ((String)item.getModelObject()).replaceAll("\\\\","/");
                var fileToDownload = calculationService.downloadOutputFiles(calculationId, link);
                var filenameparts = link.split("/");
                var filename = filenameparts[filenameparts.length-1];
                var lnk = new DownloadLink("download_output_file", fileToDownload, filename);
                lnk.add(new Label("download_output_file_link", link));
                item.add(lnk);
                item.add(new AjaxLink<>("delete_output_file") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        calculationService.deleteOutputFiles(calculationId, link);
                        reloadPage();
                    }
                });
            }
        });
        add(outputFileContainer);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        supportedParameterTypesModel.setObject(supportedParameterTypes);
        selectedRunParameterType.setObject(supportedParameterTypes.get(2));
        selectedCalcParameterType.setObject(supportedParameterTypes.get(2));
        calc_id.setObject(calculationId);
        var algos = calculationService.listAlgorithms();
        var algo = calculationService.getAlgorithm(calculationId);
        if(algo == null) algo = algos.get(0);
        selectedAlgoModel.setObject(algo);
        algoListModel.setObject(algos.toJavaList());
        var status = calculationService.getCalculationStatus(calculationId);
        var actionList = new ArrayList<Tuple2<String, Action>>();
        if (status.getStatus().equals("Running")){
            actionList.add(new Tuple2<>("Cancel", ()->
            {
                calculationService.cancelCalculation(calculationId);
                setResponsePage(OverviewPage.class);
            }));
        } else {
            actionList.add(new Tuple2<>("Delete", ()->
            {
                calculationService.deleteCalculation(calculationId);
                setResponsePage(OverviewPage.class);
            }));
        }
        newRunParameter.setObject(new SerializedParameter());
        newCalcParameter.setObject(new SerializedParameter());
        newRunParameterKey.setObject("");
        newCalcParameterKey.setObject("");
        newRunParameterValue.setObject("");
        newCalcParameterValue.setObject("");

        actions.setObject(actionList);
        calculationStatus.setObject(status);

        calculationParameters.setObject(calculationService.getCalculationParameters(calculationId).toJavaList());
        runParameters.setObject(calculationService.getRunParameters(calculationId).toJavaList());

        inputFiles.setObject(calculationService.listInputFiles(calculationId).toJavaList());
        outputFiles.setObject(calculationService.listOutputFiles(calculationId).toJavaList());
        System.out.println("");
    }

    void reloadPage(){
        var pp = new PageParameters();
        pp.add("calc_id", calculationId);
        setResponsePage(DetailPage.class, pp);
    }
}

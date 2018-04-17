package ch.unibas.fitting.web.application.calculation;

import de.agilecoders.wicket.jquery.util.Json;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.CalculationApi;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.*;

import io.vavr.Value;
import io.vavr.collection.List;
import org.apache.wicket.util.file.File;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CalculationService {

    DefaultApi defaultApi;
    CalculationApi calculationApi;
    public CalculationService() {
        ApiClient client = new ApiClient();
        client.setBasePath("http://localhost:5000");
        defaultApi = new DefaultApi(client);
        calculationApi = new CalculationApi(client);
    }
    public List<String> listAlgorithms() {
        try {
            return List.ofAll(defaultApi.getAlgorithmList().getAlgorithms());
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }
    public ServiceInfo getServiceInfo(){
        try {
            return defaultApi.getVersionInfo();
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }
    public List<CalculationStatus> listCalculations(){
        try {
            return List.ofAll(calculationApi.getCalculationList().getCalculations());
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    public String createCalculation(){
        try {
            var calc = new Calculation();
            calc.setParameters("{}");
            return calculationApi.postCalculationList(calc).getCalculation();
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    public String createCalculation(List<SerializedParameter> parameters){
        try {
            var calc = new Calculation();
            calc.setParameters(toJsonString(parameters));
            return calculationApi.postCalculationList(calc).getCalculation();
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    public void startRun(String calc_id, String algorithm, List<SerializedParameter> parameters){
        try {
            Run run = new Run();
            run.setAlgorithm(algorithm);
            run.setParameters(toJsonString(parameters));
            calculationApi.postRunCalculationActionAsync(calc_id, run, new ApiCallback<>() {
                @Override public void onFailure(ApiException e, int statusCode, Map<String, java.util.List<String>> responseHeaders) { }
                @Override public void onSuccess(RunId result, int statusCode, Map<String, java.util.List<String>> responseHeaders) { }
                @Override public void onUploadProgress(long bytesWritten, long contentLength, boolean done) { }
                @Override public void onDownloadProgress(long bytesRead, long contentLength, boolean done) { }
            });
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    private HashMap<String, Object> getJsonParametersAsMap(String calc_id, Function<Status, String> mapping){
        try {
            var status = getCalculationStatus(calc_id);
            var rawParameters = mapping.apply(status);
            if (rawParameters != null)
                return Json.fromJson(rawParameters, HashMap.class);
            return null;
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    private List<SerializedParameter> toList(HashMap<String, Object> map){
        try {
            List<SerializedParameter> list = map.entrySet().stream()
                    .map(kvp->new SerializedParameter(kvp.getKey(),kvp.getValue()))
                    .collect(List.collector());
            return list;
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    private String toJsonString(List<SerializedParameter> list){
        try {
            HashMap<String, Object> map = new HashMap<>();
            for (SerializedParameter p : list) {
                switch (p.type) {
                    case "Boolean":
                        map.put(p.key, Boolean.parseBoolean(p.value.toString()));
                        break;
                    case "String":
                        map.put(p.key, p.value.toString());
                        break;
                    case "Double":
                        map.put(p.key, Double.parseDouble(p.value.toString()));
                        break;
                    default:
                        break;
                }
            }
            return Json.stringify(map);
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }
    public List<SerializedParameter> getCalculationParameters(String calc_id){
        try {
            var parameterMap = getJsonParametersAsMap(calc_id, status -> status.getCalculationParameters().getParameters());

            var list = toList(parameterMap);
            return list;
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    public void setCalculationParameters(String calc_id,List<SerializedParameter> parameters){
        try {
            var calc = new Calculation();
            calc.setParameters(toJsonString(parameters));
            calculationApi.postCalculationResource(calc_id, calc);
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    public List<SerializedParameter> getRunParameters(String calc_id){
        try {
            var parameterMap = getJsonParametersAsMap(calc_id, status -> {
                var run_params = status.getRunParameters();
                if (run_params != null)
                    return run_params.getParameters();
                return null;
            });
            if (parameterMap == null) return List.empty();

            var list = toList(parameterMap);
            return list;
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    public String getAlgorithm(String calc_id){
        try {
            var status = getCalculationStatus(calc_id);
            var run_params = status.getRunParameters();

            if(run_params == null) return null;

            var algo =  run_params.getAlgorithm();
            return algo;
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }
    public Status getCalculationStatus(String calculationId){
        try {
            return calculationApi.getCalculationResource(calculationId);
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    /// Input files
    public void uploadInputFile(String calculationId, FileUpload fileUpload) {
        try {
            var file = File.createTempFile("___","___");
            file = new File(file.getParentFile(), fileUpload.getClientFileName());
            System.out.println(fileUpload.getClientFileName());
            fileUpload.writeTo(file);
            calculationApi.postInputFileListResource(calculationId, file);
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }
    public List<String> listInputFiles(String calculationId) {
        try {
            return List.ofAll(calculationApi.getInputFileListResource(calculationId).getFiles());
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }
    public File downloadInputFiles(String calculationId, String relativePath) {
        try {
            return new File(calculationApi.getInputFileDownloadResource(calculationId, relativePath));
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }
    public void deleteInputFiles(String calculationId, String relativePath) {
        try {
            calculationApi.deleteInputFileDownloadResource(calculationId, relativePath);
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    /// Output files
    public List<String> listOutputFiles(String calculationId) {
        try {
            var file_list = calculationApi.getOutputFileListResource(calculationId);
            var files = file_list.getFiles();
            return List.ofAll(files);
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }
    public File downloadOutputFiles(String calculationId, String relativePath) {
        try {
            return new File(calculationApi.getOutputFileDownloadResource(calculationId, relativePath));
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }
    public void deleteOutputFiles(String calculationId, String relativePath) {
        try {
            calculationApi.deleteOutputFileDownloadResource(calculationId, relativePath);
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

    ///Actions
    public void cancelCalculation(String calculationId) {
        try {
            calculationApi.postCancelCalculationAction(calculationId);
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }

    }

    public void deleteCalculation(String calculationId) {
        try {
            calculationApi.deleteCalculationResource(calculationId);
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }

    }
}

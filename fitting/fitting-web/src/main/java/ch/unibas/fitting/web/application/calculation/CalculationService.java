package ch.unibas.fitting.web.application.calculation;

import io.swagger.client.ApiClient;
import io.swagger.client.api.CalculationApi;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.Calculation;
import io.swagger.client.model.CalculationStatus;
import io.swagger.client.model.ServiceInfo;
import io.swagger.client.model.Status;
import io.vavr.collection.List;

import java.util.Arrays;

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
    public Status getCalculationStatus(String calculationId){
        try {
            return calculationApi.getCalculationResource(calculationId);
        }
        catch (Exception ex) {
            throw new RuntimeException("failed call to api", ex);
        }
    }

}

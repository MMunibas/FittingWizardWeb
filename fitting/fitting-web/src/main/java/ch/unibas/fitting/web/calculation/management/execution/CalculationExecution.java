package ch.unibas.fitting.web.calculation.management.execution;

import akka.actor.AbstractActor;
import akka.actor.Props;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.management.execution.messages.*;
import io.swagger.client.model.Status;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.CompletableFuture.runAsync;

public class CalculationExecution extends AbstractActor {
    private static final long statusUpdatePollingIntervalInMs = 5000;
    private CalculationService service;
    private StartDefinition params;
    private Status status = null;
    private boolean isDownloadComplete = false;

    private final static String INITIALIZING = "Initializing";
    private final static String CREATED = "Created";
    private final static String FINISHED = "Finished";
    private final static String CANCELED = "Canceled";
    private final static String FAILED = "Failed";
    private final static String DOWNLOADING = "Downloading";

    public static Props props(StartDefinition params, CalculationService calculationService){
        return Props.create(CalculationExecution.class,() -> {
            var run = new CalculationExecution();
            run.params = params;
            run.service = calculationService;
            run.self().tell(INITIALIZING, run.self());
            return run;
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DeleteCalculation.class, this::handleDeleteCalculation)
                .match(String.class, this::handleSetUp)
                .match(UpdateStatus.class, this::handleStatusUpdate)
                .match(ListExecutions.class, this::handleListExecutions)
                .match(CancelExecution.class, this::handleCancelExecution)
                .match(DownloadResults.class, this::handleStartDownload)
                .build();
    }

    // region Message handlers

    private void handleSetUp(String status) {
        if(status.equals(INITIALIZING)){
            initialize();
        }
    }

    private void handleStatusUpdate(UpdateStatus msg) {
        if(params.calculationId != null) {
            status = service.getCalculationStatus(params.calculationId);
            if (terminated(status.getStatus())){
                self().tell(new DownloadResults(), self());
                status.setStatus(DOWNLOADING);
                return;
            }
        }
        context()
            .system()
            .scheduler()
            .scheduleOnce(Duration.create(msg.pollingIntervalInMs, TimeUnit.MILLISECONDS),
                self(),
                new UpdateStatus(statusUpdatePollingIntervalInMs),
                context().dispatcher(),
                self());
    }

    private void handleDeleteCalculation(DeleteCalculation msg) {
        service.deleteCalculation(params.calculationId);
        sender().tell(new DeleteCalculationResponse(), self());
    }

    private void handleStartDownload(DownloadResults msg) {
        runAsync(()-> service.listOutputFiles(params.calculationId)
                .forEach(relativePath->{
                    var tempFile = service.downloadOutputFiles(params.calculationId, relativePath);
                    var targetFile = new File(params.outputDir, params.calculationId+File.separator+relativePath);
                    if(!targetFile.getParentFile().exists())
                        targetFile.getParentFile().mkdirs();
                    try {
                        Files.copy(tempFile.toPath(),targetFile.toPath());

                    } catch (IOException e) {
                        throw new RuntimeException("Failed to download file",e);
                    }
                })).thenRun(()->status.setStatus(FINISHED));
    }
    private void handleListExecutions(ListExecutions msg) {
        if(params.calculationId != null)
            sender().tell(new ExecutionProgress(params.taskId, params.calculationId, status, self()), self());
        else
            sender().tell(new ExecutionProgress(null, null, null, self()), self());
    }
    private void handleCancelExecution(CancelExecution msg) {
        service.cancelCalculation(params.calculationId);
        sender().tell(new CancelExecutionResponse(), self());
    }

    // endregion

    // region Helper methods

    private void initialize() {
        try {
            supplyAsync(this::createCalculation)
                    .thenApply(this::uploadFiles)
                    .thenApply(this::startRun)
                    .thenAccept(this::startStatusUpdater);
        } catch (Exception e) {
            throw new RuntimeException("failed to create calculation");
        }
    }

    private String createCalculation(){
        var id =  service.createCalculation();
        params.calculationId = id;
        return id;
    }

    private String uploadFiles(String calcId){

        for(var file : params.inputFiles){
            service.uploadInputFile(calcId, file);
        }
        return calcId;
    }

    private String startRun(String calcId){
        service.startRun(calcId, params.algorithmName, params.parameters);
        return calcId;
    }

    private void startStatusUpdater(String calcId){
        self().tell(new UpdateStatus(statusUpdatePollingIntervalInMs), self());
    }

    private boolean terminated(String status){
        return status.equals(FINISHED) || status.equals(CANCELED) || status.equals(FAILED) || status.equals(DOWNLOADING);
    }

    //endregion
}

package ch.unibas.fitting.web.application.calculation.manager;

import ch.unibas.fitting.shared.javaextensions.Action;
import ch.unibas.fitting.shared.javaextensions.Action1;
import com.google.gson.JsonObject;
import io.vavr.control.Option;

import java.io.File;
import java.util.Map;

public class StartDefinition {
    public final String algorithmName;
    public final Map<String, Object> parameters;
    public final File[] inputFiles;
    public final File outputDir;
    public final Option<String> calculationId;
    public final Option<Action1<Option<JsonObject>>> successCallback;
    public final boolean doNotDeleteCalculation;

    public StartDefinition(String algorithmName,
                           Map<String, Object> parameters,
                           File outputDir,
                           File... inputFiles){
        this(algorithmName, parameters, outputDir, inputFiles, Option.none(), Option.none(), false);
    }

    public StartDefinition(String algorithmName,
                           Map<String, Object> parameters,
                           File outputDir,
                           File[] inputFiles,
                           Option<String> calculationId,
                           Option<Action1<Option<JsonObject>>> successCallback,
                           boolean doNotDeleteCalculation){
        this.doNotDeleteCalculation = doNotDeleteCalculation;
        if (!outputDir.isDirectory())
            throw new IllegalArgumentException("outputDir does not exist");
        for (var f : inputFiles)
            if (!f.isFile())
                throw new IllegalArgumentException(String.format("inputFile %s does not exist", f));

        this.algorithmName = algorithmName;
        this.parameters = parameters;
        this.calculationId = calculationId;
        this.inputFiles = inputFiles;
        this.outputDir = outputDir;
        this.successCallback = successCallback;
    }
}

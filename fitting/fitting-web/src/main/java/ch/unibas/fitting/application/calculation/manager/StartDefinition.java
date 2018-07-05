package ch.unibas.fitting.application.calculation.manager;

import ch.unibas.fitting.infrastructure.javaextensions.Action1;
import com.google.gson.JsonObject;
import io.vavr.control.Option;

import java.io.File;
import java.util.Map;

/**
 * Defines all parameters needed to perform a {@link ch.unibas.fitting.application.calculation.execution.CalculationRun}.
 * A {@link ch.unibas.fitting.application.calculation.execution.CalculationRun} corresponds to a run within a
 * calculation on the calculation service.
 */
public class StartDefinition {
    /**
     * Type of algorithm which will be started.
     */
    public final String algorithmType;
    /**
     * Parameters which will be passed to the context of the calculation run.
     */
    public final Map<String, Object> parameters;
    /**
     * Files which will be uploaded to the calculation service as input files.
     */
    public final File[] inputFiles;
    /**
     * Directory where all output files will be downloaded. The download will also happen, if the calculation fails.
     */
    public final File outputDir;
    /**
     * A callback which will be called on a background thread, when the calculation run finished successfully.
     * An optional parameter containing the result JSON is passed, if the calculation run produced a result.
     * This may be used to process files in the output directory.
     */
    public final Option<Action1<Option<JsonObject>>> successCallback;
    /**
     * Keep calculation on the calculation service alive.
     * Per default calculations will be deleted when the user navigates away from the {@link ch.unibas.fitting.web.misc.ProgressPage}
     */
    public final boolean doNotDeleteCalculation;
    /**
     * An optional calculation ID to use. If none is given, then a calculation will be created.
     * This is used in the MtpFit to run fit on previously generated Mtp files.
     */
    public final Option<String> calculationId;

    public StartDefinition(String algorithmType,
                           Map<String, Object> parameters,
                           File outputDir,
                           File... inputFiles){
        this(algorithmType, parameters, outputDir, inputFiles, Option.none(), Option.none(), false);
    }

    public StartDefinition(String algorithmType,
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

        this.algorithmType = algorithmType;
        this.parameters = parameters;
        this.calculationId = calculationId;
        this.inputFiles = inputFiles;
        this.outputDir = outputDir;
        this.successCallback = successCallback;
    }
}

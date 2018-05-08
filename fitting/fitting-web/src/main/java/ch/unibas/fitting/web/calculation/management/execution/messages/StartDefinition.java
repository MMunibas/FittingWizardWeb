package ch.unibas.fitting.web.calculation.management.execution.messages;

import java.io.File;
import java.util.Map;

public class StartDefinition {
    public final String algorithmName;
    public final Map<String, Object> parameters;
    public final File[] inputFiles;
    public final File outputDir;

    public StartDefinition(String algorithmName,
                           Map<String, Object> parameters,
                           File outputDir,
                           File... inputFiles){

        this.algorithmName = algorithmName;
        this.parameters = parameters;
        this.inputFiles = inputFiles;
        this.outputDir = outputDir;
    }
}

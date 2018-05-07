package ch.unibas.fitting.web.calculation.management.execution.messages;

import java.io.File;
import java.util.Map;

public class StartDefinition {
    public final String algorithmName;
    public final Map<String, Object> parameters;
    public final File[] inputFiles;
    public String title;
    public String calculationId;
    public File outputDir;
    public String taskId = null;

    public StartDefinition(String algorithmName,
                           Map<String, Object> parameters,
                           String title,
                           File outputDir,
                           File... inputFiles){

        this.algorithmName = algorithmName;
        this.parameters = parameters;
        this.inputFiles = inputFiles;
        this.title = title;
        this.outputDir = outputDir;
    }
}

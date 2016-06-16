/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.base;

import ch.unibas.fitting.shared.config.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class PythonScriptRunner {

    private final static Logger logger = Logger.getLogger(PythonScriptRunner.class);

    private final static String ExecutableName = "python";
    private ProcessBuilder pb = new ProcessBuilder();
    
    public PythonScriptRunner(){
        Settings settings = Settings.loadConfig();

        Map<String, String> env = new HashMap<>();
        env.put("PYTHONPATH", settings.getPythonPath().toString());
        env.put("LD_LIBRARY_PATH", settings.getLDLibraryPath().toString());
        this.putEnvironment(env);
    }
    
//    public PythonScriptRunner(Map<String, String> env){
//        this.putEnvironment(env);
//    }

    public void setWorkingDir(File path) {
        logger.debug("Setting workdir: " + path);
        if (!path.exists()) {
            throw new RuntimeException("Working directory does not exist");
        }
        pb.directory(path);
    }

    public File getWorkingDir() {
        return pb.directory();
    }

    public void putEnvironment(Map<String, String> environment) {
        logger.debug("Setting environment: " + environment);
        Map<String, String> env = pb.environment();
        env.putAll(environment);
    }

    public Map<String, String> getEnvironment() {
        return pb.environment();
    }

    public static boolean isAvailable() {
        try {
            PythonScriptRunner runner = new PythonScriptRunner();
            int retval = runner.exec("--version");
            if (retval == 0) {
                logger.info("Python found in path.");
                return true;
            } else {
                logger.warn("Python not in path.");
                return false;
            }
        } catch (Exception e) {
            logger.warn("Python not in path.", e);
            return false;
        }
    }

    public int exec(File scriptName) {
        return exec(scriptName, new ArrayList<String>());
    }

    public int exec(File scriptName, List<String> args) {
        return exec(scriptName, args, null);
    }

    public int exec(String command) {
        return exec(null, Arrays.asList(command), null);
    }

    public int exec(File scriptName, List<String> args, File outputFile) {
        setCommand(scriptName, args, outputFile);
        return run(scriptName);
    }

    private int run(File scriptName) {
        logger.info("Running python script\n" + pb.command()
                + "\nin directory:\n" + pb.directory()
                + "\nwith environment:\n" + pb.environment());

        int exitCode = 0;
        try {
            Process p = pb.start();
            exitCode = p.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Python script [%s] failed.", scriptName.getName()), e);
        }
        logger.info("Python return value: " + exitCode);
        if (exitCode != 0) {
            throw new ScriptExecutionException(
                    String.format("Python script [%s] did not exit correctly. Exit code: %s",
                            scriptName.getName(),
                            String.valueOf(exitCode)));
        }

        return exitCode;
    }

    private void setCommand(File scriptName, List<String> args, File outputFile) {
        if (scriptName != null && !scriptName.exists()) {
            throw new ScriptExecutionException("Could not find Python script " + scriptName);
        }

        ArrayList<String> list = new ArrayList<>();
        list.add(ExecutableName);
        if (scriptName != null) {
            list.add(FilenameUtils.normalize(scriptName.getAbsolutePath()));
        }
        if (!args.isEmpty()) {
            list.addAll(args);
        }

        pb.command(list);
        if (logger.isDebugEnabled()) {
            logger.debug("ProcessBuilder.command = " + StringUtils.join(pb.command(), " "));
        }

        if (outputFile != null) {
            logger.debug("redirectOutput an redirectError set to " + FilenameUtils.normalize(outputFile.getAbsolutePath()));
            pb.redirectOutput(outputFile);
            pb.redirectError(outputFile);
        } else {
            logger.debug("redirectOutput set to inheritIO");
            pb.inheritIO();
        }
    }

}

/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.infrastructure.base;

import ch.unibas.fittingwizard.application.scripts.base.ScriptExecutionException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class VmdRunner {

	private final static Logger logger = Logger.getLogger(VmdRunner.class);

    private final static String ExecutableName = "vmd";
	private ProcessBuilder pb = new ProcessBuilder();

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
		File vmdScript = null;
		try {
			// create temporary file to exit vmd
			vmdScript = File.createTempFile("vmd", ".vmd");
			FileUtils.write(vmdScript, "exit");
			
            VmdRunner runner = new VmdRunner();
            int retval = runner.exec(Arrays.asList("-dispdev", "none", "-e", vmdScript.getAbsolutePath()));
			if (retval == 0) {
				logger.info("Vmd found in path.");
				return true;
			} else {
				logger.warn("Vmd not found in path.");
				return false;
			}
		} catch (Exception e) {
            logger.warn("Vmd not found in path.", e);
            return false;
        } finally {
        	if (vmdScript!=null) {
        		vmdScript.delete();
        	}
        }
    }
	
    public int exec(String command) {
        return exec(Arrays.asList(command), null);
    }
    
    public int exec(List<String> args) {
    	return exec(args, null);
    }
	
    public int exec(List<String> args, File outputFile)  {
        setCommand(args, outputFile);
        return run();
    }

	private int run() {
		logger.info("Running vmd:\n" + pb.command() +
				    "\nin directory:\n" + pb.directory() +
				    "\nwith environment:\n" + pb.environment());

        int exitCode = 0;
        try {
            Process p = pb.start();
            exitCode = p.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Vmd [%s] failed.", e));
        }
        logger.info("Vmd return value: " + exitCode);
        if (exitCode != 0) {
            throw new ScriptExecutionException(
                    String.format("Vmd did not exit correctly. Exit code: %s",
                            String.valueOf(exitCode)));
        }

		return exitCode;
	}
	
	private void setCommand(List<String> args, File outputFile) {

        ArrayList<String> list = new ArrayList<>();
        list.add(ExecutableName);

        if (! args.isEmpty()) {
            list.addAll(args);
        }

        pb.command(list);
        if (logger.isDebugEnabled()) {
            logger.debug("ProcessBuilder.command = " + StringUtils.join(pb.command(), " "));
        }

        if (outputFile != null) {
            logger.debug("redirectOutput set to " + FilenameUtils.normalize(outputFile.getAbsolutePath()));
            pb.redirectOutput(outputFile);
        } else {
            logger.debug("redirectOutput set to inheritIO");
            pb.inheritIO();
        }
	}


}

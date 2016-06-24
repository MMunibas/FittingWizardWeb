/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.fieldcomp;

import ch.unibas.fitting.shared.fieldcomp.exceptions.FieldcompParamsException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class FieldcompRunner {

	private final static Logger logger = Logger.getLogger(FieldcompRunner.class);

    private final static String ExecutableName = "fieldcomp";
	private ProcessBuilder pb = new ProcessBuilder();

    private final File scriptsPath;

    private Fieldcomp fd = null;

	public FieldcompRunner(File scriptsPath) {
		this.scriptsPath = scriptsPath;
	}
	
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
	
//	public static boolean isAvailable(File scriptsPath) {
//		try {
//            FieldcompRunner runner = new FieldcompRunner(scriptsPath);
//            int retval = runner.exec();
//			if (retval == 0) {
//				logger.info("Fieldcomp found in scripts directory.");
//				return true;
//			} else {
//				logger.warn("Fieldcomp not in scripts directory.");
//				return false;
//			}
//		} catch (Exception e) {
//            logger.warn("Fieldcomp not in scripts directory.", e);
//            return false;
//        }
//    }
	
	public int exec() {
		return exec(new ArrayList(), null);
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
    
//    private int run() {
//		logger.info("Running fieldcomp:\n" + pb.command() +
//				    "\nin directory:\n" + pb.directory() +
//				    "\nwith environment:\n" + pb.environment());
//        int exitCode = 0;
//        try {
//            Process p = pb.start();
//            exitCode = p.waitFor();
//        } catch (Exception e) {
//            throw new RuntimeException(String.format("Fieldcomp [%s] failed.", e));
//        }
//        logger.info("Fieldcomp return value: " + exitCode);
//        if (exitCode != 0) {
//            throw new ScriptExecutionException(
//                    String.format("Fieldcomp did not exit correctly. Exit code: %s",
//                            String.valueOf(exitCode)));
//        }
//
//		return exitCode;
//    }
     

    private int run() {
        logger.info("Running fieldcomp:\n" + pb.command()
                + "\nin directory:\n" + pb.directory()
                + "\nwith environment:\n" + pb.environment());
        int exitCode = 0;
        exitCode = fd.run(0);
        fd = null;

        return exitCode;
    }
	
//	private void setCommand(List<String> args, File outputFile) {
//
//        ArrayList<String> list = new ArrayList<>();
//        File executable = new File(this.scriptsPath, ExecutableName);
//        list.add(executable.getAbsolutePath());
//
//        if (! args.isEmpty()) {
//            list.addAll(args);
//        }
//
//        pb.command(list);
//        if (logger.isDebugEnabled()) {
//            logger.debug("ProcessBuilder.command = " + StringUtils.join(pb.command(), " "));
//        }
//
//        if (outputFile != null) {
//            logger.debug("redirectOutput set to " + FilenameUtils.normalize(outputFile.getAbsolutePath()));
//            pb.redirectOutput(outputFile);
//        } else {
//            logger.debug("redirectOutput set to inheritIO");
//            pb.inheritIO();
//        }
//	}
    private void setCommand(List<String> args, File outputFile) {

        try {
            String st[] = null;
            st = args.toArray(new String[0]);
            fd = new Fieldcomp(st);
        } catch (FieldcompParamsException ex) {
            logger.warn("Please solve the error previously reported.");
        }

    }


}//end of class

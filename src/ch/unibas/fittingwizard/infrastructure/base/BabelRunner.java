/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.infrastructure.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class BabelRunner {

	private final static Logger logger = Logger.getLogger(BabelRunner.class);

	private final static String ExecutableName = "babel";
	private ProcessBuilder pb;

	public BabelRunner() {
        pb = new ProcessBuilder();
        pb.redirectErrorStream(true);
	}
	
	public void setWorkingDir(File path) {
		logger.debug("Setting babel workdir: " + path);
		pb.directory(path);
	}
	
//	public static boolean isAvailable() {
//        try {
//            BabelRunner runner = new BabelRunner();
//            int retval = runner.exec();
//            if (retval == 0) {
//                logger.info("Babel found in path.");
//                return true;
//            } else {
//                logger.warn("Babel not in path.");
//                return false;
//            }
//        } catch (Exception e) {
//            logger.warn("Babel not in path.", e);
//            return false;
//        }
//	}
	
	public int exec() throws Exception {
		setCommand(new ArrayList<String>());
		return run();
	}
	
	public int exec(List<String> args) throws Exception {
		setCommand(args);
		return run();
	}
	
	private int run() throws Exception {
		logger.info("Running babel:\n" + pb.command() +
				    "\nin directory:\n" + pb.directory() +
				    "\nwith environment:\n" + pb.environment());
		Process p = pb.start();
		BufferedReader stdout = new BufferedReader (
				new InputStreamReader(p.getInputStream()));
		
		int retval = p.waitFor();
		logger.debug("Babel return value: " + retval);
		String s;
		logger.info("Babel output:\n");
		while ((s = stdout.readLine()) != null) {
			logger.info(s);
		}
		return retval;
	}
	
	private void setCommand(List<String> args) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(ExecutableName);
		if (! args.isEmpty()) {
			list.addAll(args);
		}
		pb.command(list);
	}
	
}

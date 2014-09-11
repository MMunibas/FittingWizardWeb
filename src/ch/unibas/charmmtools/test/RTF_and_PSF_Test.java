/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.test;

import ch.unibas.charmmtools.files.RTF;
import ch.unibas.charmmtools.files.RTF_generate;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import ch.unibas.fittingwizard.application.xyz.XyzFileParser;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author hedin
 */
public class RTF_and_PSF_Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            // initialise logger
            BasicConfigurator.configure();

            String xyzName = args[0];
            String csvName = args[1];

            XyzFile xyzf = XyzFileParser.parse(new File(xyzName));

            RTF rtff = new RTF_generate(xyzf, csvName);
        } catch (IOException ex) {
            Logger.getLogger(RTF_generate.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.babelBinding;

import org.apache.log4j.Logger;
import org.openbabel.OBConversion;
import org.openbabel.OBMol;

/**
 *
 * @author hedin
 */


public class BabelConverterAPI {

    OBConversion conv;
    OBMol mol;
    
    private final static Logger logger = Logger.getLogger(BabelConverterAPI.class);


    public BabelConverterAPI(String typeIN, String typeOUT) {
        
        String jpath = System.getProperty("java.library.path");
        logger.info("java.library.path is '" + jpath + "'");
        
        System.loadLibrary("openbabel_java");

        conv = new OBConversion();
        mol = new OBMol();

        conv.SetInFormat(typeIN);
        conv.SetOutFormat(typeOUT);
    }

    public void convert(String inFile, String outFile) {
        logger.info("Converting " + inFile + " to " + outFile);
        conv.ReadFile(mol, inFile);
        conv.WriteFile(mol, outFile);
    }

}// class

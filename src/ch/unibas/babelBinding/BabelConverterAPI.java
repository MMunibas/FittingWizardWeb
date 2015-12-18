/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.babelBinding;

import java.io.File;
import org.apache.log4j.Logger;
import org.openbabel.OBConversion;
import org.openbabel.OBMol;

public class BabelConverterAPI {

    OBConversion conv = null;
    OBMol mol = null;
    
    private final static Logger logger = Logger.getLogger(BabelConverterAPI.class);

    public BabelConverterAPI(String typeIN, String typeOUT) {
        
        String jpath = System.getProperty("java.library.path");
        logger.info("java.library.path is '" + jpath + "'");
        
        System.loadLibrary("openbabel_java");

        conv = new OBConversion();
        mol = new OBMol();
        
        conv.SetInAndOutFormats(typeIN, typeOUT);
        conv.AddOption("h",OBConversion.Option_type.GENOPTIONS);
        conv.AddOption("gen3D",OBConversion.Option_type.GENOPTIONS);

    }
    
    public void convert(String inFile, String outFile){
        conv.OpenInAndOutFiles(inFile, outFile);
        conv.Convert();
    }
    
    public void convert(File inFile, File outFile){
        conv.ReadFile(mol, inFile.getAbsolutePath());
        conv.WriteFile(mol, outFile.getAbsolutePath());
    }

}// class

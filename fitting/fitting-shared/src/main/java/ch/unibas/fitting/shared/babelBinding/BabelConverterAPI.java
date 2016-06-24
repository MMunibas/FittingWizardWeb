/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.babelBinding;

import java.io.File;
import org.apache.log4j.Logger;
import org.openbabel.OBConversion;
import org.openbabel.OBMol;

/**
 * This class performs molecular files conversion
 * using the OpenBabel java binding
 * 
 * @author hedin
 */
public class BabelConverterAPI {

    /**
     * Two objects used internally by OpenBabel for conversion
     */
    OBConversion conv = null;
    OBMol mol = null;
    
    private final static Logger logger = Logger.getLogger(BabelConverterAPI.class);

    /**
     * Constructor where input and output types are given at initialisation.
     * Will automatically add hydrogens and generate 3D coordinates if required
     * 
     * @param typeIN the input file type
     * @param typeOUT the output file type
     */
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
    
    /**
     * Performs Obabel conversion using path to files
     * 
     * @param inFile String with path to input file
     * @param outFile String with path to output file
     */
    public void convert(String inFile, String outFile){
        conv.OpenInAndOutFiles(inFile, outFile);
        conv.Convert();
    }
    
    /**
     * Performs Obabel conversion using File objects directly
     * 
     * @param inFile Input file
     * @param outFile Output file
     */
    public void convert(File inFile, File outFile){
        conv.ReadFile(mol, inFile.getAbsolutePath());
        conv.WriteFile(mol, outFile.getAbsolutePath());
    }

}// class

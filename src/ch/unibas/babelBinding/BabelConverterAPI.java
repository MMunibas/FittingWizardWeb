/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.babelBinding;

import org.openbabel.OBConversion;
import org.openbabel.OBMol;

/**
 *
 * @author hedin
 */


class BabelConverterAPI {

    OBConversion conv;
    OBMol mol;

    public BabelConverterAPI(String typeIN, String typeOUT) {
        System.loadLibrary("openbabel_java");

        conv = new OBConversion();
        mol = new OBMol();

        conv.SetInFormat(typeIN);
        conv.SetOutFormat(typeOUT);
    }

    public void convert(String inFile, String outFile) {
        conv.ReadFile(mol, inFile);
        conv.WriteFile(mol, outFile);
    }

}// class

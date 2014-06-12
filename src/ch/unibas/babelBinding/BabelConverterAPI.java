/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.unibas.babelBinding;

import org.openbabel.OBConversion;
import org.openbabel.OBMol;

/**
 *
 * @author hedin
 */


public class BabelConverterAPI {

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

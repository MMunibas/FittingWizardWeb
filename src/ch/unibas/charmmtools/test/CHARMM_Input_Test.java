/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.test;

import ch.unibas.charmmtools.files.input.CHARMM_input;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hedin
 */
public class CHARMM_Input_Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here

            CHARMM_input input = new CHARMM_input("test_coordinates.xyz", "test_topol.rtf", "test_params.par");
        } catch (IOException ex) {
            Logger.getLogger(CHARMM_Input_Test.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

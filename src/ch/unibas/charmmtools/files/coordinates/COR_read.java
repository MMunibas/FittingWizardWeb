/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.coordinates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author hedin
 */
public class COR_read extends COR {

    protected Scanner s = null;
        
    public COR_read(String filename) {
        try {
            // open the cor text file (buffered to have efficient IO even if large file)
            s = new Scanner(new FileInputStream(new File(filename)));
        } catch (FileNotFoundException ex) {
            logger.error("Error while opening cor file : " + ex);
        }
        this.readFile();
        s.close();
        s = null;
    }

    protected void readFile() {

        // skip comment lines
        String inp = s.nextLine();
        while (inp.contains("*")) {
            inp = s.nextLine();
        }

        // if the line containing natom also contains EXT it means the file is extended format
        if (inp.contains("EXT")) {
            isExtendedFormat = true;
            logger.info("Extended format COR file detected");
        }

        // get natom from cor
        String[] tokens = inp.trim().split(delims);

//        for (int i = 0; i < tokens.length; i++) {
//            tokens[i] = tokens[i].trim();
//        }
//        for (int i = 0; i < tokens.length; i++) {
//            System.out.println(i);
//            System.out.println(tokens[i]);
//        }
//        System.exit(0);
        natom = Integer.parseInt(tokens[0]);

        allocate();

        for (int i = 0; i < natom; i++) {
            inp = s.nextLine();
            tokens = inp.trim().split(delims);
            int idx = 0;
            atomID[i] = Integer.parseInt(tokens[idx++]);
            resID[i] = Integer.parseInt(tokens[idx++]);
            resName[i] = tokens[idx++];
            atomName[i] = tokens[idx++];
            x[i] = Float.parseFloat(tokens[idx++]);
            y[i] = Float.parseFloat(tokens[idx++]);
            z[i] = Float.parseFloat(tokens[idx++]);
            segName[i] = tokens[idx++];
            segID[i] = Integer.parseInt(tokens[idx++]);
            weight[i] = Float.parseFloat(tokens[idx++]);
        }

    }

}

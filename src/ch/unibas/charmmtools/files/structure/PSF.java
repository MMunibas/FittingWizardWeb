/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files.structure;

import ch.unibas.charmmtools.files.topology.RTF;
import ch.unibas.charmmtools.files.topology.RTF_generate;
import ch.unibas.charmmtools.internals.Atom;
import ch.unibas.charmmtools.internals.Angle;
import ch.unibas.charmmtools.internals.Bond;
import ch.unibas.charmmtools.internals.Dihedral;
import ch.unibas.charmmtools.internals.Improper;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import ch.unibas.fittingwizard.application.xyz.XyzFileParser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 * This abstract class contains attributes and setters/getters useful for reading or generating a CHARMM PSF file.
 *
 * @author hedin
 */
public abstract class PSF {

    protected String myname;

    protected boolean isExtendedFormat = false;
    protected boolean isUsingCMAP = false;
    protected boolean isUsingCHEQ = false;
    protected boolean isUsingDRUDE = false;

    protected int ntitle;
    protected int natom;
    protected int nbond;
    protected int ntheta;
    protected int nphi;
    protected int nimphi;

    // the lists read from PSF
    protected List<Atom> atomList = null;
    protected List<Bond> bondList = null;
    protected List<Angle> angleList = null;
    protected List<Dihedral> diheList = null;
    protected List<Improper> imprList = null;
    
        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            // initialise logger
            BasicConfigurator.configure();

            //path to xyz file and csv file
            String xyzName = args[0];
            String csvName = args[1];

            //reads a coordinates file (xyz format only)
            XyzFile xyzf = XyzFileParser.parse(new File(xyzName));

            //generates a topology file
            RTF rtff = new RTF_generate(xyzf, csvName);
            
            //then a PSF file re-using data from PSF 
            PSF psff = new PSF_generate(rtff);
            
        } catch (IOException ex) {
            Logger.getLogger(RTF_generate.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

} // end of class PSF

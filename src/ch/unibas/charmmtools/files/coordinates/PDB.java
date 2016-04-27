/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.coordinates;

import org.apache.log4j.Logger;

/**
 * This abstract class defines a PDB file coordinates compatible with CHARMM
 *
 * @author hedin
 */
public abstract class PDB {

    protected static final Logger logger = Logger.getLogger(PDB.class);

    /**
     * The PDB file name 
     */
    protected String fname = null;

    /**
     * Boolean setting if PDB type is for CHARMM
     */
    protected boolean charmmPDBtype = true;

//    if (this.isExtendedFormat) {
//        /*fortran :
//         write(iunit,'(i10,2x,a)') nslct,'EXT'
//         fm2='(2I10,2X,A8,2X,A8,3F20.10,2X,A8,2X,A8,F20.10)'
//         */
//        format = "%10d%10d  %-8s  %-8s%20.10f%20.10f%20.10f  %-8s  %-8s%20.10f\n";
//    }
//    else {
//        /*fortran :
//         write(iunit,'(i5)') nslct
//         fm2='(2I5,1X,A4,1X,A4,3F10.5,1X,A4,1X,A4,F10.5)'
//         */
//        format = "%5d%5d %-4s %-4s%10.5f%10.5f%10.5f %-4s %-4s%10.5f\n";
//    }
//
//    protected final static String officialFormat = "";

    /**
     * CHARMM PDB format for String.format
     */
    protected final static String charmmFormat = "%-6s%5d %-4s %-4s %-5s   %8.3f%8.3f%8.3f%6.2f%6.2f      %-4s";

}

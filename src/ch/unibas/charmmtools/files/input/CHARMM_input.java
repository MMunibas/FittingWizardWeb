/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.input;

/**
 *
 * @author hedin
 */
public abstract class CHARMM_input {

    /*
     * bomlev is the error level causing abortion of CHARMM
     * prnlev regulates the level of output from CHARMM
     * we set here those variable to 2 "decent" values
     */
    protected int bomlev=0, prnlev=2;
    
    //for storing the command line related to the rtf topology file
    protected String rtf_String;

    //for storing the command line related to the rtf topology file
    protected String param_String;
    
    //for storing the command line related to the psf structure file
    protected String coord_String;
    //the fortran unit to use for I/O
    protected int coord_unit;

    protected NBONDS nbonds_params;

}//end of class

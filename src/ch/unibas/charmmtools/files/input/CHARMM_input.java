/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.input;

enum add_elec {

    ELEC("ELEC"),
    NOELectrostatics("NOEL"),
    VDW("VDW"),
    NOVDwaals("NOVD");

    private final String keyword;

    add_elec(String key) {
        this.keyword = key;
    }

    public String getKey() {
        return this.keyword;
    }
};

enum add_vdw {

    VDW("VDW"),
    NOVDwaals("NOVD");

    private final String keyword;

    add_vdw(String key) {
        this.keyword = key;
    }

    public String getKey() {
        return this.keyword;
    }
};

//enum add_ewald {
//    EWALD("EWAL"),
//    NOEWald("NOEW");
//
//    private final String keyword;
//
//    add_ewald(String key) {
//        this.keyword = key;
//    }
//
//    public String getKey() {
//        return this.keyword;
//    }
//};

enum add_elec_opt {

    CDIE, // Constant dielectric. Energy is proportional to 1/R.
    RDIE  // Distance dielectric. Energy is proportional to 1/(R-squared)
};

enum cut_type{

    SHIFted("SHIF"),
    SWITched("SWIT"),
    FSWItched("FSWI"),
    FSHIfted("FSHI");

    private final String keyword;

    cut_type(String key) {
        this.keyword = key;
    }

    public String getKey() {
        return this.keyword;
    }
};

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
    
    //non bonded parameters line
    protected String NB_params;
    /*
     * nbxmod : for building nonbonded list
     * can be modified for adding more that 1,4 interactions
     */
    protected int nbxmod;
    /*
     * scalars for storing cutoff and cuton values
     * cut_nb : Distance cutoff in generating the list of pairs

     * cutoff_nb : Distance cut at which the switching function eliminates
     * all contributions from a pair in calculating energies.

     * cuton_nb : Distance cut at which the smoothing function begins to reduce
     * a pair's contribution. This value is not used with SHFT.
     */
    protected double cut_nb,cutoff_nb,cuton_nb;
    //a scaling factor for the no-bonded 1,4 interactions
    protected double epsilon14scalingFactor;
    
    
    
    
}//end of class

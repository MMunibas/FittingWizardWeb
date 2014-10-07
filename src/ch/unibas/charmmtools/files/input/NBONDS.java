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
public class NBONDS {

    //--------------------------------------------------------------------------
    //          PUBLIC ENUMerations
    //--------------------------------------------------------------------------
    /*
     * First a few enumerations containing some of CHARMM keywords
     */
    /*
     * Choose if nbonds calculation are Atom or Group based
     */
    public enum nbonds_type {

        ATOM("ATOM"),
        GROUp("GROU");

        private final String keyword;

        nbonds_type(String key) {
            this.keyword = key;
        }

        public String getKey() {
            return this.keyword;
        }
    };

    /*
     * Enable or disable electrostatics
     */
    public enum add_elec {

        ELEC("ELEC"),
        NOELectrostatics("NOEL");

        private final String keyword;

        add_elec(String key) {
            this.keyword = key;
        }

        public String getKey() {
            return this.keyword;
        }
    };

    /*
     * Enable or disable Van der Waals parameters
     */
    public enum add_vdw {

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

    /*
     * For using Ewald summation instead of standard electrostatics
     */
    public enum add_ewald {

        EWALD("EWAL"),
        NOEWald("NOEW");

        private final String keyword;

        add_ewald(String key) {
            this.keyword = key;
        }

        public String getKey() {
            return this.keyword;
        }
    };

    /*
     * CDIE is a constant dielectric term. Energy is proportional to 1/R.
     * RDIE is a distance dependant term. Energy is proportional to 1/(R-squared)
     */
    public enum add_elec_opt {

        CDIElec("CDIE"),
        RDIElec("RDIE");

        private final String keyword;

        add_elec_opt(String key) {
            this.keyword = key;
        }

        public String getKey() {
            return this.keyword;
        }
    };

    /*
     * Choose the cutoff type
     * SWIT - Switching function used from CTONNB to CTOFNB values.
     * SHIF - Shifted potential acting to CTOFNB and zero beyond.
     * FSWI - Switching function acting on force only.  Energy is integral of force.
     * FSHI - Classical force shift method for CDIE (force has a constant offset)
     */
    public enum cut_type {

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

    /*
     * Exclusion Lists
     *
     * By default, vdw and electrostatic interactions between two bonded
     * 1-2 interactions) and two atoms bonded to a common atom (1-3 interactions)
     * atoms are excluded from the calculation of energy and forces.  Also,
     * special vdw parameters and an electrostatic scale factor (E14FAC) can be
     * applied to atom pairs separated by 3 bonds (1-4 interactions).  The
     * control of the exclusion list is by the integer variable, NBXMod.
     *
     * NBXMod =     0        Preserve the existing exclusion lists
     * NBXMod = +/- 1        Add nothing to the exclusion list
     * NBXMod = +/- 2        Add only 1-2 (bond) interactions
     * NBXMod = +/- 3        Add 1-2 and 1-3 (bond and angle)
     * NBXMod = +/- 4        Add 1-2 1-3 and 1-4 (scaled)
     * NBXMod = +/- 5        Add 1-2 1-3 and special 1-4 interactions
     */
    public enum nbxmod_type {

        PRESERVE(0, "Preserve the existing exclusion lists"),
        ADD_NOTHING(1, "Add nothing to the exclusion list"),
        ADD_1_2(2, "Add only 1-2 (bond) interactions"),
        ADD_1_2_AND_1_3(3, "Add 1-2 and 1-3 (bond and angle)"),
        ADD_1_2_AND_1_3_AND_1_4_SCALED(4, "Add 1-2 1-3 and 1-4 (scaled)"),
        ADD_1_2_AND_1_3_AND_1_4(5, "Add 1-2 1-3 and special 1-4 interactions");

        private final int value;
        private final String description;

        nbxmod_type(int val, String descr) {
            this.value = val;
            this.description = descr;
        }

        public int getValue() {
            return this.value;
        }

        public String getDescription() {
            return this.description;
        }
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    //non bonded parameters line
    protected String NB_params = "NBONDS ";

    /*
     * nbxmod : for building nonbonded list
     * can be modified for adding more than 1,4 interactions
     */
//    protected nbxmod_type nbxmod;

    /*
     * scalars for storing cutoff and cuton values
     * cut_nb : Distance cutoff in generating the list of pairs

     * cutoff_nb : Distance cut at which the switching function eliminates
     * all contributions from a pair in calculating energies.

     * cuton_nb : Distance cut at which the smoothing function begins to reduce
     * a pair's contribution. This value is not used with SHFT.
     */
    protected double cut_nb, cutoff_nb, cuton_nb;
    //a scaling factor for the no-bonded 1,4 interactions
    protected double epsilon14scalingFactor;

    public NBONDS(nbonds_type nbt, add_elec elec, add_vdw vdw, add_ewald ewald,
            add_elec_opt elec_opt, cut_type cuttype, nbxmod_type nbxmod) {

        NB_params += nbt.getKey() + " ";
        NB_params += elec.getKey() + " ";
        NB_params += vdw.getKey() + " ";
        NB_params += ewald.getKey() + " ";
        NB_params += elec_opt.getKey() + " ";
        NB_params += cuttype.getKey() + " ";
        NB_params += "NBXMod " + nbxmod.getValue() + " ";

    }

    /**
     * @return the NB_params
     */
    public String getNB_params() {
        return NB_params;
    }

}

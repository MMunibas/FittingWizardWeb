package ch.unibas.fitting.shared.charmm.web;

/**
 * Created by tschmidt on 27.06.2016.
 */
public class CharmmResultParserOutput {
    private double temp;
    private int nres;
    private double mmass;

    private double dg;
    private double gas_mtp;
    private double gas_vdw;
    private double solvent_mtp;
    private double solvent_vdw;

    private double box;
    private double density;

    private int natom;
    private int nconstr;
    private double egas;
    private double eliq;
    private double deltaH;

    private final static String find_natom = "Number of atoms";
    private final static String find_nres = "Number of residues";
    private final static String find_nconstr = "constraints will";
    private final static String find_temp = "FINALT =";

}

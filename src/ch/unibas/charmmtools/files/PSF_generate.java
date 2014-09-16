/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files;

/**
 * This class derived from the abstract PSF class is used for generating a new psf file useful for CHARMM
 *
 * @author hedin
 */
public final class PSF_generate extends PSF {

    private String header = "PSF EXT";
    private final String format00 = "%10d%s";
    private final String format01 = "%10d %8c %8c %8c %8c %4d %14.6G%14.6G%8d";
    private final String format01a = "%10d %8c %8c %8c %8c %4d %14.6G%14.6G%8d%14.6G%14.6G";
    private final String format01b = "%10d %8c %8c %8c %8c %4d %14.6G%14.6G%8d%14.6G%14.6G%1b";
    private final String format02 = "%10d %8c %8c %8c %8c %6c %14.6G%14.6G%8d%14.6G%14.6G";
    private final String format02a = "%10d %8c %8c %8c %8c %6c %14.6G%14.6G%8d%14.6G%14.6G";
    private final String format02b = "%10d %8c %8c %8c %8c %6c %14.6G%14.6G%8d%14.6G%14.6G";
    private final String format03 = "%10d%10d%10d%10d%10d%10d%10d%10d";
    private final String format04 = "%10d%10d%10d%10d%10d%10d%10d%10d%10d";
    private final String format05 = "%10d%10d%s";
    private final String format06 = "%10d%10d   %1b%14.6G%14.6G%14.6G";
    private final String format07 = "          %14.6G%14.6G%14.6G";
    private final String format08 = "%10d%s";

    public PSF_generate(RTF topolInfo) {

        /*
            TODO : NEED TO IMPROVE THIS PART
        */
        this.natom = topolInfo.getNatom();
        this.nbond = topolInfo.getNbonds();
        this.ntheta = topolInfo.getAngTypeList().size();//angles
        this.nphi = topolInfo.getDiheTypeList().size();//dihedrals
        this.nimphi = topolInfo.getNimpr();//impropers
        
        this.atomList = topolInfo.getAtmTypeList();
        this.bondList = topolInfo.getBndTypeList();
        this.angleList = topolInfo.getAngTypeList();
        this.diheList = topolInfo.getDiheTypeList();
        this.imprList = topolInfo.getImprTypeList();

        this.generate();
    }

    private void generate() {

    }



}//end class

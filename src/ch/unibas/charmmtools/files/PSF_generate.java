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
    }

//    private void generate() {
//
//    }



}//end class

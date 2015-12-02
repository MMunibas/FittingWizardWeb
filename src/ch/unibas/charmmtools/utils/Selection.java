/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.utils;

import ch.unibas.charmmtools.files.coordinates.COR;

/**
 *
 * @author hedin
 */
public class Selection {

    // references to data from cor or psf
    private int[] atomID = null;
    private int[] resID = null;
    private String[] resName = null;
    private String[] atomName = null;
    private String[] segName = null;
    private int[] segID = null;

    int nat;

    // the boolean array marking selected atoms
    boolean isSelected[] = null;

    public Selection(COR _cor){
        this.atomID = _cor.getAtomID();
        this.resID = _cor.getResID();
        this.resName = _cor.getResName();
        this.atomName = _cor.getAtomName();
        this.segName = _cor.getSegName();
        this.segID = _cor.getSegID();

        nat = atomID.length;
        allocateBooleanArray();
    }

//    public Selection(PSF _psf) {
//        this.atomID = _psf.getAtomID();
//        this.resID = _psf.getResID();
//        this.resName = _psf.getResName();
//        this.atomName = _psf.getAtomName();
//        this.segName = _psf.getSegName();
//
//        nat = atomID.length;
//        allocateBooleanArray();
//    }

    private void allocateBooleanArray() {
        isSelected = new boolean[nat];
        for (int i = 0; i < nat; i++) {
            isSelected[i] = false;
        }
    }

    public void selectByAtomID(int[] list) {
        for (int l : list) {
            isSelected[l - 1] = true;
        }
    }

    public void selectByAtomID(int from, int to) {
        for (int l = from; l <= to; l++) {
            isSelected[l - 1] = true;
        }
    }

    public void selectByResID(int[] list) {
        for (int n = 0; n < nat; n++) {
            for (int l : list) {
                if (resID[n] == l) {
                    isSelected[n] = true;
                }
            }
        }
    }

    public void selectByResID(int from, int to) {
        for (int n = 0; n < nat; n++) {
            if (resID[n] >= from || resID[n] <= to) {
                isSelected[n] = true;
            }
        }
    }

    public void selectBySegID(int[] list) {
        if (this.segID == null) {
            System.out.println("Error : selection by SegID not available when the Selection Object is initialised with a psf.");
        } else {
            for (int n = 0; n < nat; n++) {
                for (int l : list) {
                    if (segID[n] == l) {
                        isSelected[n] = true;
                    }
                }
            }
        }// end of if else
    }

    public void selectBySegID(int from, int to) {
        if (this.segID == null) {
            System.out.println("Error : selection by SegID not available when the Selection Object is initialised with a psf.");
        } else {
            for (int n = 0; n < nat; n++) {
                if (segID[n] >= from || segID[n] <= to) {
                    isSelected[n] = true;
                }
            }
        }// end of if else
    }

    public void selectByResName(String[] sele) {
        for (int n = 0; n < nat; n++) {
            for (String s : sele) {
                if (resName[n].compareToIgnoreCase(s) == 0) {
                    isSelected[n] = true;
                }
            }
        }
    }

    public void selectByAtomName(String[] sele) {
        for (int n = 0; n < nat; n++) {
            for (String s : sele) {
                if (atomName[n].compareToIgnoreCase(s) == 0) {
                    isSelected[n] = true;
                }
            }
        }
    }

    public void selectBySegName(String[] sele) {
        for (int n = 0; n < nat; n++) {
            for (String s : sele) {
                if (segName[n].compareToIgnoreCase(s) == 0) {
                    isSelected[n] = true;
                }
            }
        }
    }

    public boolean[] getSelectionArray() {
        return isSelected;
    }

}// end class Seletion

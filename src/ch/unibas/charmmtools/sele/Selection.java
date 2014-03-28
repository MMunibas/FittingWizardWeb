/*
 * Copyright (c) 2014, hedin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.unibas.charmmtools.sele;

import ch.unibas.charmmtools.io.COR;
import ch.unibas.charmmtools.io.PSF;

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

    public Selection(PSF _psf) {
        this.atomID = _psf.getAtomID();
        this.resID = _psf.getResID();
        this.resName = _psf.getResName();
        this.atomName = _psf.getAtomName();
        this.segName = _psf.getSegName();

        nat = atomID.length;
        allocateBooleanArray();
    }

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

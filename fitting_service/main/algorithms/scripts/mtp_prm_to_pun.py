#!/usr/bin/env python
#
# Copyright 2013 Tristan Bereau and Christian Kramer
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
#     limitations under the License.
#
####################
#
# Rewrite pun file with optimized MTP parameters provided (1) .txt
# file containing optimized parameters and (2) old pun file with LRA
# connectivity.
#
# Tristan Bereau (14.05.2012)

def mtp_prm_to_pun(fit_output, ref_lpun, new_pun):
    
    import os,sys,math
    import numpy as np
    
    # txt file of MTP parameters
    mtp = fit_output
    # old pun file
    pun = ref_lpun
    # output file
    out = new_pun
    # Net charge of the system
    chg = charge
    
    def usage():
        """Print Command line usage information"""
        print("Usage: mtp_prm.to.pun.py [-chg N] -txt mtp.txt -pun in.pun -out out.pun")
        print("       mtp.txt: optimized MTP parameters")
        print("        in.pun: old pun file with LRA connectivity.")
        print("       out.pun: output punfile")
        print("OPTIONAL:")
        print("          -chg: Net charge of the entire system.")
        exit(1)
    
    ##############
    # Read command line input
    
    assert mtp[-4:] == '.txt'
    assert pun[-3:] ==  'pun'
    
    #############
    # Load molecule
    
    from . import mtp_tools
    mol = mtp_tools.molecule()
    if pun[-4:] == '.pun':
        mol.readfrompunfile(pun)
        mol.Calc_locMTP()
    elif pun [-5:] == '.lpun':
        mol.readfromlpunfile(pun)
        mol.Calc_gloMTP()
    else:
        print("Error. Punfile extension not recognized.")
        exit(1)
    
    #############
    # Parse MTP parameter file
    mtp_coef = {}
    # Are there bond increments?
    binc = False
    try:
        f = open(mtp,'r')
    except:
        print("Error. Could not find",mtp+". Exiting.")
        exit(1)
    readf = f.readlines()
    for j in range(len(readf)):
        readf[j] = readf[j].split()
        mtp_coef[ readf[j][0] ] = float(readf[j][1])
        if readf[j][0][0:5] == 'BInc_':   binc = True
    
    #############
    # Set rank of each atom
    for atom in mol.atoms:
        atom.rank = 0
        for i in ['0','1c','1s']:
            if mol.atoms[atom.idx-1].atype+"_Q1"+i in mtp_coef:
                atom.rank = 1
        for i in ['0','1c','1s','2c','2s']:
            if mol.atoms[atom.idx-1].atype+"_Q2"+i in mtp_coef:
                atom.rank = 2
    
    
    ###############
    # If bond increments are present (rather than charges), use them to
    # determine charges.  Look for additional charges (on top of bond increments),
    # in case the molecule is charged.
    if binc == True:
        neigh_pairs = []
        for atom in mol.atoms:
            atom.chrg = 0 
            at1 = mol.atoms[atom.idx-1].atype
            if at1+"_Q00" in mtp_coef:
                atom.chrg += mtp_coef[ at1+"_Q00" ]
            if atom.refkind == 'ter':
                at2 = mol.atoms[atom.refatms[0]-1].atype
    
                if "BInc_"+at1+":"+at2 in mtp_coef:
                    atom.chrg += mtp_coef[ "BInc_"+at1+":"+at2 ]
                if "BInc_"+at2+":"+at1 in mtp_coef:
                    atom.chrg -= mtp_coef[ "BInc_"+at2+":"+at1 ]
                
            else:
                for j in atom.refatms:
                    at2 = mol.atoms[j-1].atype
                    
                    if "BInc_"+at1+":"+at2 in mtp_coef:
                        atom.chrg += mtp_coef[ "BInc_"+at1+":"+at2 ]
                    if "BInc_"+at2+":"+at1 in mtp_coef:
                        atom.chrg -= mtp_coef[ "BInc_"+at2+":"+at1 ]
    else:
        # Use charges:
        for atom in mol.atoms:
            atom.chrg = mtp_coef[ mol.atoms[atom.idx-1].atype+"_Q00" ]
    
    ##############
    # Set dipole and quadrupole moments
    for atom in mol.atoms:
        if atom.rank >= 2:
            atom.Qloc = [0., 0., 0., 0., 0.]
            index = 0
            for i in ['0','1c','1s','2c','2s']:
                if mol.atoms[atom.idx-1].atype+"_Q2"+i in list(mtp_coef.keys()):
                    atom.Qloc [ index ] = mtp_coef[
                        mol.atoms[atom.idx-1].atype+"_Q2"+i] 
                index += 1
        else:
            mol.rank = 1
    
        if atom.rank >= 1:
            atom.dloc = [0., 0., 0.]
            index = 0
            for i in ['0','1c','1s']:
                if mol.atoms[atom.idx-1].atype+"_Q1"+i in list(mtp_coef.keys()):
                    atom.dloc [ index ] = mtp_coef[
                        mol.atoms[atom.idx-1].atype+"_Q1"+i]
                index += 1
    
        else:
            mol.rank = 0
    
    tot_chrg = 0.0
    abs_chrg = 0.0
    for atom in mol.atoms:
        tot_chrg += atom.chrg
        abs_chrg += abs(atom.chrg)
        atom.chrg = [ atom.chrg ]
    if chg != "":
        excess_chg = chg - tot_chrg
        for atom in mol.atoms:
            atom.chrg[0] += excess_chg * abs(atom.chrg[0])/abs_chrg
    
    ##############
    # write output
    
    print("Total charge with new parameters:",tot_chrg)
    
    print("Writing output to",out)
    mol.Calc_gloMTP()
    mol.write2punfile(out)

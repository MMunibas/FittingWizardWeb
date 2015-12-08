'''
Class that helps handling a set of molecules and their multipole parameters
'''

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

import sys, os, numpy
import mtp_tools
import subprocess

class MolPop(object):
  def __init__(self, nmols=0, molecules=[], parms=[], incrs=[]):
    """ Keeps a population of molecules """
    self.nmols = nmols
    self.molecules = molecules
    self.parms = parms
    self.incrs = incrs
  
  def add_molecule_from_punfile(self,filename):
    """ Adds a molecule to MolPop from a punfile"""
    a = mtp_tools.molecule()
    a.readfrompunfile(filename)
    a.Calc_locMTP()
    self.molecules.append(a)
    self.nmols += 1

 
  def setMTPs(self,parms):
    """ Allows setting MTP parameters"""
    self.parms = parms
    for mol in self.molecules:
      for atom in mol.atoms:
        atom.chrg = parms[(atom.atype,'chrg')]
    atom.dloc = parms[(atom.atype,'dloc')]
    atom.Qloc = parms[(atom.atype,'Qloc')]
      mol.Calc_gloMTP()
      mol.adjust_charge()

  
  def setMTPs_from_coefflist(self,coeffs):
    """ Set Multipoles from Parameter List (after SIMPLEX)"""
    # First: convert coefficient list into multipoles on molecules
    a = self.parms.keys()
    a.sort()
    b = 0
    for key in a:
      for idx,inc in enumerate(self.incrs[key]):
        if not inc == 0:
      self.parms[key][idx] = coeffs[b]
      b += 1

    # Then grow multipoles on the molecules
    b = self.parms
    self.setMTPs(b)
     

  def get_Quality_coefflist(self,coeffs,quality='avg',detail=False):
    """
    Calculates the performance for the parameter set given in the call
    (Default output is average performance, median performance can be called
     with argument median)
    """
    # First: convert coefficient list into multipoles on molecules
    a = self.parms.keys()
    a.sort()
    b = 0
    for key in a:
      for idx,inc in enumerate(self.incrs[key]):
        if not inc == 0:
      self.parms[key][idx] = coeffs[b]
      b += 1
    
    # update dummy charges, if any
    self.update_dummy_charges()


    # Then grow multipoles on the molecules
    b = self.parms
    self.setMTPs(b)
    
    # Then do the energy calculation
    
    energy_mean = 0
    energies_median = []
    totalweight = 0
    for mol in self.molecules:
      mol.write2punfile(mol.filename+'_tmp.pun')
      if not os.path.isfile(mol.filename+'.cube'):
        print "Can not find "+mol.filename+'.cube in the current directory.'
    print "Program aborted"
    exit(0)
      if not os.path.isfile(mol.filename+'.vdw'):
        print "Can not find "+mol.filename+'.vdw in the current directory.'
    print "Program aborted"
    exit(0)
      fcomp_call = ['fieldcomp','-cube',mol.filename+'.cube','-vdw',mol.filename+'.vdw',
                    '-pun',mol.filename+'_tmp.pun','-sigma_only']

      try:
        Enew = float(subprocess.Popen(fcomp_call,stdout=subprocess.PIPE).communicate()[0].split()[0])
      except OSError:
        print "Can't run fieldcomp. Program aborted"
        exit(1)

      if (detail == True): print mol.name, Enew
      os.remove(mol.filename+'_tmp.pun')
      energy_mean+= Enew*mol.fitweight
      energies_median.append(Enew)
      totalweight += mol.fitweight
    if quality == 'median':
      energies_median.sort()
      if len(energies_median) % 2 == 1:
        return energies_median[(len(energies_median)+1)/2-1]
      else:
        lower = energies_median[len(energies_median)/2-1]
        upper = energies_median[len(energies_median)/2]
        return (float(lower + upper)) / 2 
    else:
      return energy_mean/totalweight

    
  def get_Quality(self,quality='avg',detail=False):
    """
    Calculates the performance for the current parameter set.
    Default output is average performance, median performance can be called
     with argument median)
    """
    energy_mean = 0
    energies_median = []
    totalweight = 0


    # update dummy charges, if any
    self.update_dummy_charges()

    # reassign coefficients before writing pun file
    self.setMTPs(self.parms)


    for mol in self.molecules:
      mol.write2punfile(mol.filename+'.pun')
      if not os.path.isfile(mol.filename+'.cube'):
        print "Can not find "+mol.filename+'.cube in the current directory.'
    print "Program aborted"
    exit(0)
      if not os.path.isfile(mol.filename+'.vdw'):
        print "Can not find "+mol.filename+'.vdw in the current directory.'
    print "Program aborted"
    exit(0)
      fcomp_call = ['fieldcomp','-cube',mol.filename+'.cube','-vdw',mol.filename+'.vdw','-pun',mol.filename+'.pun','-sigma_only']
      Enew = float(subprocess.Popen(fcomp_call,stdout=subprocess.PIPE).communicate()[0].split()[0])
      if (detail == True): print mol.name, Enew
      energy_mean+= Enew*mol.fitweight
      energies_median.append(Enew)
      totalweight += mol.fitweight
    if quality == 'median':
      energies.sort()
      if len(energies_median) % 2 == 1:
        return energies_median[(len(energies_median)+1)/2-1]
      else:
        lower = energies_median[len(energies_median)/2-1]
        upper = energies_median[len(energies_median)/2]
        return (float(lower + upper)) / 2 
    else:
      return energy_mean/totalweight

  
  def print_locMTPs(self):
    """
    Prints the localized multipoles sorted by atoms for all molecules
    """
    atomtypes = []
    for mol in self.molecules:
      for atom in mol.atoms:
        atomtypes.append(atom.atype)
    atomtypes = list(set(atomtypes))
    atomtypes.sort()
    for atomtype in atomtypes:
      print "Atomtype "+atomtype+", CHARGE"
      for mol in self.molecules:
        print "  Molecule "+mol.name
    for atom in mol.atoms:
      if atom.atype == atomtype: print atom.chrg
      print "Atomtype "+atomtype+", DIPOLE"
      for mol in self.molecules:
        print "  Molecule "+mol.name
    for atom in mol.atoms:
      if atom.atype == atomtype: print atom.dloc
      print "Atomtype "+atomtype+", QUADRUPOLE"
      for mol in self.molecules:
        print "  Molecule "+mol.name
    for atom in mol.atoms:
      if atom.atype == atomtype: print atom.Qloc
      print  
    return
       
  def average_mtps(self):
    """
    Average all multipoles from the same atom type
    """
    parms = {}
    atomtypes = []
    for mol in self.molecules:
      for atom in mol.atoms:
        atomtypes.append(atom.atype)
    atomtypes = list(set(atomtypes))
    atomtypes.sort()
    for atomtype in atomtypes:
      a = numpy.array([0])
      b = numpy.array([0,0,0])
      c = numpy.array([0,0,0,0,0])
      d = 0
      for mol in self.molecules:
        for atom in mol.atoms:
      if atom.atype == atomtype: 
        a = a + atom.chrg 
        b = b + atom.dloc
        c = c + atom.Qloc
        d += 1
      parms[(atomtype,'chrg')] = a/d
      parms[(atomtype,'dloc')] = b/d
      parms[(atomtype,'Qloc')] = c/d

    return parms

  def set_small_coeffs_zero(self,limit = 0.001):
    """
    Set all coefficients smaller than limit to zero
    """
    for key in self.parms:
      for idx, coeff in enumerate(self.parms[key]):
        if abs(coeff) < limit: self.parms[key][idx] = 0
    
    
  def constrain_dip_quad_dummy_atoms(self):
    """
    Constrain point charges involved in dipoles and quadrupoles
    ("*_dipX" and "*_quadX")
    """
    for mol in self.molecules:
      # First atom ("_dip1", "_quad1") will be optimized, while all
      # the others will be constrained.  Since we're dealing with
      # point charges, idx = 0.
      for i in mol.q_dip:
        if int(i.atype[len(i.atype)-1:len(i.atype)]) == 2:
          self.incrs[(i.atype,'chrg')][0] = 0

      for l in mol.q_quad:
        num = int(l.atype[len(l.atype)-1:len(l.atype)])
        if num == 2 or num == 3 or num == 4:
          self.incrs[(l.atype,'chrg')][0] = 0

  def update_dummy_charges(self):
    """
    Update any dummy charges involved in dipoles and
    quadrupoles.
    """
    for mol in self.molecules:
      for i in mol.q_dip:
        if int(i.atype[len(i.atype)-1:len(i.atype)]) == 2:
          atom_name = i.atype[0:len(i.atype)-1] + str(1)
          self.parms[(i.atype,'chrg')] = (-1.) * \
              self.parms[(atom_name,'chrg')] 
      
      for l in range(len(mol.q_quad)):
        atom = mol.q_quad[l]
        num = int(atom.atype[len(atom.atype)-1:len(atom.atype)])
        if num == 2 or num == 3 or num == 4:
          atom_name = atom.atype[0:len(atom.atype)-1] + str(1)
          self.parms[(atom.atype,'chrg')] = mol.q_quad_fac[l] * \
              self.parms[(atom_name,'chrg')]
          

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
# Library that helps with basic multipole actions

###############
# Set defaults

import numpy, math

sqrt_3 = 1.7320508075688772
a2b = 1.889725989
atomic_mass = {'H' : 1.007,
               'C' : 12.01,
               'N' : 14.00,
               'O' : 15.99,
               'F' : 18.99,
               'P' : 30.97,
               'S' : 32.06,
               'Cl': 35.45,
               'Br': 79.90,
               'I' : 126.9}


class atom(object):
  def __init__(self,atype,coords,idx,rnk):
    """Initialize atom object"""
    self.coords = coords
    self.idx = idx
    self.atype = atype
    self.rank = rnk
    self.refatms = []
    self.refkind = ''
    self.vdw_radius = 0.0
    self.chrg = numpy.array([0])
    self.dglo = numpy.array([0,0,0])
    self.Qglo = numpy.array([0,0,0,0,0])
    self.dloc = numpy.array([0,0,0])
    self.Qloc = numpy.array([0,0,0,0,0])
    self.mass = 0.0

  def SetGlobMTP(self,refkind,refatms,chrg,dglo=numpy.array([0,0,0]),Qglo=numpy.array([0,0,0,0,0])):
    """Set MTP parameters within the global frame"""
    self.refkind = refkind
    self.refatms = refatms
    self.chrg = chrg
    self.dglo = dglo
    self.Qglo = Qglo
    
  def SetLocMTP(self,mtptype,pos,value):
    """Set MTP parameters within the local frame"""
    if mtptype == 'dloc': self.dloc[pos] = value
    if mtptype == 'Qloc': self.Qloc[pos] = value

  def GetAtomicMass(self):
    """Assign atomic mass to atom.mass"""
    try:
      at = self.atype[0]
      if len(self.atype) > 1:
        for i in range(len(self.atype)-1):
          if self.atype[i+1] in ['0','1','2','3','4','5','6','7','8','9','.']: break
          if self.atype[i+1].isupper() : break
          at = at + self.atype[i+1]
      self.mass = atomic_mass[at]
    except:
      print ("Cannot determine element of atom ",self.idx," with atomtype ",self.atype)
      print ("Please define manually")

class molecule(object):
  def __init__(self, name='', natoms=0, atoms=[], filename=''):
    """Initialize molecule object"""
    self.name = ''
    self.natoms = 0
    self.atoms = []
    self.filename = ''
    self.fitweight = 1
    self.cog = ''
    self.dmol = numpy.array([0.0,0.0,0.0])
    self.Qmol = numpy.array([0.0,0.0,0.0,0.0,0.0])
    self.totcharge = 0
    # generate dipoles and quadrupoles from symmetric arrangements of
    # point charges
    self.q_dip      = []
    self.q_quad     = []
    self.q_quad_fac = []

  def readfrompunfile(self,filename):
    """
    Read molecule specifications from .pun file with
    local referance axis system information
    """
    f = open(filename,'r')
    lines = f.readlines()
    f.close()
    if 'Local Multipole file' in lines[1]:
      print (filename,' is a local multipole file.')
      return
    self.name = lines[0].rstrip()[1:]
    if self.name == '': self.name = filename[:filename.rindex('.')]
    self.filename = filename[:filename.rindex('.')]
    j = 0
    idx = 1
    self.natoms = 0
    header = True

    while j <= len(lines):
      line = lines[j].split()
      
      if header:
        if len(line) >= 6 and line[4] == "Rank" \
          and line[0] not in ['#','!']:
          header = False
        else:
          j += 1
          continue
      if line[0] == 'LRA:' : break
      atype = line[0]
      coords = numpy.array([float(line[1]),float(line[2]),float(line[3])])
      rnk = int(line[5])
      a = atom(atype,coords,idx,rnk)

      # look for dummy atoms that will generate dipoles/quadrupoles
      if atype.find('_dip') > -1:
        self.q_dip.append(a)
      if atype.find('_quad') > -1:
        self.q_quad.append(a)

      refkind = ''
      refatms = []
      chrg = numpy.array([float(lines[j+1])])
      if rnk > 0:
        line = lines[j+2].split()
        dglo = numpy.array([float(line[0]), float(line[1]), 
                            float(line[2])])
      else:
        dglo = numpy.array([0.0, 0.0, 0.0])
      if rnk > 1:
        line = lines[j+3].split()
        Qglo = numpy.array([float(line[0]), float(line[1]), 
                            float(line[2]), float(line[3]),
                            float(line[4])])
      else:
        Qglo = numpy.array([0.0, 0.0, 0.0, 0.0, 0.0])
      a.SetGlobMTP(refkind,refatms,chrg,dglo,Qglo)
      self.atoms.append(a)
      self.natoms += 1
      self.totcharge += chrg
      idx += 1
      j += 3 + rnk

    if lines[j].split()[0] == 'LRA:':
      j += 1
      for k in range(self.natoms):
        line = lines[j+k].split()
        self.atoms[k].refkind = line[0]
        refatms = [int(l) for l in line[1:]]
        self.atoms[k].refatms = refatms

    print ("Total charge of the molecule:", float(self.totcharge))
    self.check_consistency_dip_quad()
    

  def readfromrawpunfile(self,filename):
    """
    Read molecule specifications from .pun file without
    local referance axis system information
    """
    f = open(filename,'r')
    lines = f.readlines()
    f.close()
    if 'Local Multipole file' in lines[1]:
      print (Filename,' is a local multipole file.')
      return
    self.name = lines[0].rstrip()[1:]
    if self.name == '': self.name = filename[:filename.index('.')]
    self.filename = filename[:filename.rindex('.')]
    j = 0
    idx = 1
    self.natoms = 0
    header = True

    while j <= len(lines):
      line = lines[j].split()
      
      if header:
        if len(line) >= 6 and line[4] == "Rank" \
          and line[0] not in ['#','!']:
          header = False
        else:
          j += 1
          continue
      if line[0] == 'LRA:' : break
      atype = line[0]
      coords = numpy.array([float(line[1]),float(line[2]),float(line[3])])
      rnk = int(line[5])
      a = atom(atype,coords,idx,rnk)

      # look for dummy atoms that will generate dipoles/quadrupoles
      if atype.find('_dip') > -1:
        self.q_dip.append(a)
      if atype.find('_quad') > -1:
        self.q_quad.append(a)

      refkind = ''
      refatms = []
      chrg = numpy.array([float(lines[j+1])])
      line = lines[j+2].split()
      dglo = numpy.array([float(line[0]),float(line[1]),float(line[2])])
      line = lines[j+3].split()
      Qglo = numpy.array([float(line[0]),float(line[1]),float(line[2]),float(line[3]),float(line[4])])
      a.SetGlobMTP(refkind,refatms,chrg,dglo,Qglo)
      self.atoms.append(a)
      self.natoms += 1
      self.totcharge += chrg
      idx += 1
      j += 5

    self.check_consistency_dip_quad()


  def readfromlpunfile(self,filename):
    """
    Read molecule specifications from .lpun file with
    MTPs defined within the local referance axis system
    """
    f = open(filename,'r')
    lines = f.readlines()
    f.close()
    self.name = lines[0].split()[1]
    if self.name == '': self.name = filename[:filename.index('.')]
    self.filename = filename[:filename.index('.')]
    i = 0
    while True:
      if lines[i][0][0] == '!' or lines[i][0][0] == '#' or len(lines[i]) == 0: 
        i+= 1
      else:
        break
    
    while True:
      if i == len(lines): break
      line = lines[i].split()
      idx = int(line[0])
      atype = line[1]
      coords = numpy.array([float(line[2]),float(line[3]),float(line[4])])
      rnk = int(line[6])
      a = atom(atype,coords,idx,rnk)

      # look for dummy atoms that will generate dipoles/quadrupoles
      if atype.find('_dip') > -1:
        self.q_dip.append(a)
      if atype.find('_quad') > -1:
        self.q_quad.append(a)

      i+=1
      line = lines[i].split()
      a.refkind = line[1]
      refatms = [int(l) for l in line[2:]]
      a.refatms = refatms
      i+=1
      a.chrg = numpy.array([float(lines[i])])
      i+= 1
      a.dloc = numpy.array([float(j) for j in lines[i].split()])
      i+= 1
      a.Qloc = numpy.array([float(j) for j in lines[i].split()])
      i+= 2
      self.atoms.append(a)
      self.totcharge += a.chrg
    
    self.natoms = len(self.atoms)
    self.check_consistency_dip_quad()

  
  def write2punfile(self,filename = ''):
    """
    Write molecule specifications to .pun file (including LRA information)
    """
    if filename == '': filename = self.filename[:self.filename.index('.')]+'.pun'
    f = open(filename,'w')
    f.write('! '+self.name+'\n! File generated by mtp_tools.py\n!\n')
    for i in self.atoms:
      f.write('\n')
      text = i.atype+'\t'+str(i.coords[0])+'\t'+str(i.coords[1])+'\t'+str(i.coords[2])+'\tRank\t'+str(i.rank)+'\n'
      f.write(text)
      f.write(str(i.chrg[0])+'\n')
      if i.rank > 0:
        f.write(str(i.dglo[0])+'\t'+str(i.dglo[1])+'\t'+str(i.dglo[2])+'\n')
      if i.rank > 1:
        f.write(str(i.Qglo[0])+'\t'+str(i.Qglo[1])+'\t'+str(i.Qglo[2])+'\t'+str(i.Qglo[3])+'\t'+str(i.Qglo[4])+'\n')
    f.write('\n')
    f.write('LRA:\n')
    for i in self.atoms:
      text = i.refkind+'\t'
      for j in i.refatms: text = text + str(j) + '\t'
      text = text[:-1] + '\n'
      f.write(text)
    f.flush()
    f.close()


  
  def check_consistency_dip_quad(self):
    """
    Check consistency of point-charge-generated dipoles and
    quadrupoles.  Dipoles go by pairs and quadrupoles by sets of
    four atoms.
    Check that charges are exactly equal in magnitude.
    Check that rank of these particles is 0.
    """
    if len(self.q_dip) % 2 != 0:
      print ("Error. Odd number of dipole-generating "\
          "point charges.")
      exit(1)
    if len(self.q_quad) % 4 != 0:
      print ("Error. Number of quadrupole-generating point " \
          "charges is not a multiple of 4.")
      exit(1)
    for i in self.q_dip:
      identified_terms = numpy.array([0, 0])
      name_i = i.atype[0:len(i.atype)-1]
      num_i  = int(i.atype[len(i.atype)-1:len(i.atype)])
      if num_i == 1 or num_i == 2:
        identified_terms[num_i-1] += 1
      # match with pair
      for j in self.q_dip:
        if identified_terms[0] == 1 and identified_terms[1] == 1:
          break 
        name_j = j.atype[0:len(j.atype)-1]
        num_j  = int(j.atype[len(j.atype)-1:len(j.atype)])
        if name_j == name_i:
          if (num_i == 1 and num_j == 2) or \
                (num_i == 2 and num_j == 1): 
            identified_terms[num_j-1] += 1
            if j.chrg * (-1.) != i.chrg:
              print ("Error. charges between atoms", \
                  i.atype,"and",j.atype,"do not match.")
              exit(1)

      # Check that both elements of the list are populated
      if identified_terms[0] == 0 or identified_terms[1] == 0:
        print ("Error. Non-matching dummy atoms for dipole" \
            " interaction.")
        exit(1)

    # Same thing for quadrupole
    for i in self.q_quad:
      num_i = num_j = num_k = num_l = 0
      identified_terms = numpy.array([0, 0, 0, 0])
      num_pos_chrgs = 0
      num_neg_chrgs = 0
      name_i = i.atype[0:len(i.atype)-1]
      num_i  = int(i.atype[len(i.atype)-1:len(i.atype)])

      if num_i == 1:
        identified_terms[num_i-1] += 1
        if float(i.chrg) > 0.:
          num_pos_chrgs += 1
        else:
          num_neg_chrgs += 1
        self.q_quad_fac.append(1.)

        # match with number 2
        for j in self.q_quad:
          name_j = j.atype[0:len(j.atype)-1]
          num_j  = int(j.atype[len(j.atype)-1:len(j.atype)])
          if name_j == name_i and num_j == 2:          
            identified_terms[num_j-1] += 1
            compare_abs_charges(i,j)
            if float(j.chrg) > 0.:
              num_pos_chrgs += 1
            else:
              num_neg_chrgs += 1
            self.q_quad_fac.append(float(j.chrg)/float(i.chrg))
            break
     
        # match with number 3
        for k in self.q_quad:
          name_k = k.atype[0:len(k.atype)-1]
          num_k  = int(k.atype[len(k.atype)-1:len(k.atype)])
          if name_k == name_i and num_k == 3:
            identified_terms[num_k-1] += 1
            compare_abs_charges(i,k)
            if float(k.chrg) > 0.:
              num_pos_chrgs += 1
            else:
              num_neg_chrgs += 1
            self.q_quad_fac.append(float(k.chrg)/float(i.chrg))
            break

        # match with number 4
        for l in self.q_quad:
          name_l = l.atype[0:len(l.atype)-1]
          num_l  = int(l.atype[len(l.atype)-1:len(l.atype)])
          if name_l == name_i and num_l == 4:
            identified_terms[num_l-1] += 1
            compare_abs_charges(i,l)
            if float(l.chrg) > 0.:
              num_pos_chrgs += 1
            else:
              num_neg_chrgs += 1
            self.q_quad_fac.append(float(l.chrg)/float(i.chrg))
            break

        # Check that all elements of the list are populated
        if identified_terms[0] == 0 or identified_terms[1] == 0 \
              or identified_terms[2] == 0 or identified_terms[3] == 0:
          print ("Error. Non-matching dummy atoms for quadrupole" \
            " interaction.")
          exit(1)
          
        # Check that there are two negative and two positive charges
        if num_pos_chrgs != 2 or num_pos_chrgs != 2:
          print ("Error. Arrangement of charges does not match " \
              "for atoms", i.atype, ",", j.atype,",", k.atype, \
              "and", l.atype)
          exit(1)


  def Calc_locMTP(self):
    """
    Rotate MTP parameters from global to local frame
    """
    for i in self.atoms:
      AC = i.coords      # AC are the atomic coordinates
      RC = []         # RC are the reference coordinates of the reference atoms
      for j in i.refatms: RC.append(self.atoms[j-1].coords)
      TM = Get_local_XYZ(AC,i.refkind,RC)    # TM is the Transformation Matrix. Maybe directly generate the inverse here
      TM = numpy.linalg.inv(TM)
      
      temp = SpH2C_D(i.dglo)
      temp1 = transform_dipole(temp,TM)
      temp2 = C2SpH_D(temp1)
      i.dloc = temp2
  
      temp = SpH2C_Q(i.Qglo)
      temp1 = transform_qpole(temp,TM)
      temp2 = C2SpH_Q(temp1)
      i.Qloc = temp2
    
  def Calc_gloMTP(self):
    """
    Rotate MTP parameters from local to global frame
    """

    print ("Calculating atomic MTPs in global frame for:")
    for i in self.atoms:
      print ("Atom ",i.atype,", ref frame: ",i.refkind,i.refatms)
      AC = i.coords      # AC are the atomic coordinates
      RC = []        # RC are the reference coordinates of the reference atoms
      for j in i.refatms: 
        if j != 0 : RC.append(self.atoms[j-1].coords)
      TM = Get_local_XYZ(i.coords,i.refkind,RC)    # TM is the Transformation Matrix. Maybe directly generate the inverse here
            
      temp = SpH2C_D(i.dloc)
      temp1 = transform_dipole(temp,TM)
      temp2 = C2SpH_D(temp1)
      i.dglo = temp2
  
      temp = SpH2C_Q(i.Qloc)
      temp1 = transform_qpole(temp,TM)
      temp2 = C2SpH_Q(temp1)
      i.Qglo = temp2

  def set_coefficients_zero_due_to_symmetry(self):
    """
    Find MTP coefficients that can be set to zero due to local symmetries.
    """
    # MTP coefficients of the form:
    # dloc = [mu_z, mu_x, mu_y]
    # Qloc \propto [Q_z^2, Q_xz, Q_yz, Q_x^2-y^2, Q_xy]    
    for atom in self.atoms:
      neighbourtypes = [self.atoms[i-1].atype for i in atom.refatms]
      for i in range(len(neighbourtypes)):
        at = neighbourtypes[i][0]
        if len(neighbourtypes[i]) > 1:
          for j in range(len(neighbourtypes[i])-1):
            if neighbourtypes[i][j+1].isupper(): break
            at = at + neighbourtypes[i][j+1]
        neighbourtypes[i] = at
      if atom.refkind == "int":
        if len(neighbourtypes) == 4:
          if len(set(neighbourtypes)) == 1:
            atom.dloc = [0., 0., 0.]
            atom.Qloc = [0., 0., 0., 0., 0.]
          elif neighbourtypes[0] == neighbourtypes[1]:
            atom.dloc[1] = 0. 
            atom.Qloc[1] = 0.
            atom.Qloc[4] = 0.
            if neighbourtypes[2] == neighbourtypes[3]:
              atom.dloc[2] = 0. 
              atom.Qloc[0] = 0. 
              atom.Qloc[2] = 0.
            if neighbourtypes[0] == neighbourtypes[2]:
              atom.dloc[2] = 0.
              atom.Qloc[2] = 0. 
              atom.Qloc[3] = 0.
        elif len(neighbourtypes) == 3:
          if neighbourtypes[0] == neighbourtypes[1]:
            atom.dloc[1] = 0. 
            atom.Qloc[1] = 0. 
            atom.Qloc[4] = 0.
            if neighbourtypes[0] == neighbourtypes[2]:
              atom.dloc[2] = 0. 
              atom.Qloc[2] = 0. 
              atom.Qloc[3] = 0.
        elif len(neighbourtypes) == 2:
          atom.dloc[0] = 0.
          atom.Qloc[1] = 0.
          atom.Qloc[2] = 0.
          if len(set(neighbourtypes)) == 1:
            atom.dloc[1] = 0.
            atom.Qloc[4] = 0.
      elif atom.refkind == "ter":
        if len(neighbourtypes) == 4:
          if neighbourtypes[1] == neighbourtypes[2]:
            atom.dloc[1] = 0. 
            atom.Qloc[1] = 0. 
            atom.Qloc[4] = 0.
            if neighbourtypes[1] == neighbourtypes[3]:
              atom.dloc[2] = 0. 
              atom.Qloc[2] = 0. 
              atom.Qloc[3] = 0.
        elif len(neighbourtypes) == 3:
          if neighbourtypes[1] == neighbourtypes[2]:
            atom.dloc[1] = 0. 
            atom.Qloc[1] = 0. 
            atom.Qloc[4] = 0.
        elif len(neighbourtypes) == 2:
          atom.dloc[2] = 0. 
          atom.Qloc[2] = 0. 
          atom.Qloc[4] = 0.
      elif atom.refkind == "c3v":
        # Same as 4 neighbors, B1=B2=B3.
        atom.dloc[1] = 0. 
        atom.dloc[2] = 0.
        atom.Qloc[1] = 0.
        atom.Qloc[2] = 0. 
        atom.Qloc[3] = 0.
        atom.Qloc[4] = 0.
      elif atom.refkind == "lin":
        # linear symmetry. Only keep coefficients along Z
        atom.dloc[1] = 0.
        atom.dloc[2] = 0.
        atom.Qloc[1] = 0.
        atom.Qloc[2] = 0. 
        atom.Qloc[3] = 0.
        atom.Qloc[4] = 0.
      else:
        print ("Do not understand the atom kind",atom.refkind)
        exit(1)
            


  def write_localized_mtp_file(self,filename):
    """
    Write molecule specifications in .pun file (with MTP parameters in the local frame)
    """
    f = open(filename,'w')
    f.write('#'+self.name+'\n')
    f.write('#Local Multipole file: Multipoles given in this file are localized in each specific reference axis frame!\n#\n')
    for v,i in enumerate(self.atoms):
      text = str(v+1)+' '+i.atype+' '+str(i.coords[0])+' '+str(i.coords[1])+' '+str(i.coords[2])+' Rank '+str(i.rank)+'\n'
      f.write(text)
      text = "LRA: "+i.refkind+' '
      for j in i.refatms: text = text + str(j) + ' '
      for j in range(4-len(i.refatms)): text = text + '0 '
      text = text[:-1] + '\n'
      f.write(text)
      f.write(str(i.chrg[0])+'\n')
      f.write(str(i.dloc[0])+' '+str(i.dloc[1])+' '+str(i.dloc[2])+'\n')
      f.write(str(i.Qloc[0])+' '+str(i.Qloc[1])+' '+str(i.Qloc[2])+' '+str(i.Qloc[3])+' '+str(i.Qloc[4])+'\n')
      f.write('\n')
    f.close()

  def adjust_charge(self):
    """
    Adjust the total molecular charge
    """
    tot_charge = 0
    abs_charge = 0
    for atom in self.atoms: 
      # exclude dummy atoms
      if atom.atype.find('_dip') == -1 and \
            atom.atype.find('_quad') == -1:
        tot_charge += float(atom.chrg)
        abs_charge += float(abs(atom.chrg))
    excess_charge = float(tot_charge) - float(self.totcharge)
    if abs_charge != 0:
      for atom in self.atoms: 
        if atom.atype.find('_dip') == -1 and \
              atom.atype.find('_quad') == -1:
          atom.chrg = numpy.array([float(atom.chrg) + 
                                   (-1.) * float(excess_charge) * 
                                   (float(abs(atom.chrg))/abs_charge)]) 

  def get_cog(self):
    """
    Calculate center of gravity of the molecule
    """
    cog = numpy.array([0.0,0.0,0.0])
    mol_mass = 0.0
    for atom in self.atoms:
      if atom.mass == 0.0: atom.GetAtomicMass()
      cog = cog + atom.coords*atom.mass
      mol_mass = mol_mass + atom.mass
    cog = cog/mol_mass
    self.cog = cog

  def Calc_dmol(self):
    """ Calculate molecular dipole moment"""
    self.dmol = numpy.array([0.0,0.0,0.0])
    for atom in self.atoms:
      self.dmol[0] += atom.coords[2]*atom.chrg*a2b
      self.dmol[1] += atom.coords[0]*atom.chrg*a2b
      self.dmol[2] += atom.coords[1]*atom.chrg*a2b
      self.dmol += atom.dglo

  def Calc_molMTP(self):
    """ Calculate molecular quadrupole moment on the center of gravity """
# First determine center of gravity(cog) for molecule
#    if self.cog == '': self.get_cog()
    self.Qmol = numpy.array([0.0,0.0,0.0,0.0,0.0])
# Then calculate molecular quadrupole at cog
    for atom in self.atoms:
      rx = a2b*(atom.coords[0])
      ry = a2b*(atom.coords[1])
      rz = a2b*(atom.coords[2])
      r2 = rx**2+ry**2+rz**2
# Q20
      self.Qmol[0] = self.Qmol[0] + atom.Qglo[0] + 2*rz*atom.dglo[0] - rx*atom.dglo[1] - ry*atom.dglo[2] + 0.5*(3*rz**2-r2)*atom.chrg
# Q21c
      self.Qmol[1] = self.Qmol[1] + atom.Qglo[1] + sqrt_3*rz*atom.dglo[1] + sqrt_3*rx*atom.dglo[0] + sqrt_3*rx*rz*atom.chrg
# Q21s
      self.Qmol[2] = self.Qmol[2] + atom.Qglo[2] + sqrt_3*rz*atom.dglo[2] + sqrt_3*ry*atom.dglo[0] + sqrt_3*ry*rz*atom.chrg
# Q22c
      self.Qmol[3] = self.Qmol[3] + atom.Qglo[3] + sqrt_3*rx*atom.dglo[1] - sqrt_3*ry*atom.dglo[2] + sqrt_3*0.5*(rx**2-ry**2)*atom.chrg
# Q22s
      self.Qmol[4] = self.Qmol[4] + atom.Qglo[4] + sqrt_3*ry*atom.dglo[1] + sqrt_3*rx*atom.dglo[2] + sqrt_3*rx*ry*atom.chrg


def transform_dipole(dipole,transfm):
  '''Transforms a dipole d according to the transformation matrix T: dt = Td '''
  return numpy.dot(transfm,dipole)
  
def transform_qpole(qpole,transfm):
  '''Transforms a quadrupole Q according to the transformation matrix T: Q(t) = TQ(T^-1)'''
  a = numpy.linalg.inv(transfm)
  b = numpy.dot(transfm,qpole)
  return numpy.dot(b,a)
  
# The order in the spherical harmonics definition of dipoles is 
#    SpHD = (Q10, Q11c, Q11s)
# The order in the cartesian coordinates definition of dipole is 
#    CartD = (muX, muY, muZ)

def SpH2C_D(SpHD):
  '''Converts a Spherical Harmonic representation of a dipole to the Cartesian representation'''
  return numpy.array([SpHD[1],SpHD[2],SpHD[0]])
  
def C2SpH_D(CartD):
  '''Converts a Cartesian representation of a dipole to the Spherical Harmonic representation'''
  return numpy.array([CartD[2],CartD[0],CartD[1]])
  
# The order in the spherical harmonics definition of quadrupoles is
#    SpHQ = (Q20, Q21c, Q21s, Q22c, Q22s)
# The cartesian matrix of quadrupoles is defined as
#
#            Qxx  Qxy  Qxz
#    CartQ = {   Qxy  Qyy  Qyz   }
#            Qxz  Qyz  Qzz

def SpH2C_Q(SpHQ):
  '''Converts a Spherical Harmonic representation of a quadrupole to the Cartesian representation'''
  Qxx = -0.5*SpHQ[0]+0.5*sqrt_3*SpHQ[3]
  Qyy = -0.5*SpHQ[0]-0.5*sqrt_3*SpHQ[3]
  Qzz = SpHQ[0]
  Qxy = 0.5*sqrt_3*SpHQ[4]
  Qxz = 0.5*sqrt_3*SpHQ[1]
  Qyz = 0.5*sqrt_3*SpHQ[2]
  return numpy.array([(Qxx,Qxy,Qxz),(Qxy,Qyy,Qyz),(Qxz,Qyz,Qzz)])
  
def C2SpH_Q(CartQ):
  '''Converts a Cartesian representation of a dipole to the Spherical Harmonic representation'''
  Q20  = CartQ[2][2]
  Q21c = 2./sqrt_3 * CartQ[0][2]
  Q21s = 2./sqrt_3 * CartQ[1][2]
  Q22c = 1./sqrt_3 * (CartQ[0][0]-CartQ[1][1])
  Q22s = 2./sqrt_3 * CartQ[0][1]
  return numpy.array([Q20,Q21c,Q21s,Q22c,Q22s])

def norm(x):
  '''Calculates the euclidean norm of x'''
# This is much faster than numpy.linalg.norm()
  n = math.sqrt(numpy.dot(x,x.conj()))
  return x/n

# RC is an array of reference positions with decreasing priority
# AC is a vector with the center of the XYZ system

def Get_local_XYZ(AC,refkind,RC):
  '''Returns the unit vectors of the local coordinate system in terms of the global unit vectors'''
  
#    local_XYZ assignment principle depends on the reference sphere and
#    whether or not the center is terminal:
#
#    internal: if two or three neighbouring atom types are the same, 
#              their indices are the first, second (and third)
#
#              if there are two pairs of neighbouring atoms, the pair with 
#              higher priority according to CIP rules is first
#
#              the priority of all other atoms is according to CIP rules
#
#
#    terminal: First atom is always the nearest neighbour
#                 After that the rules are the same as in the internal case 
#
#    c3v     : Special kind of internal reference axis system where 3
#    neighbours are the same atom type
#
#    linear  : appropriate for linear molecules (e.g., diatomic, acetylene)
#    for which one can't define x and y axes.
  
  nrefA = len(RC)
  if nrefA not in [1,2,3,4]: 
    print ('Number of reference atoms for the current atom is wrong')
    exit(0)

  if refkind == 'c3v':
    if nrefA not in [3,4]:
      print ('Number of reference atoms for the current atom is wrong')
      exit(0)

    # Z for both nrefA 3&4 points outwards or towards RC[3]
    # Y is perpendicular to the plane of 3&C&4 and to the plane of ((1-C)+(2-C))&Z

    if nrefA == 4:
      Z = norm(norm(RC[3]-AC)+3*(norm(norm(AC-RC[2])+norm(AC-RC[1])+norm(AC-RC[0]))))
      Y = norm(numpy.cross(Z,numpy.cross(RC[2]-AC,Z)))            # This does not really matter, because its coefficients should be 0 anyway
      X = numpy.cross(Y,Z)

    elif nrefA == 3:
      D = norm(AC-RC[0])+norm(AC-RC[1])+norm(AC-RC[2])            # tetragonal (not reallly sp2) -> sp3
      Z = norm(numpy.cross(norm(RC[0]-RC[2]),norm(RC[1]-RC[2])))
      if numpy.dot(D,D) > 0 and numpy.dot(D,Z) < 0: Z = (-1)*Z
      Y = norm(numpy.cross(Z,numpy.cross(norm(AC-RC[0])+norm(AC-RC[1])+norm(RC[2]-AC),Z)))
      X = numpy.cross(Y,Z)

    loc_xyz = numpy.array([X,Y,Z])
   
    return numpy.transpose(loc_xyz)
    
  elif refkind == 'ter':

    # Z points from RC[0] to AC
    # nrefA = 4: Y points along RC[3]
    # nrefA = 3: Y points along missing RC[3] or is perpendicular to the plain spanned by RC[0], RC[1] and AC
    # nrefA = 2: Y is perpendicular to the plain spanned by RC[0], RC[1] and AC

    Z = norm(AC-RC[0])
    if nrefA == 4:
      Y = norm(numpy.cross(Z,numpy.cross(norm(RC[3]-RC[0])+norm(RC[0]-RC[1])+norm(RC[0]-RC[2]),Z)))
      if numpy.dot(RC[1]-RC[0],numpy.cross(RC[2]-RC[0],RC[3]-RC[0])) > 0:            # This is the enantiomer check
        X = numpy.cross(Y,Z)
      else:
        X = numpy.cross(Z,Y)
      
    elif nrefA == 3:
      D = norm(RC[0]-AC)+norm(RC[0]-RC[1])+norm(RC[0]-RC[2])            # tetragonal (not reallly sp2) -> sp3
      Y = norm(numpy.cross(Z,numpy.cross(numpy.cross(AC-RC[1],AC-RC[2]),Z)))
      if numpy.dot(D,D) > 0 and numpy.dot(D,Z) < 0: Y = (-1)*Y
      X = numpy.cross(Y,Z)
      if numpy.dot(X,RC[1]-AC) < 0: X = (-1)*X                    # 13/12/11 Changed to this system due to consistency problems

#      if numpy.dot(AC-RC[0],numpy.cross(RC[1]-RC[0],RC[2]-RC[0])) > 0:            # This is the enantiomer check
#        X = numpy.cross(Y,Z)
#      else:
#        X = numpy.cross(Z,Y)
  
    elif nrefA == 2:
      Y = norm(numpy.cross(Z,RC[1]-RC[0]))
      X = norm(numpy.cross(Y,Z))
   
    else:
      print ("Error: Inappropriate number of reference atoms for 'ter' ("+ \
        nrefA+")")
      exit(1)

    loc_xyz = numpy.array([X,Y,Z])

    return numpy.transpose(loc_xyz)
    
  elif refkind == 'int':
  
    # Z for nrefA 4: bisects both angles between 1&2 and 3&4
    # Y for nrefA 4: is perpendicular to the plain of 3&4&C and in the plain of 1&2&C
    if nrefA == 4:
      Z = norm(norm(AC-RC[0])+norm(AC-RC[1])+norm(RC[2]-AC)+norm(RC[3]-AC))
      Y = norm(numpy.cross(Z,numpy.cross(norm(RC[2]-AC)+norm(AC-RC[3]),Z)))
      if numpy.dot(RC[0]-RC[3],numpy.cross(RC[1]-RC[3],RC[2]-RC[3])) > 0:            # This is the enantiomer check
        X = numpy.cross(Y,Z)
      else:
        X = numpy.cross(Z,Y)

    # Z for nrefA 3: is perpendicular to the plain of RC[:]
    # Y for nrefA 3: points towards RC[2]    
    elif nrefA == 3:
      D = norm(AC-RC[0])+norm(AC-RC[1])+norm(AC-RC[2])            # tetragonal (not reallly sp2) -> sp3
      Z = norm(numpy.cross(norm(RC[0]-RC[2]),norm(RC[1]-RC[2])))
      if numpy.dot(D,D) > 0 and numpy.dot(D,Z) < 0: Z = (-1)*Z
      Y = norm(numpy.cross(Z,numpy.cross(norm(AC-RC[0])+norm(AC-RC[1])+norm(RC[2]-AC),Z)))
      X = numpy.cross(Y,Z)
      if numpy.dot(X,RC[0]-AC) < 0: X = (-1)*X                    # 13/12/11 Changed to this system due to consistency problems
      
    # Z for nrefA 2: is perpendicular to the plain of AC, RC[0] and RC[1]
    # Y for nrefA 2: bisects the angle RC[0],AC,RC[1]   
    elif nrefA == 2: 
      Z = norm(numpy.cross(RC[0]-AC,RC[1]-AC))
      Y = norm(numpy.cross(Z,numpy.cross(norm(AC-RC[0])+norm(AC-RC[1]),Z)))
      X = numpy.cross(Y,Z)

    else:
      print ("Error: Inappropriate number of reference atoms for 'int' ("+ \
        str(nrefA)+")")
      exit(1)
  
    loc_xyz = numpy.array([X,Y,Z])
    
    return numpy.transpose(loc_xyz)

  elif refkind == 'lin':
    Z = [0.,0.,0.]
    if nrefA == 1:
      # Z is from reference atom AC to only neighbor
      Z = norm(RC[0]-AC)  
    elif nrefA == 2:
      # Z is between the two neighbors, pointing towards the first one
      Z = norm(RC[0]-RC[1])
    else:
      print ("Error: Inappropriate number of reference atoms for 'lin' ("+ \
        nrefA+")")
      exit(1)
    # Choose X and Y. They are arbitrarily chosen.
    Xtemp = [1.,1.,1.]
    X = numpy.cross(Xtemp,Z)
    if math.sqrt(numpy.dot(X,X.conj())) < 0.000001:
      Xtemp = [-1.,0.,0.]
      X = numpy.cross(Xtemp,Z)
    Y = numpy.cross(X,Z)
    loc_xyz = numpy.array([X,Y,Z])
    return numpy.transpose(loc_xyz)

  else:
    print ("Reference axis system not properly defined for current atom")
    exit(0)

def compare_abs_charges(atom_i,atom_j):
  """Compare two partial atomic charges for identity"""
  if abs(float(atom_i.chrg)) != abs(float(atom_j.chrg)):
    print ("Error. charges between atoms", \
        i.atype,"and",j.atype,"do not match.")
    exit(1)            
    

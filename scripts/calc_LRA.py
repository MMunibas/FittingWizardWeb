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
# This program generates a .pun file with the local reference axis system assigned for each atom

def distribute_charge(atom,chrg):
  """Identifies groups of atoms connected by pi systems 
     and distributes charge assignment among these."""
  global checked
  global hbrdz
  global dchrg
  neighbors = atom.GetNeighbors()
  for at in neighbors:
    if checked[at.GetIdx()] == False and (hbrdz[at.GetIdx()] in ['SP2','SP']):
      checked[at.GetIdx()] = True
      dchrg[at.GetIdx()] += chrg
      distribute_charge(at,chrg)

import sys

###########
# Set defaults

basename = ''
pun = True
boxp = False
bondcheck = True
punxyz = False

###########
# Read Input

for i in range(len(sys.argv)):
  if sys.argv[i] == '-in':
    basename = sys.argv[i+1]
  elif sys.argv[i] == '-lpun':
    pun = False
  elif sys.argv[i] == '-boxp':
    boxp = True
  elif sys.argv[i] == '-nobondcheck':
    bondcheck = False
  elif sys.argv[i] == '-punxyz':
    punxyz = True
  elif sys.argv[i] == '-h':
    print "Usage: python calc_LRA.py -in [base filename] [-lpun] [-boxp] [-punxyz] [-nobondcheck] [-h]"
    print "(Base filename might also end on {.sdf, .log, .pun, .out})"
    exit(0)

if basename == '':
  print "Could not recognize file basename."
  print "Usage: python calc_LRA.py -in [base filename] [-lpun] [-boxp] [-punxyz] [-nobondcheck] [-h]"
  print "(Base filename might also end on {.sdf, .log, .pun, .out})"
  exit(0)

import copy, os, string
import rdkit
from rdkit import Chem
    
if basename[-4:] in ['.sdf','.log','.pun','.out']: basename = basename[:-4]

##############
# Get coordinates from the gaussian output file or the punfile
# The Gaussian output file is supposed to end on .log
# If no Gaussian output file that ends on .log is found, a gaussian output file 
#  that ends on .out is searched. If both are not found, the program is exited.

if punxyz == True:
  import mtp_tools, numpy
  mo = mtp_tools.molecule()
  mo.readfromrawpunfile(basename+'.pun')
  xyzblock = []
  for atom in mo.atoms: xyzblock.append([atom.atype, float(atom.coords[0]), float(atom.coords[1]), float(atom.coords[2])])
else:
  outfiles = []
  outfile  = ''
  if os.path.exists(basename+'.log'):
    outfiles.append(basename+'.log')
  elif os.path.exists(basename+'.out'):
    outfiles.append(basename+'.out')

  for fi in outfiles:
    f = open(fi,'r')
    lines = f.readlines()
    for line in lines:
      if " Entering Gaussian System" in line:
        outfile = fi
    f.close()
  if outfile == '':
    print "Cannot find Gaussian output file", \
      basename+".log or",basename+'.out.'
    print "Program exiting"
    exit(0)

  f = open(outfile,'r')
  gin = f.readlines()
  f.close()
# This extracts the bonding block (unless the molecule is super super large)
  gin = gin [-400:-6]
  for i in range(len(gin)):
    if 'l9999.exe' in gin[i]: break
  if i == len(gin):
    print "Have not been able to extract the bonding block from the Gaussian output file for ",basename
    print "Exiting"
    exit(0)
  gin = gin[i+1:]
  xyzblock = ''
  for line in gin: xyzblock = xyzblock+line.strip()
  xyzblock = xyzblock.split('\\')
  for i in range(len(xyzblock)):
    xyzblock[i] = xyzblock[i].replace(' ','')
  for i in range(len(xyzblock)):
    if xyzblock[i][0:7] == "Version": break
  xyzblock = xyzblock[16:i-1]
  for i in range(len(xyzblock)):
    xyzblock[i] = xyzblock[i].split(',')
    lenblock = len(xyzblock[i])
    xyzblock[i][1] = float(xyzblock[i][lenblock-3])
    xyzblock[i][2] = float(xyzblock[i][lenblock-2])
    xyzblock[i][3] = float(xyzblock[i][lenblock-1])

##########
# Read .sd File & update H-bond information if necessary

f = open(basename+'.sdf','r')
orig_sdf = f.readlines()
f.close()
natoms = int(orig_sdf[3][0:3])
nbonds = int(orig_sdf[3][3:6])
bonds = orig_sdf[4+natoms:4+natoms+nbonds]
fchrg = [0]*natoms
if orig_sdf[4+natoms+nbonds][0:6] == 'M  CHG':
  chrg_line = orig_sdf[4+natoms+nbonds].split()
  for i in range((len(orig_sdf[4+natoms+nbonds].split())-3)/2): fchrg[int(chrg_line[3+2*i])-1] = int(chrg_line[4+2*i])

if len(xyzblock) <> natoms:
  print "Number of atoms in the original SD-File and the Gaussian output file are different for ",basename
  print "LRA-assignment stopped."
  print len(xyzblock),natoms
  exit(1)

for i in range(len(xyzblock)):
  if xyzblock[i][0] == 'H':
# find closest atom
    dist = 100
    closest_atom = i
    for j in range(len(xyzblock)):
      if j == i: continue    
      d = (xyzblock[i][1]-xyzblock[j][1])**2+(xyzblock[i][2]-xyzblock[j][2])**2+(xyzblock[i][3]-xyzblock[j][3])**2
      if d < dist:
        dist = d
        closest_atom = j+1
# check whether closest atom is bond partner and replace information if necessary
    for j in range(len(bonds)):
      if int(bonds[j][0:3]) == i+1:
        if int(bonds[j][3:6]) == closest_atom:
          break
        else:
          previous_partner = int(bonds[j][3:6])
          print 'SD-update in',basename,': Bond between ',i+1,'and',previous_partner,'replaced by bond between',i+1,'and',closest_atom
          a = bonds[j][0:5]+str(closest_atom)+bonds[j][6:]
          if closest_atom > 9: a = bonds[j][0:4]+str(closest_atom)+bonds[j][6:]
          if closest_atom > 99: a = bonds[j][0:3]+str(closest_atom)+bonds[j][6:]
          bonds[j] = a
          fchrg[previous_partner-1] -= 1
          fchrg[closest_atom-1] += 1
          break
      if int(bonds[j][3:6]) == i+1:
        if int(bonds[j][0:3]) == closest_atom:
          break
        else:
          previous_partner = int(bonds[j][0:3])
          print 'SD-update: Bond between ',i+1,'and',previous_partner,'replaced by bond between',i+1,'and',closest_atom
          a = '  '+str(closest_atom)+bonds[j][3:]
          if closest_atom > 9: a = ' '+str(closest_atom)+bonds[j][3:]
          if closest_atom > 99: a = str(closest_atom)+bonds[j][3:]
          bonds[j] = a
          fchrg[previous_partner-1] -= 1
          fchrg[closest_atom-1] += 1
          break
## Write updated SD-File to output       
#  f = open(basename+'_n.sdf','w')
#  for i in range(4): f.write(orig_sdf[i])
#
#  for i in range(natoms): 
#    astring = '%10.4f'%xyzblock[i][1]
#    astring = astring + '%10.4f'%xyzblock[i][2]
#    astring = astring + '%10.4f'%xyzblock[i][3]
#    astring = astring + orig_sdf[4+i][30:33]
#    f.write(astring)
#
#  for i in range(nbonds): f.write(bonds[i])
#
#  if natoms-charge.count(0) > 0:
#    chargeline = 'M  CHG  '+str(natoms-charge.count(0))
#    for i in range(len(charge)):
#      if charge[i] <> 0: chargeline = chargeline+'  '+str(i)+'  '+str(charge[i])
#    chargeline = chargeline+'\n'
#    f.write(chargeline)
#
#  for i in range(len(orig_sdf)-5-natoms-nbonds): f.write(orig_sdf[5+natoms+nbonds+i])
#
#  f.close()

###########
# Read .sd File using RDkit
try:
  mol = Chem.MolFromMolFile(basename+'.sdf',removeHs=False)
except:
  print 'Cannot read SD File ',basename,'.sdf properly. Program exiting.'
  exit(0)

if natoms < 2:
  print "Problem with ",basename
  print "Currently only molecules with 2 or more atoms can be handled. Exiting"
  exit(0)

###########
# Atom type each atom

atypes = [''] * natoms
atoms = mol.GetAtoms()
hbrdz = []
for atom in atoms: hbrdz.append(str(atom.GetHybridization()).split('.')[-1])

##########
# Get neighbours and second neighbours, assign terminal atoms
neighbour_atoms = [[]]*natoms
for bond in bonds:
   bpartners = [bond[0:3],bond[3:6]]
   neighbour_atoms[int(bpartners[0])-1] = neighbour_atoms[int(bpartners[0])-1]+[int(bpartners[1])-1]
   neighbour_atoms[int(bpartners[1])-1] = neighbour_atoms[int(bpartners[1])-1]+[int(bpartners[0])-1]

terminal = [False]*natoms
for i in range(natoms):
   if len(neighbour_atoms[i]) == 1: 
     terminal[i] = True
# Terminal atoms on SP hybridized atoms are not assigned SP hybridization in RDkit. So this has to be added here
     if hbrdz[neighbour_atoms[i][0]] == 'SP': hbrdz[i] = 'SP'

nextneighbours = [[]]*natoms
for i,j in enumerate(terminal):
   if j == True:
     nextneighbours[i] = copy.copy(neighbour_atoms[neighbour_atoms[i][0]])
     nextneighbours[i].remove(i)

##########
# Atom type Amides and aromatics (the others as well, but this is removed later on)

smartsf = open(__file__[:__file__.rfind("/")] + '/essential_atom_types.txt','r')
etypes = []
lines = smartsf.readlines()
for line in lines:
    if line[0] == '#': continue
    line = line.split()
    if len(line) < 2: continue
    etypes.append(dict(type=line[0],smarts=line[1],molsmarts=Chem.MolFromSmarts(line[1])))


smartsf.close()
atypes = [''] * natoms
priorities = [0] * natoms
for i,tp in enumerate(etypes):
    for at in mol.GetSubstructMatches(tp["molsmarts"],uniquify=False):
        if atypes[at[0]] == '': 
          atypes[at[0]] = tp["type"]
          priorities[at[0]] = i

############
# Finish Atom typing and assignment of priorities
for i in range(natoms):
  if atypes[i][0] in ['C','N','O','P','S'] and atypes[i][1:] not in ['ar','am','l']: atypes[i] = atypes[i][0]+str(len(neighbour_atoms[i]))

for i,tp in enumerate(etypes):
  for j in range(natoms):
    if atypes[j] == tp["type"]: priorities[j] = i

############
# Add charges and distribute them across conjugated systems.
# This is done based on the old SD File (before Gaussian), naively
#  assuming that Conjugation does not change during optimization.
#
# 1: Calculate formal charge on each atom
# 2: Calculate hybridization type on each atom (Hs will be unspecified)
# 3: Distribute charge to all conjugated atoms (i.e. atoms which are 
#    connected through a network of non-sp3 hybridization)

dchrg = copy.copy(fchrg)
for i,atom in enumerate(atoms):
  checked = [False] * natoms
  checked[i] = True
  if fchrg[i] <> 0 and (hbrdz[i] in ['SP2','SP']): 
    distribute_charge(atom,fchrg[i])

for i in range(len(dchrg)):
  if dchrg[i] > 0:
    atypes[i] = atypes[i] + dchrg[i]*'+' 
  elif dchrg[i] < 0:
    atypes[i] = atypes[i] + (-1)*dchrg[i]*'-'

# Initialize lin assignment (in case SP search below fails.)
# If we have a diatomic molecule, use the lin atom tyep

lin = [False]*natoms
if natoms == 2:
  lin = [True]*natoms

# Special treatment for SP-hybridised Atoms: go along the chain until the end is reached (terminal =  True) 
# or Non SP-hybridised atoms are found
#
# SP atoms are assigned terminal reference spheres by default

SP_name_atoms = ['']*natoms
SP_neighbour_atoms = ['']*natoms
SP_nextneighbours = ['']*natoms

for i in range(natoms):
  if hbrdz[i] <> 'SP': continue
  if lin[i]: continue
  if terminal[i] == True:
    a = copy.copy(neighbour_atoms[i][0])
    SP_name_atoms[i] = [a,nextneighbours[i][0]]
    SP_neighbour_atoms[i] = copy.copy([a])
    excl_list = [i,a]
    while True:
      b = copy.copy(neighbour_atoms[a])
      for j in b: 
        if j in excl_list: b.remove(j)
      if b == []:
        lin[i] = True
        break
      if hbrdz[b[0]] == 'SP':
        excl_list.append(b[0])
        a = b[0]
        continue
      else:
        break
    if lin[i]: continue
    SP_nextneighbours[i] = copy.copy(neighbour_atoms[b[0]])
    for j in SP_nextneighbours[i]: 
      if j in excl_list: SP_nextneighbours[i].remove(j)
  else:
    a1 = copy.copy(neighbour_atoms[i][0])
    a2 = copy.copy(neighbour_atoms[i][1])
    if priorities[a1] < priorities[a2]:
      SP_name_atoms[i] = [a1,a2]
    else:
      SP_name_atoms[i] = [a2,a1]
    def_found = False
    excl_list = [i,a1,a2]
    a = a1
    while True:
      if terminal[a] == True:
        break
      elif hbrdz[a] <> 'SP':
        def_found = True
        SP_neighbour_atoms[i] = copy.copy([a])
        SP_nextneighbours[i] = copy.copy(neighbour_atoms[a])
        for j in SP_nextneighbours[i]:
          if j in excl_list: SP_nextneighbours[i].remove(j)
        break
      else:
        a = copy.copy(neighbour_atoms[a])
        for j in a: 
          if j in excl_list: a.remove(j)
        if a == []:
          lin[i] = True
          break
        else:
          a = a[0]
          excl_list.append(a)
    a = a2
    while True:
      if def_found == True: break
      if terminal[a] == True: 
        # Tag atom as lin
        lin[i] = True
        break
      elif hbrdz[a] <> 'SP':
        def_found = True
        SP_neighbour_atoms[i] = copy.copy([a])
        SP_nextneighbours[i] = copy.copy(neighbour_atoms[a])
        for j in SP_nextneighbours[i]:
          if j in excl_list: SP_nextneighbours[i].remove(j)
        break
      else:
        a = copy.copy(neighbour_atoms[a])
        for j in a: 
          if j in excl_list: a.remove(j)
        if a == []:
          lin[i] = True
          break
        else:
          a = a[0]
          excl_list.append(a)

for i in range(natoms):
  if hbrdz[i] == 'SP' and not lin[i]: 
    terminal[i] = True
    neighbour_atoms[i] = SP_neighbour_atoms[i]
    nextneighbours[i] = SP_nextneighbours[i]

###########
# Adjust priorities for charge: Priorities: negative charge > 0 > positive charge

if max(dchrg) > 10 or min(dchrg) < (-10): 
  print "Absolute distributed charges have become very large."
  print " Please check your molecule"
  exit(0) 

for i in range(len(dchrg)):
  priorities[i] = priorities[i] + dchrg[i] * 0.01

###########
# Initialize c3v assignment

c3v = [False]*natoms

###########
# Sort nearest neighbors according to priority and assign reference atoms by idx for each atom

priorised_neighbours = ['']*natoms
for i in range(natoms):
  if terminal[i] == False:
    neighbour_priorities = []
    for neighbour in neighbour_atoms[i]: neighbour_priorities.append(priorities[neighbour])
    prio_count = []
    for j in neighbour_priorities: prio_count.append(neighbour_priorities.count(j))
    # 'lin' type. Non-terminal can be defined by two atoms.
    if lin[i]:
      pri_neigh = []
      for j in range(min(2,len(neighbour_atoms[i]))):
        pri_neigh.append(neighbour_atoms[i][j])
      priorised_neighbours[i] = pri_neigh
    # Three neighbour atoms of the same kind
    elif max(prio_count) == 3:
      c3v[i] = True
      pri_neigh = []
      for j,count in enumerate(prio_count):
        if count == 3: pri_neigh.append(neighbour_atoms[i][j])
      for j,count in enumerate(prio_count):
        if count == 1: pri_neigh.append(neighbour_atoms[i][j])
      priorised_neighbours[i] = pri_neigh
    # Two * two neighbour atoms of the same kind
    elif max(prio_count) == 2 and min(prio_count) == 2 and len(prio_count) == 4:
      pri_neigh = []
      a = min(neighbour_priorities)
      for j,prio in enumerate(neighbour_priorities):
        if prio == a: pri_neigh.append(neighbour_atoms[i][j])
      for j,prio in enumerate(neighbour_priorities):
        if prio <> a: pri_neigh.append(neighbour_atoms[i][j])
      priorised_neighbours[i] = pri_neigh
    # Two neighbour atoms overall, both of the same kind
    elif max(prio_count) == 2 and len(prio_count) == 2:
      priorised_neighbours[i] = [neighbour_atoms[i][0],neighbour_atoms[i][1]]
    # Three or four neighbour atoms with two of them of the same kind
    elif max(prio_count) == 2 and min(prio_count) < 2:
      pri_neigh = []
      for j,count in enumerate(prio_count):
        if count == 2: pri_neigh.append(neighbour_atoms[i][j])
      neighbour_priorities = [neighbour_priorities[j] for j,k in enumerate(prio_count) if k < 2]
      rest_atoms = [neighbour_atoms[i][j] for j,k in enumerate(prio_count) if k < 2]
      pri_neigh.append(rest_atoms[neighbour_priorities.index(min(neighbour_priorities))])
      if len(neighbour_atoms[i]) == 4: pri_neigh.append(rest_atoms[neighbour_priorities.index(max(neighbour_priorities))])
      priorised_neighbours[i] = pri_neigh
    # All neighbour atoms different
    else:
      pri_neigh = []
      next_atoms = list(neighbour_atoms[i])
      while len(neighbour_priorities) > 0:
        pri_neigh.append(next_atoms[neighbour_priorities.index(min(neighbour_priorities))])
        del next_atoms[neighbour_priorities.index(min(neighbour_priorities))]
        del neighbour_priorities[neighbour_priorities.index(min(neighbour_priorities))]
      priorised_neighbours[i] = pri_neigh

for i,atom in enumerate(atoms):
  if terminal[i] == True:
    # The single nearest neighbour has the highest priority
    pri_neigh = [neighbour_atoms[i][0]]
    neighbour_priorities = []
    for neighbour in nextneighbours[i]: neighbour_priorities.append(priorities[neighbour])
    prio_count = []
    for j in neighbour_priorities: prio_count.append(neighbour_priorities.count(j)) 
    # 'lin' type. Terminal only defined by one neighbor.
    if lin[i]:
      priorised_neighbours[i] = pri_neigh
    # Three second neighbour atoms of the same kind
    elif max(prio_count) == 3:
      pri_neigh.append(nextneighbours[i][0])
      pri_neigh.append(nextneighbours[i][1])
      pri_neigh.append(nextneighbours[i][2])
      priorised_neighbours[i] = pri_neigh
    # Two second neighbour atoms of the same kind and no other second neighbour atom
    elif max(prio_count) == 2 and len(prio_count) == 2:
      pri_neigh.append(nextneighbours[i][0])
      pri_neigh.append(nextneighbours[i][1])
      priorised_neighbours[i] = pri_neigh
    # Two second neighbour atoms of the same kind and one more atom in the second sphere
    elif max(prio_count) == 2 and len(prio_count) == 3:
      for j,count in enumerate(prio_count): 
        if count == 2: pri_neigh.append(nextneighbours[i][j])
      pri_neigh.append(nextneighbours[i][prio_count.index(1)])
      priorised_neighbours[i] = pri_neigh
    # All second neighbours of different kinds
    else:
      next_atoms = list(nextneighbours[i])
      while len(neighbour_priorities) > 0:
        pri_neigh.append(next_atoms[neighbour_priorities.index(min(neighbour_priorities))])
        del next_atoms[neighbour_priorities.index(min(neighbour_priorities))]
        del neighbour_priorities[neighbour_priorities.index(min(neighbour_priorities))]
      priorised_neighbours[i] = pri_neigh

############
# Assemble Composite atomtype Names

com_atypes = []
for i in range(len(atypes)):
  com_atype = atypes[i]
  if hbrdz[i] <> 'SP':
    for j in priorised_neighbours[i]:com_atype = com_atype + atypes[j]
  else:
    com_atype = com_atype + atypes[SP_name_atoms[i][0]] + atypes[SP_name_atoms[i][1]]
  com_atypes.append(com_atype)

###########
# Write output file

if pun == True:
  f = open(basename+'.pun','r')
  ol_punf = f.readlines()
  f.close()
  f = open(basename+'_l.pun','w')
  k=0
  header = True
  while k <= len(ol_punf) and header:
    line = ol_punf[k].split()
    if len(line) >= 6 and line[4] == "Rank" \
      and line[0] not in ['#','!']:
      header = False
    k += 1

  for i in range(k-1): f.write(ol_punf[i])

  for i in range(len(atoms)):
    line = ol_punf[k-1][2:]
    line = com_atypes[i]+line
    f.write(line)
    line = line.split()
    rnk = line[-1]
    if rnk == '0':
      for j in range(1): f.write(ol_punf[k+j])
      k = k + 3
    elif rnk == '2':
      for j in range(3): f.write(ol_punf[k+j])
      k = k + 5
    else:
      print "Error. Rank not supported ("+rnk+"). Exiting"
      print line
      exit(1)
    f.write('\n')
  f.write('LRA:\n')
  for i in range(len(atoms)):
    if lin[i]:
      f.write('lin')
    elif terminal[i] == True:
      f.write('ter')
    elif c3v[i] == True:
      f.write('c3v')
    else:
      f.write('int')
    for j in priorised_neighbours[i]: f.write('  '+str(j+1))
    f.write('\n')
  f.write('\n')
  f.close()

if pun == False:
  import mtp_tools, math, numpy
  mo = mtp_tools.molecule()
  mo.readfromrawpunfile(basename+'.pun')
  for i in range(natoms):
    mo.atoms[i].refkind = 'int'
    if terminal[i] == True: mo.atoms[i].refkind = 'ter'
    if c3v[i] == True: mo.atoms[i].refkind = 'c3v'
    for j in range(len(priorised_neighbours[i])): mo.atoms[i].refatms.append(priorised_neighbours[i][j]+1)
  mo.Calc_locMTP()
# Check bondlengths
  if bondcheck == True:
    for i in range(len(mo.atoms)):
      if mo.atoms[i].refkind <> 'ter':
        for j in range(len(mo.atoms[i].refatms)):
          vect = mo.atoms[i].coords - mo.atoms[mo.atoms[i].refatms[j]-1].coords
          dist = numpy.dot(vect,vect)
# 5.3 corresponds to 2.3 Angstroems (C-I bonds often have 2.1 Angstroems, it is annoying to see these in the warnings)
          if dist > 5.3:
            print 'Bondlength warning for molecule:',basename
            print 'Distance between atom',str(i+1),'(',atypes[i],') and its reference atom',str(mo.atoms[i].refatms[j]),'(',atypes[mo.atoms[i].refatms[j]-1],') is',str(math.sqrt(dist)),'Angstroem'
            print
  f = open(basename+'.lpun','w')    
  f.write("! "+basename+"; Distributed multipoles rotated to local reference axis system.\n")
  f.write("! Multipoles were obtained from "+basename+".pun. File was written by calc_lra.py.\n")
  g = open(basename+'.pun','r')  
  for i in range(2): wrd = g.readline()
  g.close()
  f.write(wrd+'\n')
  for i in range(len(atoms)):
    f.write(com_atypes[i]+'  '+str(i+1))
    for j in range(3): f.write('  '+str(mo.atoms[i].coords[j]))
    if boxp == False:
      f.write('  Rank  '+str(mo.atoms[i].rank)+'\n')
    elif len(neighbour_atoms[i]) == 3 and terminal[i] == False:
# Calculate the box product as a scaling factor for the dipoles on internal pyramidal nitrogens
      AC = mo.atoms[i].coords
      RC0 = mo.atoms[mo.atoms[i].refatms[0]-1].coords
      RC1 = mo.atoms[mo.atoms[i].refatms[1]-1].coords
      RC2 = mo.atoms[mo.atoms[i].refatms[2]-1].coords
      R0 = (RC0-AC)/math.sqrt(numpy.dot((RC0-AC),(RC0-AC).conj()))
      R1 = (RC1-AC)/math.sqrt(numpy.dot((RC1-AC),(RC1-AC).conj()))
      R2 = (RC2-AC)/math.sqrt(numpy.dot((RC2-AC),(RC2-AC).conj()))
      boxp = numpy.dot(R0,numpy.cross(R1,R2))
      f.write('  Rank  '+str(mo.atoms[i].rank)+'  '+str(boxp)+'\n')
    else:
      f.write('  Rank  '+str(mo.atoms[i].rank)+'\n')
    f.write('LRA:  '+mo.atoms[i].refkind)
    for j in mo.atoms[i].refatms: f.write('  '+str(j))
    f.write('\n')
    f.write(str(float(mo.atoms[i].chrg))+'\n')
    for j in mo.atoms[i].dloc: f.write(str(j)+'  ')
    f.write('\n')
    for j in mo.atoms[i].Qloc: f.write(str(j)+'  ')
    f.write('\n\n')
  f.close()
  

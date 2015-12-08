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
# This program prepares a table which can be used for fitting atomic point
#  multipoles. All values are used in atomic units unless stated otherwise
#

import sys,math
import numpy as np

#############
# Set Parameters

a2b = 1.889726
b2a = 0.52917720859
au2KjMol = 2625.5

#############
# Set Defaults

cubefile = ''
vdwfile = ''
punfile = ''
bondincr = False
si = 1.66
so = 2.2
no_sym = False

#############
# Read command line input

for i in range(len(sys.argv)):
  if sys.argv[i] == '-cube':
    cubefile = sys.argv[i+1]
  if sys.argv[i] == '-vdw':
    vdwfile = sys.argv[i+1]
  if sys.argv[i] == '-pun':
    punfile = sys.argv[i+1]
  if sys.argv[i] == '-sdf':
    sdfile = sys.argv[i+1]
  if sys.argv[i] == '-si':
    si = float(sys.argv[i+1])
  if sys.argv[i] == '-so':    
    so = float(sys.argv[i+1])
  if sys.argv[i] == '-no_sym':
    no_sym = True
  if sys.argv[i] == '-bondincr':
    bondincr = True
  elif sys.argv[i] == '-h':
    print "Usage: python mtp_fittab_maker -cube [file] -vdw [file] -pun [file]",\
          "[-si [#]] [-so [#]] [-no_sym] [-bondincr]"
    exit(0)

if cubefile == '' or vdwfile == '' or punfile == '':
  print "Could not recognize file basename."
  print "Usage: python mtp_fittab_maker -cube [file] -vdw [file] -pun [file]",\
        "[-si [#]] [-so [#]] [-no_sym] [-bondincr]"
  exit(0)

if bondincr == True: 
  print "Reference atoms will be used to ditribute bond increments"

############
# 1.) Read cube input file

try:
  f = open(cubefile,'r')
except:
  print "Could not find the -cube input file. Exiting..."
  exit(0)

print "Reading ESP input file"  

a = f.readline()
a = f.readline()

a = f.readline()
a = a.split()
natoms=int(a[0])
xstart=float(a[1])
ystart=float(a[2])
zstart=float(a[3])

a = f.readline()
a = a.split()
xpts = int(a[0])
step_x = float(a[1])

a = f.readline()
a = a.split()
ypts = int(a[0])
step_y = float(a[2])

a = f.readline()
a = a.split()
zpts = int(a[0])
step_z = float(a[3])

coords=[]
for i in range(natoms):
  a = f.readline()
  a = a.split()
  coords.append(np.array([float(a[2]),float(a[3]),float(a[4])]))

a = f.readlines()
f.close()

ESP_raw = []
for line in a:
  line = line.split()
  for item in line: ESP_raw.append(float(item))

###########
# Calculate Grid coordinates

grid_coords_raw = [0.0 for i in range(xpts*ypts*zpts)]
for i in range(xpts):
  for j in range(ypts):
    for k in range(zpts):
      grid_coords_raw[i*ypts*zpts+j*zpts+k] = np.array([xstart+i*step_x,ystart+j*step_y,zstart+k*step_z])
#      grid_coords_raw.append(np.array([xstart+i*step_x,ystart+j*step_y,zstart+k*step_z]))

###########
# 2.) Read .pun file

print "Reading .pun file"
# Check that there's the LRA definition in the file
fpun = open(punfile,'r')
rpun = fpun.readlines()
fpun.close()
lrafound = False
for line in rpun:
  line = line.split()
  if len(line) > 0:
    if line[0] not in ['!','#'] and "LRA:" in line:
      lrafound = True
if lrafound == False:
  print "Error. No LRA definition in",punfile
  print "Use calc_LRA.py to generate a *_l.pun file."
  exit(1)
import mtp_tools
mol = mtp_tools.molecule()
if punfile[-4:] == '.pun':
  mol.readfrompunfile(punfile)
  mol.Calc_locMTP()
elif punfile [-5:] == '.lpun':
  mol.readfromlpunfile(punfile)
  mol.Calc_gloMTP()
else:
  print ("Was not able to recognize extension of punfile. Exiting...\n")
  exit(0)

###########
# Update XYZ coordinates with coordinates from .cube file

for i,atom in enumerate(mol.atoms): atom.coords = coords[i]

###########
# 3.) Read .vdw file and update vdw Parameters

print "Reading .vdw file"
f = open(vdwfile,'r')
a = f.readlines()
f.close()

for i,atom in enumerate(mol.atoms): atom.vdw_radius = float(a[3*i+1])

##########
# 4.) Sort out grid points that are not in the desired interaction belt

print "Identifying relevant grid points"

grid_coords = []
ESP = []
for i,gridpt in enumerate(grid_coords_raw):
  inside = False
  belt = False
  for atom in mol.atoms:    
    dist = np.dot(atom.coords-gridpt,atom.coords-gridpt)
    if dist < (so * atom.vdw_radius)**2 : 
      belt = True
      if dist < (si * atom.vdw_radius)**2 : inside = True
  if belt == True and inside == False:
    grid_coords.append(gridpt)
    ESP.append(ESP_raw[i])

###########
# 5.) Allocate Array with dimensions npoints, 9*natomtypes.(and (natomtypes*natomtypes-1)/2 for the bond increments)

# First: get array of atom types
atypes = []
for atom in mol.atoms:
  atypes.append(atom.atype)
atypes = list(set(atypes))
atypes.sort()

xblock = np.array([[0.0 for i in range(9*len(atypes))] for j in range(len(grid_coords))])

excl_from_tab = []
if no_sym == False:
  # Use symmetry to discard some multipole moments.
  # These will not be included in the output table.
  for i in range(len(mol.atoms)):
    for j in range(3):
      mol.atoms[i].SetLocMTP('dloc',j,1.)
    for j in range(5):
      mol.atoms[i].SetLocMTP('Qloc',j,1.)
  mol.set_coefficients_zero_due_to_symmetry()
  for i in range(len(mol.atoms)):
    for j in range(3):
      if mol.atoms[i].dloc[j] == 0.:
        excl_id = atypes.index(mol.atoms[i].atype) * 9 + 1+j
        if excl_id not in excl_from_tab:
          excl_from_tab.append(excl_id)
    for j in range(5):
      if mol.atoms[i].Qloc[j] == 0.:
        excl_id = atypes.index(mol.atoms[i].atype) * 9 + 4+j
        if excl_id not in excl_from_tab:
          excl_from_tab.append(excl_id)

###########
# 6.) Fill array with data:

print "Calculating Multipole contribution terms"

for atom in mol.atoms:
  RC = []
  for j in atom.refatms: RC.append(mol.atoms[j-1].coords)
  TM = mtp_tools.Get_local_XYZ(atom.coords,atom.refkind,RC)    # TM is the local XYZ written in the coordinates of the global XYZ
  rotM = np.linalg.inv(TM)       # rotM is the rotation matrix that rotates vectors from the global to the local XYZ
  atidx = atypes.index(atom.atype)
  for i,point in enumerate(grid_coords):
    vect_raw = point-atom.coords
# Rotate vector to local coord system
    vect = np.dot(rotM,vect_raw)
    dist = math.sqrt(np.dot(vect,vect))
    uv_vect = vect/dist    
# Add contribution according to charge
    xblock[i][0+atidx*9] = xblock[i][0+atidx*9] + (1./dist)
# Add contribution according to Q10
    xblock[i][1+atidx*9] = xblock[i][1+atidx*9] + ((uv_vect[2]/(dist**2)))
# Add contribution according to Q11c
    xblock[i][2+atidx*9] = xblock[i][2+atidx*9] + ((uv_vect[0]/(dist**2)))
# Add contribution according to Q11s
    xblock[i][3+atidx*9] = xblock[i][3+atidx*9] + ((uv_vect[1]/(dist**2)))
# Add contribution according to Q20
    xblock[i][4+atidx*9] = xblock[i][4+atidx*9] + (((3.*((uv_vect[2])**2)-1)*0.5)/(dist**3))
# Add contribution according to Q21c
    xblock[i][5+atidx*9] = xblock[i][5+atidx*9] + ((math.sqrt(3.)*uv_vect[0]*uv_vect[2])/(dist**3))
# Add contribution according to Q21s
    xblock[i][6+atidx*9] = xblock[i][6+atidx*9] + ((math.sqrt(3.)*uv_vect[1]*uv_vect[2])/(dist**3))
# Add contribution according to Q22c
    xblock[i][7+atidx*9] = xblock[i][7+atidx*9] + ((0.5*math.sqrt(3)*((uv_vect[0]**2)-(uv_vect[1]**2)))/(dist**3))
# Add contribution according to Q22s
    xblock[i][8+atidx*9] = xblock[i][8+atidx*9] + ((math.sqrt(3)*uv_vect[0]*uv_vect[1])/(dist**3))

if bondincr == True:
  print "Calculating Bond Increments"
# Generate List of all neighbour pairs
  neigh_pairs = []
  for atom in mol.atoms:
    if atom.refkind == 'ter':
      j = atom.refatms[0]
      b = [atom.idx,j]
      if atom.atype < mol.atoms[j-1].atype:
        neigh_pairs.append(str(b[0])+':'+str(b[1]))
      else:
        neigh_pairs.append(str(b[1])+':'+str(b[0]))
    else:
      for j in atom.refatms:
        b = [atom.idx,j]
        if atom.atype < mol.atoms[j-1].atype:
          neigh_pairs.append(str(b[0])+':'+str(b[1]))
        else:
          neigh_pairs.append(str(b[1])+':'+str(b[0]))
  neigh_pairs = list(set(neigh_pairs))
# Initialize block with bond increment data
  incr_block = np.array([[0.0 for i in range(int(len(atypes)*(len(atypes)-1)*0.5))] for j in range(len(grid_coords))])
# Generate Header and index for bond increments
  incr_header = []
  for i in range(len(atypes)-1):
    for j in range(i+1,len(atypes)):
      incr_header.append('BInc_'+atypes[i]+':'+atypes[j])
# For each point: calculate contribution to ESP according to bond increments
  for i,point in enumerate(grid_coords):
    for entry in neigh_pairs:
      if mol.atoms[int(entry[:entry.index(':')])-1].atype == mol.atoms[int(entry[entry.index(':')+1:])-1].atype: continue
      d1 = math.sqrt(np.dot(point-mol.atoms[int(entry[:entry.index(':')])-1].coords,point-mol.atoms[int(entry[:entry.index(':')])-1].coords))
      d2 = math.sqrt(np.dot(point-mol.atoms[int(entry[entry.index(':')+1:])-1].coords,point-mol.atoms[int(entry[entry.index(':')+1:])-1].coords))
      incr_block[i][incr_header.index('BInc_'+mol.atoms[int(entry[:entry.index(':')])-1].atype+':'+mol.atoms[int(entry[entry.index(':')+1:])-1].atype)] += ((1/d1)-(1/d2))
  # Postprocess bond increments: exclude those that give 0 contribution
  # in entire column and those that are between same atom types
  bi_exclude = []
  for i in range(len(incr_header)):
    exclude = True
    for j,point in enumerate(grid_coords):
      if incr_block[j][i] <> 0.0:
        exclude = False
        break
      if exclude == True:
        bi_exclude.append(i)

print "Writing output"

#############
#7.) write table to output

# Therefore: calculate total charge and count atypes
tot_chrg = 0.0
for atom in mol.atoms: tot_chrg += atom.chrg
tot_chrg = int(round(tot_chrg))

atype_count = [0 for i in range(len(atypes))]
for atom in mol.atoms: atype_count[atypes.index(atom.atype)] += 1

suffix = ['_Q00 ','_Q10 ','_Q11c ','_Q11s ','_Q20 ','_Q21c ','_Q21s ','_Q22c ','_Q22s ']
f = open(cubefile[:-5]+'_mtpfittab.txt','w')
header = 'PtID '
if bondincr == True:
  if tot_chrg == 0:
    for i in range(len(atypes)):
      for j in range(1,len(suffix)):
        if i*9 + j not in excl_from_tab:
          header = header+atypes[i]+suffix[j]
  else:
    for i in range(len(atypes)):
      for j in range(1,len(suffix)):
        if i*9 + j not in excl_from_tab:
          header = header+atypes[i]+suffix[j]
  incr_index = 0
  for i in incr_header:
    if incr_index not in bi_exclude: header = header+i+' '
    incr_index += 1
else:
  for i in range(len(atypes)):
    for j in range(len(suffix)):
      if i*9 + j not in excl_from_tab:
        header = header+atypes[i]+suffix[j]

header = header + 'aiESP\n'
f.write(header)

for i in range(len(grid_coords)):
  line = 'Pt:'+str('%f' %grid_coords[i][0])+'_'+str('%f' %grid_coords[i][1])+'_'+str('%f' %grid_coords[i][2])+'_'+cubefile[:-5]+' '
  if bondincr == True:
    if tot_chrg == 0:
      for j in range(len(atypes)*9):
        if j%9 > 0 and j not in excl_from_tab: line = line + str(xblock[i][j]) + ' '
    else:
      for j in range(len(atypes)*9) and \
          j not in excl_from_tab:line = line + str(xblock[i][j]) + ' '
    for j in range(len(incr_block[i])):
      if j not in bi_exclude: line = line + str(incr_block[i][j]) + ' '
  else:
    for j in range(len(xblock[i])):
      if j not in excl_from_tab: line = line + str(xblock[i][j]) + ' '
  line = line + str(ESP[i])+'\n'
  f.write(line)

#############
# Add line for charge Control

prefactor = 1
if bondincr == True and tot_chrg <> 0:
  line = 'ChgRegulation_'+cubefile[:-5]+' '
  for j in range(len(atypes)*9):
    if j%9 == 0: 
      line = line + str(atype_count[j/9]*prefactor) + ' '
    elif j not in excl_from_tab:
      line = line + '0.0 '
  for j in range(len(incr_block[i])): line = line + '0.0 '
  line = line + str(tot_chrg*prefactor) + '\n'
  f.write(line)
elif bondincr == False:
  line = 'ChgRegulation_'+cubefile[:-5]+' '
  for j in range(len(atypes)*9):
    if j%9 == 0: 
      line = line + str(atype_count[j/9]*prefactor) + ' '
    elif j not in excl_from_tab:
      line = line + '0.0 '
  line = line + str(tot_chrg*prefactor) + '\n'
  f.write(line)

f.close()

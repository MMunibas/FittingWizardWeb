#!/usr/bin/env python
# This program generates a line/table with all Atom-Atom contributions to the Lennard Jones Energy between two fragments
#
# The atom order for each fragment is read from the .rtf file
# The Atom types are obtained either from the .rtf file or from the .lpun file
# The number of fragments and the coordinates are read from a CHARMM .pdb file
#
import os,sys

rtf = []
lpun = ''
lpun_atype = False
pdb = ''
LJexp = [12,6]
atyp = []

def usage():
  print "Usage: python LJ_Tab.py -rtf file.rtf [file2 [...]] -pdb file.pdb"
  print "       [-lpun file.lpun] [-LJexp # # ...] [-aty file.aty [file2 [...]]"
  print "where file.aty is a list of atom-type pairs."
  exit(0)


for i in range(len(sys.argv)):
  if sys.argv[i] == '-rtf':
    next_id = 1
    next_arg = sys.argv[i+next_id]
    while next_arg[0] != '-' and next_arg != '':
      rtf.append(next_arg)
      next_id += 1
      if len(sys.argv) > i+next_id:
        next_arg = sys.argv[i+next_id]
      else:break
  elif sys.argv[i] == '-lpun':
    lpun = sys.argv[i+1]
    lpun_atype = True
  elif sys.argv[i] == '-pdb':
    pdb = sys.argv[i+1]
  elif sys.argv[i] == '-LJexp':
    LJexp = []
    j = i+1
    while True:
      if j+1 > len(sys.argv): break
      if sys.argv[j][0] == '-': break
      LJexp.append(int(sys.argv[j]))
      j += 1
  elif sys.argv[i] == '-aty':
    next_id = 1
    next_arg = sys.argv[i+next_id]
    while next_arg[0] != '-' and next_arg != '':
      atyp.append(next_arg)
      next_id += 1
      if len(sys.argv) > i+next_id:
        next_arg = sys.argv[i+next_id]
      else: break
  elif sys.argv[i] == '-h':
    usage()

if len(rtf) == 0 or pdb == '':
  usage()


frag = {}
for i in range(len(rtf)):
  try:
    r = open(rtf[i],'r')
  except:
    print "Could not open file ->",rtf
    print "Please check path and filename"
    exit(0)
  ############################
  # Read .rtf file and store necessary information
  #
  # Each fragment setup as obtained from the .rtf file (and the .lpun file) is stored in a dictionary with dictionaries inside
  # This could later be replace by objects. 
  #
  rl = r.readlines()
  r.close()
  
  i = 0
  while i < len(rl):
    if rl[i][:4] == 'RESI':
      line = rl[i].split()
      name = line[1]
      i += 2
      atoms = []
      atypes = []
      while rl[i][:4] == 'ATOM':
        line = rl[i].split()
        atoms.append(line[1])
        atypes.append(line[2])
        i += 1
      frag[(name,'atoms')]=atoms
      frag[(name,'atypes')]=atypes
    i += 1

try:
  p = open(pdb,'r')
except:
  print "Could not open file ->",pdb
  print "Please check path and filename and eventually specify pdb filename individually"
  exit(0)

if lpun_atype == True:
    try:
        l = open(lpun,'r')
    except:
        print "Could not open file ->",lpun
        print "Please check path and filename"
        exit(0)

############################
# Read .pdb file and extract setup and coordinates
#
import numpy

pl = p.readlines()
p.close()

i = 0
s_atoms = []
s_ftype = []
s_fid = []
s_coords = []

for line in pl:
    if line[0:6] == 'HETATM' or line[0:4] == 'ATOM':
        s_atoms.append(line[13:16].rstrip())
        s_ftype.append(line[17:21].rstrip())
        s_fid.append(int(line[25]))
        s_coords.append(numpy.array([float(line[30:38]),float(line[38:46]),float(line[46:54])]))

frag_idx = list(set(s_fid))
nfrags = len(frag_idx)

############################
# If given, read .lpun file and update atom types
#
if lpun_atype == True:
    ll = l.readlines()
    l.close()
    frags = set(s_ftype)
    for f in frags:
        start = s_ftype.index(f)
        for i in range(len(frag[(f,'atypes')])):
             frag[(f,'atypes')][i] = ll[3+6*start+6*i].split()[1]
    
############################
# The program can calucalate the distance dependent part for a variety of exponents.
#
# If only two exponents are use, the potential used here is the m-n potential:
#
#              A(ij)         B(ij)
# LJ(ij) =  ----------- - ----------
#             R(ij)^m       R(ij)^n
#
# The well known e(ij) and Rmin(ij) can be obtained from A and B according to
#
#
#           B^(m/(m-n))
#  e(ij) = -----
#           4*A^(n/(m-n)) 
#
#                A
#  Rmin(ij) = (-----)^(1/(m-n))
#                B
#
#
# From these, in principle atomic Rmin(i) and Rmin(j) can be calculated, 
#    but this is not really necessary and it probably the Rmin's obtained 
#    will be different for different pairs of i and j.
#
# Atomic e(i) can not be calculated back from e(ij), at least not directly.
#    Again, this is not necessary for the theory.
#
############################

############################
# Create Table with all contributors to the LJ potential
#

# 1st: create a list with all atom type pairs contributing to the interfragment
# LJ energy. If file.aty is given, read the file to extract the list.
atype_pairs = []
if atyp == []:
  for i in range(nfrags-1):
    for j in range(i+1,nfrags):
      for k in frag[(s_ftype[s_fid.index(frag_idx[i])],'atypes')]:
        for l in frag[(s_ftype[s_fid.index(frag_idx[j])],'atypes')]:
          a = [k,l]
          a.sort()
          # Exclude HT with OT (TIP3P water)
          if a not in [['OT','HT'],['HT','OT'],['HT','HT'],['OT','OT']]:
            atype_pairs.append(a[0]+':'+a[1])
else:
  for k in range(len(atyp)):
    try:
      aty_f = open(atyp[k],'r')
    except:
      print "Could not open file",aty
      exit(1)
    aty_l = aty_f.readlines()
    aty_f.close()
    for i in range(len(aty_l)):
      pair = aty_l[i].rstrip('\n').split('_')[0]
      el1, el2  = pair.split(":")
      conj_pair = el2+":"+el1    
      # Check that the pair hasn't already been added
      if pair not in atype_pairs and conj_pair not in atype_pairs:
        atype_pairs.append(pair)

atp = list(set(atype_pairs))
atp.sort()

# 2nd: add all contributions of specific Atom Type pairs up

import math
LJ_coeffs = [[0.0 for i in range(len(atp))] for j in range(len(LJexp))]

for i in range(nfrags-1):
    for j in range(i+1,nfrags):
        for k in range(len(frag[(s_ftype[s_fid.index(frag_idx[i])],'atypes')])):
            for l in range(len(frag[(s_ftype[s_fid.index(frag_idx[j])],'atypes')])):
                idx_af1 = s_fid.index(frag_idx[i])+k
                idx_af2 = s_fid.index(frag_idx[j])+l
                atype1 = frag[(s_ftype[s_fid.index(frag_idx[i])],'atypes')][frag[(s_ftype[s_fid.index(frag_idx[i])],'atoms')].index(s_atoms[idx_af1])]
                atype2 = frag[(s_ftype[s_fid.index(frag_idx[j])],'atypes')][frag[(s_ftype[s_fid.index(frag_idx[j])],'atoms')].index(s_atoms[idx_af2])]
                a = [atype1,atype2]
		a.sort()
		atype_pair = a[0]+':'+a[1]
                if atype_pair not in atp: atype_pair = a[1]+':'+a[0]
                dist = math.sqrt(numpy.dot(s_coords[idx_af1]-s_coords[idx_af2],s_coords[idx_af1]-s_coords[idx_af2]))
                for ii,m in enumerate(LJexp): 
                  if atype_pair not in ['OT:HT','HT:OT','HT:HT','OT:OT']:
                    LJ_coeffs[ii][atp.index(atype_pair)] += 1/dist**m

# 3rd: Write Atom type pairs and values to output

header = "Complex_ID"
for i in LJexp:
  for j in atp: header = header + ' ' + j +'_' + str(i)

values = pdb[:-4]
for i in range(len(LJexp)):
  for j in range(len(atp)): values = values + ' ' + str(LJ_coeffs[i][j])

#for i in range(len(LJ_A)): values = values + ' ' + str(LJ_A[i]) + ' ' + str(LJ_B[i])

f = open(pdb[:-4]+'.ljf','w')
f.write(header+'\n')
f.write(values+'\n')
f.close()



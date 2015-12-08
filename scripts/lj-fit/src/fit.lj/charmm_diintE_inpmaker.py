#!/usr/bin/env python
#
# This skript generates the inputs and carries out the calculations 
# of charmm dimer interaction energies.
# At the moment this skript only works for uncharged dimers
# The skript assumes that both monomers are the same and 
# that all atoms of the two monomers appear in the same order in the dimer .pdb file
#

import sys

inp = ''
rtf = []
par = []
lpun_mono1 = ''
lpun_mono2 = ''
lpun_di = ''
no_mtp = False

# Read input

for i in range(len(sys.argv)):
  if sys.argv[i] == '-pdb':
    inp = sys.argv[i+1]
  if sys.argv[i] == '-rtf':
    next_id  = 1
    next_arg = sys.argv[i+next_id]
    while next_arg[0] != '-' and next_arg != '':
      rtf.append(next_arg)
      next_id += 1
      if len(sys.argv) > i+next_id :
        next_arg = sys.argv[i+next_id]
      else: break
  if sys.argv[i] == '-par':
    next_id  = 1
    next_arg = sys.argv[i+next_id]
    while next_arg[0] != '-' and next_arg != '':
      par.append(next_arg)
      next_id += 1
      if len(sys.argv) > i+next_id:
        next_arg = sys.argv[i+next_id]
      else: break
  if sys.argv[i] == '-lpun_mono1':
    lpun_mono1 = sys.argv[i+1]
  if sys.argv[i] == '-lpun_mono2':
    lpun_mono2 = sys.argv[i+1]
  if sys.argv[i] == '-lpun_di':
    lpun_di = sys.argv[i+1]
  if sys.argv[i] == '-no_mtp':
    no_mtp = True

if inp == '' or len(rtf) == 0 or len(par) == 0: 
  print
  print("Usage: python charmm_diintE_inpmaker.py -pdb [file]")
  print("         -rtf [file1 [file2 [...]]] -par [file1 [file2 [...]]]")
  print("         [-lpun_mono1 [file]] [-lpun_mono2 [file]] [-lpun_di [file]] [-no_mtp]")
  print
  exit(1)

if no_mtp == False and (lpun_di == '' and lpun_mono1 == '' and lpun_mono2 == ''):
  print
  print('---> Either -no_mtp or the lpun files have to be specified <---')
  print
  print("Usage: python charmm_diintE_inpmaker.py -pdb [file]")
  print("         -rtf [file1 [file2 [...]]] -par [file1 [file2 [...]]]")
  print("         [-lpun_mono1 [file]] [-lpun_mono2 [file]] [-lpun_di [file]] [-no_mtp]")
  print
  exit(1)

import os

# Read pdb file and extract residue names
# It is assumed that the first monomer has ID 1 and all others belong to fragment 2.

f = open(inp,'r')
a = f.readlines()
f.close()

inlines = [line for line in a if line[0:6].strip() in ['HETATM','ATOM']]
resnames = []
curr_res = '0'
for line in inlines:
  if line[22:26].strip() <> curr_res: 
     curr_res = line[22:26].strip()
     resnames.append(line[17:21].strip())

# Write .pdb for dimer (reorder molecule IDs)
f = open(inp[:-4]+'di.pdb','w')
res_index = 0
dimlist = []
for line in inlines:
  curr_res = line[22:26].strip()
  if curr_res not in dimlist:
    dimlist.append(curr_res)
    res_index += 1
  line = line[:22]+str("{:4d}".format(res_index))+line[26:]
  f.write(line)
f.write("END")
f.close()

# write .pdb for fragment 1

f = open(inp[:-4]+'f1.pdb','w')
for line in inlines:
  if line[22:26].strip() == '1': f.write(line)

f.write("END")
f.close()

# List of molecules in fragment 2
frag2 = []
# write .pdb for fragment 2
f = open(inp[:-4]+'f2.pdb','w')
res_index = 0
for line in inlines:
  if line[22:26].strip() != '1': 
    curr_res = line[22:26].strip()
    if curr_res not in frag2:
      frag2.append(curr_res)
      res_index += 1
    line = line[:22]+str("{:4d}".format(res_index))+line[26:]
    f.write(line)

f.write("END")
f.close()

# Number of molecules in each fragment
n_mols_frag = [str(1), str(len(frag2))]

# write input for charmm dimer energy calculation

f = open(inp[:-4]+'di.inp','w')
f.write('* '+inp[:-4]+' dimer energy\n*\n\n')
f.write('BOMLEV -1\n\n')
f.write('PRNLEV 4\n\n')
f.write('!READ TOPOLOGY\n\n')
f.write('OPEN UNIT 30 CARD READ NAME '+rtf[0]+'\n')
f.write('READ RTF CARD UNIT 30\n')
f.write('CLOSE UNIT 30\n\n')
for i in range(1,len(rtf)):
  f.write('OPEN UNIT 30 CARD READ NAME '+rtf[i]+'\n')
  f.write('READ RTF CARD APPEND UNIT 30\n')
  f.write('CLOSE UNIT 30\n\n')
f.write('!READ PARAMETER\n\n')
f.write('OPEN UNIT 31 CARD READ NAME '+par[0]+'\n')
f.write('READ PARAMETER CARD UNIT 31\n')
f.write('CLOSE UNIT 31\n\n')
for i in range(1,len(par)):
  f.write('OPEN UNIT 31 CARD READ NAME '+par[i]+'\n')
  f.write('READ PARAMETER CARD APPEND UNIT 31\n')
  f.write('CLOSE UNIT 31\n\n')
f.write('! MAKE SEQUENCE\n\n')
for i in range(2):
  f.write('READ SEQUENCE '+resnames[i]+' '+n_mols_frag[i]+'\n')
  if resnames[i] == 'TIP3':
    f.write('GENERATE '+resnames[i]+' NOANGLE\n')
  else:
    f.write('GENERATE '+resnames[i]+'\n')
f.write('OPEN UNIT 32 READ CARD NAME '+inp[:-4]+'di.pdb\n')
f.write('READ COOR PDB UNIT 32\n')
f.write('CLOSE UNIT 32\n\n')
f.write('NBONDS NBXMOD 5 ATOM CDIEL EPS 1.0 SHIFT VATOM VDISTANCE -\n')
f.write('  VSWITCH CUTNB 99.0 CTOFNB 98.0 CTONNB 97. E14FAC 1.0\n')
if lpun_di != '':
  f.write('!============initialize MTPL module====================\n')
  f.write('OPEN UNIT 40 CARD READ NAME '+lpun_di+'\n')
  f.write('MTPL MTPUNIT 40\n')
  f.write('CLOSE UNIT 40\n\n')

f.write('ENERGY CUTNB 99.0 CTONNB 99.0 CTOFNB 99.0\n\n')
f.write('STOP\n\n')
f.close()

# write input for charmm fragment 1 energy calculation

f = open(inp[:-4]+'f1.inp','w')
f.write('* '+inp[:-4]+' fragment 1 energy\n*\n\n')
f.write('BOMLEV -1\n\n')
f.write('PRNLEV 4\n\n')
f.write('!READ TOPOLOGY\n\n')
f.write('OPEN UNIT 30 CARD READ NAME '+rtf[0]+'\n')
f.write('READ RTF CARD UNIT 30\n')
f.write('CLOSE UNIT 30\n\n')
for i in range(1,len(rtf)):
  f.write('OPEN UNIT 30 CARD READ NAME '+rtf[i]+'\n')
  f.write('READ RTF CARD APPEND UNIT 30\n')
  f.write('CLOSE UNIT 30\n\n')
f.write('!READ PARAMETER\n\n')
f.write('OPEN UNIT 31 CARD READ NAME '+par[0]+'\n')
f.write('READ PARAMETER CARD UNIT 31\n')
f.write('CLOSE UNIT 31\n\n')
for i in range(1,len(par)):
  f.write('OPEN UNIT 31 CARD READ NAME '+par[i]+'\n')
  f.write('READ PARAMETER CARD APPEND UNIT 31\n')
  f.write('CLOSE UNIT 31\n\n')
f.write('! MAKE SEQUENCE\n\n')
f.write('READ SEQUENCE '+resnames[0]+' '+n_mols_frag[0]+'\n')
if resnames[0] == 'TIP3':
  f.write('GENERATE '+resnames[0]+' NOANGLE\n')
else:
  f.write('GENERATE '+resnames[0]+'\n')
f.write('OPEN UNIT 32 READ CARD NAME '+inp[:-4]+'f1.pdb\n')
f.write('READ COOR PDB UNIT 32\n')
f.write('CLOSE UNIT 32\n\n')
f.write('NBONDS NBXMOD 5 ATOM CDIEL EPS 1.0 SHIFT VATOM VDISTANCE -\n')
f.write('  VSWITCH CUTNB 99.0 CTOFNB 98.0 CTONNB 97. E14FAC 1.0\n')
if lpun_mono1 != '':
  f.write('!============initialize MTPL module====================\n')
  f.write('OPEN UNIT 40 CARD READ NAME '+lpun_mono1+'\n')
  f.write('MTPL MTPUNIT 40\n')
  f.write('CLOSE UNIT 40\n\n')

f.write('ENERGY CUTNB 99.0 CTONNB 99.0 CTOFNB 99.0\n\n')
f.write('STOP\n\n')
f.close()


# write input for charmm fragment 2 energy calculation

f = open(inp[:-4]+'f2.inp','w')
f.write('* '+inp[:-4]+' fragment 2 energy\n*\n\n')
f.write('BOMLEV -1\n\n')
f.write('PRNLEV 4\n\n')
f.write('!READ TOPOLOGY\n\n')
f.write('OPEN UNIT 30 CARD READ NAME '+rtf[0]+'\n')
f.write('READ RTF CARD UNIT 30\n')
f.write('CLOSE UNIT 30\n\n')
for i in range(1,len(rtf)):
  f.write('OPEN UNIT 30 CARD READ NAME '+rtf[i]+'\n')
  f.write('READ RTF CARD APPEND UNIT 30\n')
  f.write('CLOSE UNIT 30\n\n')
f.write('!READ PARAMETER\n\n')
f.write('OPEN UNIT 31 CARD READ NAME '+par[0]+'\n')
f.write('READ PARAMETER CARD UNIT 31\n')
f.write('CLOSE UNIT 31\n\n')
for i in range(1,len(par)):
  f.write('OPEN UNIT 31 CARD READ NAME '+par[i]+'\n')
  f.write('READ PARAMETER CARD APPEND UNIT 31\n')
  f.write('CLOSE UNIT 31\n\n')
f.write('! MAKE SEQUENCE\n\n')
f.write('READ SEQUENCE '+resnames[1]+' '+n_mols_frag[1]+'\n')
if resnames[1] == 'TIP3':
  f.write('GENERATE '+resnames[1]+' NOANGLE\n')
else:
  f.write('GENERATE '+resnames[1]+'\n')
f.write('OPEN UNIT 32 READ CARD NAME '+inp[:-4]+'f2.pdb\n')
f.write('READ COOR PDB UNIT 32\n')
f.write('CLOSE UNIT 32\n\n')
f.write('NBONDS NBXMOD 5 ATOM CDIEL EPS 1.0 SHIFT VATOM VDISTANCE -\n')
f.write('  VSWITCH CUTNB 99.0 CTOFNB 98.0 CTONNB 97. E14FAC 1.0\n')
if lpun_mono2 != '':
  f.write('!============initialize MTPL module====================\n')
  f.write('OPEN UNIT 40 CARD READ NAME '+lpun_mono2+'\n')
  f.write('MTP MTPUNIT 40\n')
  f.write('CLOSE UNIT 40\n\n')

f.write('ENERGY CUTNB 99.0 CTONNB 99.0 CTOFNB 99.0\n\n')
f.write('STOP\n\n')
f.close()





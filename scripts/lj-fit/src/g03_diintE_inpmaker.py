#!/usr/bin/env python
# This skript generates the input for a gaussian calculation of dimer interaction energies
# At the moment this skript only works for uncharged dimers
#

import os,sys

inp = ''

# Read input

for i in range(len(sys.argv)):
  if sys.argv[i] == '-in':
    inp = sys.argv[i+1]

if inp == '':
  print
  print("Usage: python g03_diintE_inpmaker.py -in [file]")
  print("Caution: This skript only works for uncharged dimers")
  print
  exit(0)

f = open(inp,'r')
a = f.readlines()
f.close()

inlines = [line for line in a if line[0:6].strip() in ['ATOM','HETATM']]
#for line in a:
#  if line.split()[0] in ['ATOM','HETATM']:
#    inlines.append(line)

elements = []
xyz = []
frag = []

for line in inlines:
  frag.append(line[22:26].strip())
  xyz.append([line[30:38].strip(),line[38:46].strip(),line[46:54].strip()])
  ele = line[12:16].strip()
  while True:
    if ele[-1] in [str(i) for i in range(10)]: 
      ele = ele[:-1]
    else:
      break
  elements.append(ele)

# Write Gaussian input file

wd = os.getcwd()

f = open(inp[:-4]+'.inp','w')
f.write('%nproc=1\n')
f.write('#MP2/aug-cc-PVDZ Counterpoise=2 maxdisk=20GB\n\n')
f.write('Dimer Interaction energy for '+inp[:-4]+'\n\n')
f.write('0,1 0,1 0,1\n')
for i in range(len(frag)):
  line = elements[i]+'   '+xyz[i][0]+'   '+xyz[i][1]+'   '+xyz[i][2]+' '+frag[i]+'\n'
  f.write(line)

f.write('\n\n')
f.close()





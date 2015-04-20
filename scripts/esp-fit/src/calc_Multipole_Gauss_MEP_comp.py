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
########################################################################
# This skript calculates the comparison of Electrostatic Potentials
# obtained from Gaussian calculations and Multipoles derived from that
#
# To be run from computer cluster with queuing system, fornchk, and gaussian.
######################################################################## 

###########
# These are system specific variables that might need to be changed by
#  the user. Absolute paths may be required here!

formchkexe   = 'formchk'
cubegenexe   = 'cubegen'
gsub_exe     = 'gsub_com'       # Gaussian submission script or executable
gdmaexe      = 'gdma'
fieldcompexe = 'fieldcomp'
# Flag as to whether GDMA is computed locally (True) or 
# submitted to the queuing system (False)
gdma_local   = True
# Gaussian submission script extension
gauss_ext    = 'com'

###########
# Set defaults. They may also need to be changed

mtp_order = 2
charge = 0
state = 1
checkpoint_path = '/tmp/'
grid_extra_space = 5      # 5 Angstroem extra space after the last atoms in each direction to be considered
grid_stepsize = 0.1     # Calculate a Gridpoint each 0.1 Angstroem
gauss_prm = '#P MP2 6-31G** nosymm'
ncor = 1

vdw_radii = {'H' :2.268,
             'He':2.301,
             'Du':0.0,
             'B' :3.931,
             'C' :3.496,
             'N' :2.91,
             'O' :2.646,
             'F' :2.551,
             'P' :3.591,
             'S' :3.496,
             'Cl':3.42,
             'Br':3.685,
             'I' :4.063,
             'Fe':4.0,
             'K': 5.20,
             'Zn':2.80}

############
# Start Program

from sys import *
import subprocess,os,time
import os.path

# Read input (Multipole limit, input z-matrix)

usage = 'Usage: python calc_Multipole_Gauss_MEP_comp.py \n' + \
  '  [OPTIONS] -xyz file.xyz\n' + \
  'OPTIONS:\n' + \
  '  -mtp_order ARG: multipole rank (default:'+str(mtp_order)+')\n' + \
  '  -stpsz ARG    : grid step size (default:'+str(grid_stepsize)+')\n' + \
  '  -charge ARG   : net charge (default:'+str(charge)+')\n' + \
  '  -state ARG    : multiplicity (default:'+str(state)+')\n' + \
  '  -Gauss ARG    : Gaussian options (default:'+gauss_prm+')\n' + \
  '  -n ARG        : number of cores (default:'+str(ncor)+')\n' + \
  '  -formchk      : path to formchk (default:'+formchkexe+')\n' + \
  '  -cubegen      : path to cubegen (default:'+cubegenexe+')\n' + \
  '  -gsub         : path to Gaussian submission script (default:'+gsub_exe+')\n' + \
  '  -gdma         : path to gdma (default:'+gdmaexe+')\n' + \
  '  -fieldcomp    : path to fieldcomp (default:'+fieldcompexe+')\n' + \
  '  -gdma_local   : gdma computed locally (default:'+str(gdma_local)+')\n' + \
  '  -gauss_ext    : Gaussian submission script extension (default:'+str(gauss_ext)+')\n' + \
  '  -h            : help\n'

if '-h' in argv:
  print usage
  exit(0)

if '-zxy' in argv or '-xyz' in argv:
  fileext = []
  if '-zxy' in argv:
    fileext.append('-zxy')
  if '-xyz' in argv:
    fileext.append('-xyz')
  if len(fileext) > 1:
    print "Error. Multiple coordinate files."
    exit(1)
  zmat_file = argv[argv.index(fileext[0])+1]
  filename_body = zmat_file[:zmat_file.index('.')]
  z_file = open(zmat_file,'r')
  zmat = z_file.readlines()
  z_file.close()
  # If we're reading an xyz file, delete the first two lines
  if len(zmat[0].split()) == 1:
    zmat = zmat[2:]
else:
  print usage
  exit(1)

if '-mtp_order' in argv:
  mtp_order = argv[argv.index('-mtp_order')+1]
if '-stpsz' in argv:
  grid_stepsize = float(argv[argv.index('-stpsz')+1])
if '-charge' in argv:
  charge = int(argv[argv.index('-charge')+1])
if '-state' in argv:
  state = int(argv[argv.index('-state')+1])
if '-Gauss' in argv:
  gauss_prm = argv[argv.index('-Gauss')+1]
if '-n' in argv:
  ncor = argv[argv.index('-n')+1]
if '-formchk' in argv:
  formchkexe = argv[argv.index('-formchk')+1]
if '-cubegen' in argv:
  cubegenexe = argv[argv.index('-cubegen')+1]
if '-gsub' in argv:
  gsub_exe = argv[argv.index('-gsub')+1]
if '-gdma' in argv:
  gdmaexe = argv[argv.index('-gdma')+1]
if '-fieldcomp' in argv:
  fieldcompexe = argv[argv.index('-fieldcomp')+1]
if '-gdma_local' in argv:
  gdma_local = argv[argv.index('-gdma_local')+1]
if '-gauss_ext' in argv:
  gauss_ext = argv[argv.index('-gauss_ext')+1]

##############
# Check that all executables work
for exe in [formchkexe, gdmaexe, fieldcompexe, cubegenexe, gsub_exe]:
  checkexe_call = 'command -v '+ exe +' >/dev/null 2>&1 || ' + \
    '{ echo >&2 "I require '+ exe +' but it\'s not installed.  Aborting."; ' + \
    'exit 1; }'
  if subprocess.call(checkexe_call, shell=True) != 0:
    exit(1)

os.mkdir(os.getcwd()+'/'+filename_body)
os.chdir(os.getcwd()+'/'+filename_body)

###########################################
# Write input file for gaussian calculation
###########################################

gauss_file = open(filename_body+'.'+gauss_ext,'w')

gauss_file.write('%NPROC='+str(ncor)+'\n')
gauss_file.write('%Chk='+os.getcwd()+'/'+filename_body+'.chk\n')
gauss_file.write('%Mem=1000MB\n\n')
gauss_file.write(gauss_prm+'\n\n')
gauss_file.write(filename_body+' single point\n\n')
gauss_file.write(str(charge)+' '+str(state)+'\n')
for line in zmat:
  gauss_file.write(line)
gauss_file.write('\n\n')

gauss_file.close()

###########################################
# Run gaussian calculation
###########################################

print "Running gaussian..."
subprocess.call([gsub_exe,filename_body+'.com'])
found_end_of_file = False
while not found_end_of_file:
  outfile = ''
  if os.path.exists(filename_body+'.log'):
    outfile = filename_body+'.log'
  elif os.path.exists(filename_body+'.out'):
    outfile = filename_body+'.out'
  if outfile != '':
    gauss_o = open(outfile,'r')
    gauss_output = gauss_o.readlines()
    gauss_o.close()
    for line in gauss_output[-10:]:
      if 'Error termination' in line:
        print "Error termination in gaussian while calculating "+filename_body+"\nexiting"
        exit(0)
      elif ' Normal termination of Gaussian' in line:
        found_end_of_file = True
  time.sleep(10)

###########################################
# Convert the checkpoint file into formchk file
###########################################

print "Running formchk"
formchk_call = formchkexe+' '+filename_body+'.chk'
subprocess.call(formchk_call,shell=True)

###########################################
# Prepare GDMA input file
###########################################

gdma_inp = open(filename_body+'.dat','w')
gdma_inp.write('Title "'+filename_body+' HF 6-31G* G03"\n')
gdma_inp.write('File '+filename_body+'.fchk\n\n')
gdma_inp.write('Angstrom\nMultipoles\n  Limit '+str(mtp_order)+'\n')
gdma_inp.write('  Punch '+filename_body+'.pun\nStart\n\nFinish')
gdma_inp.close()

###########################################
# Prepare submission script
###########################################

if not gdma_local:
  gdma_sub = open(filename_body+'.gdma.sub','w')
  gdma_sub.write('#$ -S /bin/bash\n')
  gdma_sub.write('#$ -cwd\n')
  gdma_sub.write('cd $PWD\n')
  gdma_sub.write(gdmaexe+' < '+filename_body+'.dat > '+filename_body+'.dma\n')
  gdma_sub.close()

###########################################
# Execute GDMA code
###########################################

print "Running gdma..."
if gdma_local:
  gdma_sub_call = gdmaexe+' < '+filename_body+'.dat > '+filename_body+'.dma'
else:
  gdma_sub_call = 'qsub '+filename_body+'.gdma.sub'

print gdma_sub_call
subprocess.Popen(gdma_sub_call,shell=True).communicate()
while True:
  if os.path.exists(filename_body+'.dma'):
    gdma_o = open(filename_body+'.dma','r')
    gdma_output = gdma_o.readlines()
    gdma_o.close()
    if len(gdma_output) > 0:
        if gdma_output[-1][:11] == 'Finished at':
            break
  time.sleep(10)

###########################################
# Calculate maximum .xyz coordinates from GDMA output and calculate grid
# to be used within cubegen and fieldcomp
###########################################

print "Calculating grid specifications..."
dmaout_file = open(filename_body+'.dma','r')
dma_out = dmaout_file.readlines()

xyz = []
natoms = 0
i=0
dma_read = False
while True:
  if dma_out[i][0:5] == "Total": break
  if "Multipole moments in" in dma_out[i]: dma_read = True
  if dma_read and "x =" in dma_out[i]:
    if dma_out[i].split()[0] in ("H","He","B","C","N","O","F","P","S","I","Zn","Cl","Br","K"):
      xyz.append(dma_out[i])
      natoms = natoms+1
  i = i + 1

for i in range(len(xyz)):
  xyz[i] = xyz[i].split()
  if len(xyz[i][2]) > 1:
    xyz[i].insert(2,'=')
    xyz[i][3] = xyz[i][3].lstrip('=')
  if len(xyz[i][5]) > 1:
    xyz[i].insert(5,'=')
    xyz[i][6] = xyz[i][6].lstrip('=')
  if len(xyz[i][8]) > 1:
    xyz[i].insert(8,'=')
    xyz[i][9] = xyz[i][9].lstrip('=')

xmin = None
xmax = None
ymin = None
ymax = None
zmin = None
zmax = None

for i in range(len(xyz)):
  if xmin == None:
    xmin = float(xyz[i][3])
  elif xmin > float(xyz[i][3]):
    xmin = float(xyz[i][3])
  if xmax == None:
    xmax = float(xyz[i][3])
  elif xmax < float(xyz[i][3]):
    xmax = float(xyz[i][3])
  if ymin == None:
    ymin = float(xyz[i][6])
  elif ymin > float(xyz[i][6]):
    ymin = float(xyz[i][6])
  if ymax == None:
    ymax = float(xyz[i][6])
  elif ymax < float(xyz[i][6]):
    ymax = float(xyz[i][6])
  if zmin == None:
    zmin = float(xyz[i][9])
  elif zmin > float(xyz[i][9]):
    zmin = float(xyz[i][9])
  if zmax == None:
    zmax = float(xyz[i][9])
  elif zmax < float(xyz[i][9]):
    zmax = float(xyz[i][9])

range_x = round(xmax-xmin + 2*grid_extra_space,1)
range_y = round(ymax-ymin + 2*grid_extra_space,1)
range_z = round(zmax-zmin + 2*grid_extra_space,1)

xstart = round(xmin-grid_extra_space,1)
ystart = round(ymin-grid_extra_space,1)
zstart = round(zmin-grid_extra_space,1)

################################
# Run cubegen
################################

grid_spec = '"-1, '+str(xstart)+', '+str(ystart)+', '+str(zstart)+'\n '+str(int(1+range_x/grid_stepsize))+', '+str(grid_stepsize)+', 0.0, 0.0\n '+str(int(1+range_y/grid_stepsize))+', 0.0, '+str(grid_stepsize)+', 0.0\n '+str(int(1+range_z/grid_stepsize))+', 0.0, 0.0, '+str(grid_stepsize)+'" '
cubegen_call = 'echo -e '+grid_spec+'| '+cubegenexe+' 0 potential '+filename_body+'.fchk '+filename_body+'.cube -1'

print "Running cubegen..."
subprocess.Popen(cubegen_call,shell=True).communicate()

################################
# Prepare file containing the vdw radii for the input molecule
################################

vdw_file = open(filename_body+'.vdw','w')
vdw_file.write('\n')
for i in range(len(xyz)):
  vdw_file.write(str(vdw_radii[xyz[i][0]])+'\n')
  vdw_file.write(str(mtp_order)+'\n\n')

vdw_file.close()

################################
# Run fieldcomp
################################

print "Running fieldcomp..."
fieldcomp_call = fieldcompexe+' -cube '+filename_body+'.cube -vdw '+filename_body+'.vdw -pun '+filename_body+'.pun > '+filename_body+'_fieldcomp.log'
subprocess.Popen(fieldcomp_call,shell=True).communicate()

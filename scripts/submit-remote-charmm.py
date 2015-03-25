#!/usr/bin/env python

# Submit ab initio calculations to remote cluster, then 
# copy the results back.

# Tristan BEREAU (2013)

import sys,os,ConfigParser,argparse,subprocess
dir = os.path.dirname(os.path.realpath(__file__))

#print dir

config = ConfigParser.ConfigParser()
config.readfp(open('../scripts/config.ini'))

#print config

## Parse command-line options 
parser = argparse.ArgumentParser(description=
  'Submit CHARMM calculations to remote cluster, then copy the results back',
  epilog='Florent Hedin (2015) Tristan BEREAU (2013)')
  
parser.add_argument('-inp',dest='inpF',type=str,
  required=True, help='CHARMM input file')
  
parser.add_argument('-out',dest='outF',type=str,
  default="charmm.out", help='CHARMM output file')
  
parser.add_argument('-par',dest='parF',type=str,
  required=True, help='CHARMM FF parameters file')
  
parser.add_argument('-top',dest='topF',type=str,
  required=True, help='CHARMM FF topology file')
  
parser.add_argument('-lpun',dest='lpunF',type=str,
  required=True, help='CHARMM MTP lpun file')
  
parser.add_argument('-np',dest='numCores',type=int,
  default=1, help='number of cores for MPI parallelized runs')

args = parser.parse_args()

#print parser

#print args.inpF
#print args.outF
#print args.parF
#print args.topF
#print args.lpunF
#print args.numCores

# Test SSH connection
print "Establishing connection"
sshuser = config.get('remote','user').strip('\'')
hostname = config.get('remote','hostname').strip('\'')
sshaddress = sshuser+"@"+hostname
bashCmd = "ssh " + sshaddress + " hostname >/dev/null"
process = subprocess.call(bashCmd.split())
if process != 0:
  print "Error. Can't connect to remote computer."
  exit(1)
  
# charmm test locally
#bashCmd = "../scripts/charmm -i " + args.inpF + " -o " + args.outF
#print bashCmd.split()
#process = subprocess.call(bashCmd.split())
#if process != 0:
#  print "Problem with the CHARMM calculation, check the output file."
#  exit(0)

## Create directory for calculations
workdir = config.get('remote','workdir').strip('\'')
bashCmd = "ssh " + sshaddress + " ls "+workdir+" "
existing_dirs = subprocess.check_output(bashCmd.split())
dirIndex = 0
dirName = "dir{:03d}".format(dirIndex)
while dirName in existing_dirs:
  dirIndex += 1
  dirName = "dir{:03d}".format(dirIndex)
  if dirIndex > 999:
    print "Error. No free directory in remote cluster."
    exit(1)
bashCmd = "ssh " + sshaddress + " mkdir -p " + workdir + \
  "/" + dirName
process = subprocess.call(bashCmd.split())
if process != 0:
  print "Error. Can't create remote directory."
  exit(1)
print "Copying files to remote computer"
bashCmd = "scp " + args.inpF + " " + args.parF + " " + args.topF + " "+ args.lpunF + " " + sshaddress + ":" + workdir + "/" + dirName
process = subprocess.call(bashCmd.split())
if process != 0:
  print "Error. Can't copy files to remote directory."
  exit(1)
#print "Running script remotely in " + sshaddress + \
#  ":" + workdir + "/" + dirName
#print "Please wait..."
#formchk    = config.get('remote','formchk').strip('\'')
#cubegen    = config.get('remote','cubegen').strip('\'')
#gdma       = config.get('remote','gdma').strip('\'')
#fieldcomp  = config.get('remote','fieldcomp').strip('\'')
#gdma_local = config.get('remote','gdma_local').strip('\'')
#gauss_ext  = config.get('remote','gauss_ext').strip('\'')
#gsub       = config.get('remote','gsub').strip('\'')
#bashCmd = "ssh " + sshaddress + " cd " + workdir + \
#  "/" + dirName + "; python calc_Multipole_Gauss_MEP_comp.py -xyz " + \
#  args.xyz + " -mtp_order " + str(args.mtpOrder) + \
#  " -stpsz " + str(args.gridStep) + \
#  " -charge " + str(args.netCharge) + \
#  " -state " + str(args.multiplicity) + \
#  " -Gauss \"" + args.gauss + "\" -n " + str(args.numCores) + \
#  " -formchk "+ formchk +" -cubegen " + cubegen + \
#  " -gdma " + gdma + " -fieldcomp " + fieldcomp + \
#  " -gdma_local " + gdma_local + " -gauss_ext " + gauss_ext + " -gsub " + gsub  
## print bashCmd
#process = subprocess.check_output(bashCmd.split())
## After this point, the script has ended. Copy back the results
#print "Copying back remote data and delete remote directory"
#bashCmd = "scp -r " + sshaddress + ":" + workdir + "/" + \
#  dirName + "/* " + os.getcwd()
#process = subprocess.call(bashCmd.split())
#if process != 0:
#  print "Error. Can't copy files back from remote directory."
#  exit(1)
#bashCmd = "ssh " + sshaddress + " rm -rf " + workdir + "/" + dirName
#process = subprocess.call(bashCmd.split())

#!/usr/bin/env python

# Submit ab initio calculations to remote cluster, then 
# copy the results back.

# Tristan BEREAU (2013)

import sys,os,ConfigParser,argparse,subprocess
dir = os.path.dirname(os.path.realpath(__file__))

print dir

config = ConfigParser.ConfigParser()
config.readfp(open(dir+'/config.ini'))

# Parse command-line options 
parser = argparse.ArgumentParser(description=
  'Submit ab initio calculations to remote cluster, then copy the results back',
  epilog='Tristan BEREAU (2013)')
parser.add_argument('-xyz',dest='xyz',type=str,required=True,
  help='XYZ coordinate file')
parser.add_argument('-mtp_order',dest='mtpOrder',type=int,
  default=0, help='MTP rank')
parser.add_argument('-charge',dest='netCharge',type=int,
  default=0, help='Net molecular charge')
parser.add_argument('-state',dest='multiplicity',type=int,
  default=1, help='Multiplicity')
parser.add_argument('-Gauss',dest='gauss',type=str,
  default="#P MP2 6-31G** nosymm")
parser.add_argument('-stpsz',dest='gridStep',type=float,
  default=0.1, help='grid step size')
parser.add_argument('-n',dest='numCores',type=int,
  default=1, help='number of cores')

args = parser.parse_args()

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
# Create directory for calculations
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
bashCmd = "scp " + args.xyz + " " + \
  dir + "/calc_Multipole_Gauss_MEP_comp.py " + \
  sshaddress + ":" + workdir + "/" + dirName
process = subprocess.call(bashCmd.split())
if process != 0:
  print "Error. Can't copy files to remote directory."
  exit(1)
print "Running script remotely in " + sshaddress + \
  ":" + workdir + "/" + dirName
print "Please wait..."
formchk    = config.get('remote','formchk').strip('\'')
cubegen    = config.get('remote','cubegen').strip('\'')
gdma       = config.get('remote','gdma').strip('\'')
fieldcomp  = config.get('remote','fieldcomp').strip('\'')
gdma_local = config.get('remote','gdma_local').strip('\'')
gauss_ext  = config.get('remote','gauss_ext').strip('\'')
gsub       = config.get('remote','gsub').strip('\'')
bashCmd = "ssh " + sshaddress + " cd " + workdir + \
  "/" + dirName + "; python calc_Multipole_Gauss_MEP_comp.py -xyz " + \
  args.xyz + " -mtp_order " + str(args.mtpOrder) + \
  " -stpsz " + str(args.gridStep) + \
  " -charge " + str(args.netCharge) + \
  " -state " + str(args.multiplicity) + \
  " -Gauss \"" + args.gauss + "\" -n " + str(args.numCores) + \
  " -formchk "+ formchk +" -cubegen " + cubegen + \
  " -gdma " + gdma + " -fieldcomp " + fieldcomp + \
  " -gdma_local " + gdma_local + " -gauss_ext " + gauss_ext + " -gsub " + gsub  
# print bashCmd
process = subprocess.check_output(bashCmd.split())
# After this point, the script has ended. Copy back the results
print "Copying back remote data and delete remote directory"
bashCmd = "scp -r " + sshaddress + ":" + workdir + "/" + \
  dirName + "/* " + os.getcwd()
process = subprocess.call(bashCmd.split())
if process != 0:
  print "Error. Can't copy files back from remote directory."
  exit(1)
bashCmd = "ssh " + sshaddress + " rm -rf " + workdir + "/" + dirName
process = subprocess.call(bashCmd.split())

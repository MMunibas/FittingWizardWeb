#!/usr/bin/env python

# Submit ab initio calculations to remote cluster, then 
# copy the results back.

# Tristan BEREAU (2013)

import sys,os,ConfigParser,argparse,subprocess,time,tempfile
dir = os.path.dirname(os.path.realpath(__file__))

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

parser.add_argument('-pdb',dest='pdbF',type=str,
                    required=True, help='CHARMM PDB coordinates file')

parser.add_argument('-np',dest='numCores',type=int,
  default=1, help='number of cores for MPI parallelized runs')

parser.add_argument('-cfg',dest='cfgF',type=str,
  required=True, help='Remote clusters config file')
  
args = parser.parse_args()


config = ConfigParser.ConfigParser()
config.readfp(open(args.cfgF))


#print parser

#print args.inpF
#print args.outF
#print args.parF
#print args.topF
#print args.lpunF
#print args.pdbF
#print args.numCores

# TO REMOVE ; it is here for debugging
# exit(0)

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
bashCmd = "scp " + args.inpF + " " + args.parF + " " + args.topF + " " + args.lpunF + " " + args.pdbF + " " + sshaddress + ":" + workdir + "/" + dirName
process = subprocess.call(bashCmd.split())
if process != 0:
  print "Error. Can't copy files to remote directory."
  exit(1)

print "Running script remotely in " + sshaddress + \
 ":" + workdir + "/" + dirName
print "Please wait..."
chm = config.get('remote','charmm').strip('\'')

#generate a unique jobname for following submission status
uniqueID = "job_" + dirName
#submit job
bashCmd = "ssh " + sshaddress + " cd " + workdir + \
    "/" + dirName + " ; echo '/usr/lib64/mpi/gcc/openmpi/bin/mpirun -np " + str(args.numCores) + " " + chm + " -i " + \
        os.path.relpath(args.inpF) + " -o " + os.path.relpath(args.outF) + " ' > run.sh ; " + " qsub -N " + uniqueID + \
        " -cwd -S /bin/bash -pe orte* " + str(args.numCores) + " run.sh ; sleep 2"
process = subprocess.call(bashCmd.split())
if process != 0:
  print "Error when trying to run simulation script"
  exit(1)

# check if jobs still running
cmd = '''\
user=hedin
stop=0
myid=$1

while true;
do 
out=$(ssh verdi "qstat -u $user"| grep $myid | awk '{print $1}')
arr=($out)
size=${#arr[@]}
if [ "$size" -eq "$stop" ]; then
break
fi
echo "$size jobs still running ; sleeping 10 seconds and then looping ..."
sleep 10
done
'''

with tempfile.NamedTemporaryFile() as scriptfile:
    scriptfile.write(cmd)
    scriptfile.flush()
    process = subprocess.call(['/bin/bash', scriptfile.name, str(uniqueID)])
    if process != 0:
        print "Error. Can't copy files back from remote directory."
        exit(1)
  
# After this point, the script has ended. Copy back the results
print "Copying back remote data and delete remote directory"
bashCmd = "rsync -avz " + sshaddress + ":" + workdir + "/" + dirName + "/" + os.path.relpath(args.outF) + " ."
print bashCmd
process = subprocess.call(bashCmd.split())
if process != 0:
  print "Error. Can't copy files back from remote directory."
  exit(1)


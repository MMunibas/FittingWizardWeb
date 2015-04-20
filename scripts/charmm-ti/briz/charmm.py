'''Submit CHARMM jobs remotely'''

import ssh
import os
import sys
import misc

class RunCharmmRemotely(ssh.RunCommandRemotely):
  '''Run CHARMM job remotely'''
  def __init__(self, server, charmmInp="", subdir=""):
    ssh.RunCommandRemotely.__init__(self, server=server, subdir=subdir)
    self.charmmInp = ""
    self.charmmOut = ""
    self.subjobs   = []
    misc.assertProgramIsReachable('catdcd')


  def generateCharmmJob(self, inpFile, outFile, otherInpDir="", email=False):
    self.charmmInp = inpFile
    self.charmmOut = outFile
    emailString    = ""
    if email is True:
      emailString = "#$ -m a\n#$ -M %s" % \
        self.config.get('misc','email')
    copyOtherInpDir    = ""
    if otherInpDir != "":
      otherDir = ""
      if otherInpDir[0] == "/":
        otherDir = otherInpDir
      else:
        otherDir = self.remdir + "/" + otherInpDir
      copyOtherInpDir = "cp -r %s/* $tempdir" % otherDir
    return \
'''#!/bin/bash
%s

input=%s
output=%s
tempdir=%s/$JOB_ID
mkdir -p $tempdir
oridir=%s

cp -r $oridir/* $tempdir/
%s
cd $tempdir
%s
mpirun=%s
charmm=%s
if [  "$NSLOTS" -gt 1 ]; then
  $mpirun -v -np $NSLOTS $charmm < $input > $output
else
    $charmm < $input > $output
fi

cd $oridir
mv $tempdir/* $oridir
rmdir $tempdir
''' % (emailString, self.charmmInp, self.charmmOut,
  self.config.get(self.server, 'scratchdir'), self.remdir + "/" + self.subsubdir,
  copyOtherInpDir, self.config.get(self.server,'module'), 
  self.config.get(self.server,'mpirun'),
  self.config.get(self.server, 'charmm'))

  def remoteSimulationTerminatedNormally(self, myFile):
    # Analyze remote simulation output log. Return True if it's reached Normal
    # Termination; False otherwise.
    baseFile = myFile
    if baseFile.find("/") != -1:
      baseFile = myFile[myFile.rfind("/")+1:]
    remFile = self.remdir + "/" + self.subsubdir + "/" + baseFile
    try:
      f = self.sftp.open(remFile,'r')
      s = f.readlines()
      f.close()
    except IOError, e:
      print "I/O Error: {0}".format(e.strerror)
      return False
    if len(s) < 11:
      return False
    for i in range(len(s)-10,len(s)):
      if "NORMAL TERMINATION BY NORMAL STOP" in s[i]:
        return True
    return False

  def consistentAndGet(self, myFile):
    # Analyze remote simulation and get file. Return True if it was
    # successful, False otherwise.
    if self.remoteFileExists(myFile) is False:
      return False
    status = self.remoteSimulationTerminatedNormally(myFile)
    if status == False:
      return False
    self.getFile(myFile)
    return True

  def trjfileConsistent(self, myFile):
    # Analyze trjFile locally using catdcd. Return True if no Warning was
    # issued.
    if self.localFileExists(myFile) is False:
      return False
    baseFile = myFile
    if baseFile.find("/") != -1:
      baseFile = myFile[myFile.rfind("/")+1:]
    oriFile = os.path.abspath(myFile)
    # Now grep Warning in catdcd. Return True if grep returns a non-zero
    # exit code.
    if os.system("catdcd " + oriFile + " | grep Warning >/dev/null") != 0:
      return True
    else:
      return False


if __name__ == "__main__":
  # Testing with verdi
  server = 'verdi'
  rmtChm = RunCharmmRemotely(server, subdir='dir000000')
  print rmtChm.generateCharmmJob(inpFile='sim.inp', outFile='sim.out')

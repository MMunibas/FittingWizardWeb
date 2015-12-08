'''ssh computer cluster and run job'''

import ConfigParser
import paramiko
import os
import time
import misc
import subprocess

class RunCommandRemotely:
  '''Run remote command on server'''
  def __init__(self, server, subdir=''):
    self.config   = ConfigParser.RawConfigParser()
    hostsFile = __file__[:__file__.rfind("/")] + "/hosts.ini"
    self.config.read(hostsFile) 

    self.locdir      = os.getcwd()
    self.server      = server
    self.hostname    = self.config.get(server, 'hostname')
    self.username    = self.config.get(server, 'user')
    self.workdir     = self.config.get(server, 'workdir')
    self.subdir      = subdir
    self.remdir      = ''
    self.subsubdir   = ''
    self.pkeyfile    = os.path.expanduser('~/.ssh/id_rsa_fw')
    self.mykey       = paramiko.RSAKey.from_private_key_file(
      self.pkeyfile)
    self.trnsprt     = paramiko.Transport((self.hostname, 22))
    self.trnsprt.connect(username=self.username, pkey=self.mykey)
    self.sftp        = paramiko.SFTPClient.from_transport(self.trnsprt)
    self.ssh         = paramiko.SSHClient()
    self.stdin       = ''
    self.stdout      = ''
    self.stderr      = ''
    self.maxtrials   = 20
    self.numprocflag = self.config.get(server, 'numprocflag')
    self.queuespec   = self.config.get(server, 'queuespec' )
    self.queuespecn  = self.config.get(server, 'queuespecn')
    self.quejobidcol = self.config.get(server, 'quejobidcol')
    self.joblog      = "logdir"

    self.ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    # Check that md5sum is present on both local and remote machines.
    misc.assertProgramIsReachable('md5sum')
    self.assertRemoteProgramIsReachable('md5sum')

    # Connect to remote server, choose directory, and create working directory
    self.overwrite   = True
    if self.subdir != "":
      self.overwrite = False
    self.chooseDir(overwrite=self.overwrite)

  def getDir(self):
    return self.remdir

  def die(self):
    for line in self.stdout.readlines():
      print line
    for line in self.stderr.readlines():
      print line
    exit(1)

  def setSubSubDir(self, subSubDir):
    # Only allow relative paths. Die if subDir starts with "/"
    if subSubDir[0] == "/":
      print "Error. createSubSubDir only allows relative paths:",subSubDir
      exit(1)
    self.execCmd("mkdir -p " + self.remdir + "/" + subSubDir)
    self.subsubdir = subSubDir
    return

  def connectSSH(self):
    if hasattr(self.ssh,"is_active") is False:
      self.ssh.connect(self.hostname, username=self.username)
      print "SSH connection to",self.hostname,"established" 

  def disconnectSSH(self):
    if hasattr(self.ssh,"is_active"):
      self.ssh.close()

  def chooseDir(self, overwrite=False):
    '''Determine unused directory on remote server'''
    self.connectSSH()

    if self.subdir == "":
      freeDir = False
      dirCounter = 0
      dirName = ''
      while freeDir is False:
        dirName = "dir%03d" % dirCounter
        sin, sout, serr = self.ssh.exec_command( "find " + 
          self.workdir + " -maxdepth 1 -name " + dirName + " | wc -l")
        stat = sout.channel.recv_exit_status()
        if sout.readlines()[0].strip() == "0":
          freeDir = True
        dirCounter += 1
        if dirCounter > 999:
          print "No free directory in",self.workdir
          exit(1)
      self.subdir   = dirName
    else:
      if overwrite is True:
        # If folder already exists, move it to .bak
        self.execCmd( "rm -rf " + self.workdir 
          + "/" + self.subdir + ".bak")
        self.execCmd( "mv " + self.workdir + "/" + self.subdir 
          + " " + self.workdir + "/" + self.subdir + ".bak")
    self.remdir   = self.workdir + "/" + self.subdir
    self.execCmd("mkdir -p " + self.remdir)
    self.execCmd("mkdir -p " + self.joblog)
    self.disconnectSSH()

  def remoteFileExists(self, myFile):
    baseFile = myFile
    if baseFile.find("/") != -1:
      baseFile = myFile[myFile.rfind("/")+1:]
    destFile = self.remdir + "/" + self.subsubdir + "/" + baseFile
    try:
      self.sftp.stat(destFile)
      return True
    except:
      return False

  def localFileExists(self, myFile):
    baseFile = myFile
    if baseFile.find("/") != -1:
      baseFile = myFile[myFile.rfind("/")+1:]
    oriFile = os.path.abspath(myFile)
    return os.path.exists(oriFile)

  def putFile(self, myFile):
    baseFile = myFile
    if baseFile.find("/") != -1:
      baseFile = myFile[myFile.rfind("/")+1:]
    destFile = self.remdir + "/" + self.subsubdir + "/" + baseFile
    oriFile = os.path.abspath(myFile)
    if self.bothFilesIdentical(myFile):
      return
    trials = 0
    putSuccess = False
    while trials < self.maxtrials and putSuccess == False:
      try:
        self.sftp.put(oriFile, destFile)
        putSuccess = True
      except:
        trials += 1
        time.sleep(1)
        continue
      break
    if trials == self.maxtrials:
      print "Error. Can't copy", myFile
      exit(1)
    # print "copied",baseFile,"to the remote server"
    return

  def getFile(self, myFile):
    baseFile = myFile
    if baseFile.find("/") != -1:
      baseFile = myFile[myFile.rfind("/")+1:]
    remFile = self.remdir + "/" + self.subsubdir + "/" + baseFile
    # We first check whether the file exists on the local machine. If it is,
    # no need to copy.
    if self.bothFilesIdentical(myFile):
      return
    trials = 0
    getSuccess = False
    while trials < self.maxtrials and getSuccess == False:
      try:
        self.sftp.get(remFile, self.locdir + "/" + baseFile)
        getSuccess = True
      except:
        trials += 1
        time.sleep(1)
        continue
      break
    if trials == self.maxtrials:
      print "Error. Can't copy", myFile
      exit(1)
    # print "copied",baseFile,"from the remote server"
    return

  def bothFilesIdentical(self, myFile):
    # Checks whether myFile is present on both the local and remote machines
    # and if they're identical using a MD5 checksum.
    baseFile = myFile
    if baseFile.find("/") != -1:
      baseFile = myFile[myFile.rfind("/")+1:]
    remFile = self.remdir + "/" + self.subsubdir + "/" + baseFile
    locFile = self.locdir + "/" + baseFile
    if self.remoteFileExists(myFile) is False:
      return False
    if self.localFileExists(myFile) is False:
      return False
    # Both files exist. Check the MD5 checksum
    md5Loc = subprocess.check_output("md5sum " + locFile, 
      shell=True).split()
    if len(md5Loc) != 2:
      # Should be of the form 
      # <MD5SUM> file.name
      return False
    md5Loc = md5Loc[0]
    if self.execCmd("md5sum " + remFile) != 0:
      # Non-zero exit status
      return False
    md5Rem = self.stdout.readlines()[0].split()[0]
    if md5Loc == md5Rem:
      return True
    else:
      return False

  def delFile(self, myFile, force=False):
    baseFile = myFile
    if baseFile.find("/") != -1:
      baseFile = myFile[myFile.rfind("/")+1:]
    remFile = self.remdir + "/" + self.subsubdir + "/" + baseFile
    trials = 0
    delSuccess = False
    while trials < self.maxtrials and delSuccess == False:
      try:
        self.sftp.remove(remFile)
        delSuccess = True
      except:
        trials += 1
        time.sleep(1)
        if force == False:
          print "Warning: couldn't delete remote file",remFile
          delSuccess = True
        continue
      break
    if trials == self.maxtrials:
      return False
    return True     

  def execCmd(self, cmd):
    self.stdin, self.stdout, self.stderr = self.ssh.exec_command(cmd)
    # The following is a blocking command.
    return self.stdout.channel.recv_exit_status()

  def submitJob(self, jobName, numProc, inpCmd, dependID=0):
    # Submit job to queueing system. Return job ID.
    queuesub   = ""
    numprocsub = ""
    depend     = ""
    if int(numProc) > 1:
      numprocsub = self.numprocflag + " " + str(numProc)
      if int(numProc) >= int(self.queuespecn):
        queuesub = "-q " + self.queuespec
    if dependID > 0:
      depend = "-hold_jid " + str(dependID)
    trials = 0
    subSuccess = False
    while trials < self.maxtrials and subSuccess == False:
      status = self.execCmd("qsub -S /bin/sh -cwd -N " + jobName \
        + " -j y " + numprocsub + " " + queuesub + " " + depend \
        + " -o " + self.joblog + "/" + jobName + ".log " 
        + inpCmd)
      if status == 0:
        subSuccess = True
      else:
        trials += 1
        time.sleep(1)
    if subSuccess is False:
      print "Error: qsub submission failed. Error code", status
      self.die()
    # Return job ID
    self.execCmd("qstat | grep " + self.username + " | grep " \
      + jobName[:10] + " | awk '{print $" + self.quejobidcol + "}'")
    time.sleep(2)
    return int(self.stdout.readlines()[0].split()[0]) 

  def jobIsRunning(self, jobName):
    if jobName == "":
      return False
    # Has the job experienced an error when running the script? Look for "Eqw"
    # status.
    self.execCmd("qstat | grep " + self.username + " | grep " \
      + str(jobName) + " | grep Eqw")
    numLines = int(len(self.stdout.readlines()))
    if numLines > 0:
      # Delete this job
      self.execCmd("qdel " + str(jobName))
      # We return true in case another instance of this job is currently
      # running. This will be checked during the next call to this routine.
      return True
    self.execCmd("qstat | grep " + self.username + " | grep " \
      + str(jobName))
    numLines = int(len(self.stdout.readlines()))
    if numLines > 0:
      return True
    else:
      return False

  def assertRemoteProgramIsReachable(self, progName):
    # Assert that progName is reachable on remote machine
    self.connectSSH()
    if self.execCmd("type " + progName + " > /dev/null") != 0:
      print "Error. Can't find " + progName + " on remote machine."
      self.die()
    self.disconnectSSH()

  def delRemoteSubDir(self):
    self.execCmd("rm -rf " + self.remdir)

  def delRemoteSubSubDir(self):
    self.execCmd("rm -rf " + self.remdir + "/" + self.subsubdir)

  def getStdin(self):
    return self.stdin.readlines()

  def getStdout(self):
    return self.stdout.readlines()

  def getStderr(self):
    return self.stderr.readlines()

  def __del__(self):
    self.disconnectSSH()


if __name__ == "__main__":
  # Testing with verdi
  server   = 'verdi'
  cmd = "hostname; which charmmsub"
  remoteCmd = RunCommandRemotely(server, 'dir000000')
  remoteCmd.execCmd(cmd)
  remoteCmd.putFile('hosts.ini')
  remoteCmd.getFile('hosts.ini')
  print remoteCmd.getStdout()


'''Misc routines'''

import os
import subprocess

def readInFromFile(fileName):
  '''Read in fileName and output string containing whole content.'''
  try:
    f = open(fileName,'r')
    s = f.readlines()
    f.close()
  except IOError, e:
    raise "I/O Error",e
  return s

def grep(fileName, query):
  '''Perform grep-type search for query in file fileName.'''
  s = readInFromFile(fileName)
  ret = []
  for i in range(len(s)):
    if str(query) in s[i]:
      ret.append(s[i])
  return ret

def substituteStringInArray(array, oriString, subString):
  '''Substitute string oriString for subString throughout array.'''
  retArray = array
  for i in range(len(retArray)):
    while str(oriString) in retArray[i]:
      pos = retArray[i].find(str(oriString))
      length = len(str(oriString))
      retArray[i] = retArray[i][:pos] + str(subString) \
        + retArray[i][pos+length:]
  return retArray


def writeOutToFile(array, fileName):
  '''Write out array to file fileName.'''
  f = open(fileName,'w')
  for i in range(len(array)):
    f.write(str(array[i]) + "\n")
  f.close()
  return 

def writeOutToFile2D(array1, array2, fileName):
  '''Write out array1 and array2 to file fileName.'''
  if len (array1) != len(array2):
    print "# Error. Can't write to file: Arrays of different sizes."
    exit(1)
  f = open(fileName,'w')
  for i in range(len(array1)):
    f.write(str(array1[i]) + "\t" + str(array2[i]) + "\n")
  f.close()
  return

def assertProgramIsReachable(progName):  
  '''Assert that program progName is reachable on local machine.'''
  if os.system("type " + progName + " > /dev/null") != 0:
      print "# Error. Can't find " + progName + " on local machine."
      exit(1)
  return 

def findFilesInLocalDir(subString):
  # Return all local files that contain subString
  locFiles = subprocess.check_output(
    "find . -depth ! -path \"*/\.*\" -name \"*" + subString 
      + "*\" ", shell=True).split("\n")
  return locFiles

def removeFile(myFile):
  subprocess.check_output("rm -f " + myFile, shell=True)
  return 

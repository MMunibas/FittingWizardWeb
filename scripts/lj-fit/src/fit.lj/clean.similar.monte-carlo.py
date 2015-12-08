#!/usr/bin/env python
# Read output file from fit.LJ.monte-carlo.py and remove similar entries.
#
# Tristan BEREAU (2013)

import sys,os,math

def usage():
    print "clean.similar.monte-carlo.py [-e N] [-s N] file.dat"
    print "Options:"
    print "   -e N: set sum of epsilon differences to N"
    print "   -s N: set sum of sigma   differences to N"
    exit(1)

def readFile(fileIn):
    try:
        f = open(fileIn,'r')
        s = f.readlines()
        f.close()
    except IOError, e:
        raise "I/O Error",e
    return s

def epsdiff(line1,line2,thres,numAtms):
    # Read two sets of parameters. Return false if the difference is smaller
    # than the threshold.
    total = 0.0
    for atom in range(numAtms):
        total += math.sqrt((float(line1.split()[1+2*atom])-float(line2.split()[1+2*atom]))**2)
    if total < thres:
        return False
    else:
        return True

def rmindiff(line1,line2,thres,numAtms):
    # Read two sets of parameters. Return false if the difference is smaller
    # than the threshold.
    total = 0.0
    for atom in range(numAtms):
        total += math.sqrt((float(line1.split()[2+2*atom])-float(line2.split()[2+2*atom]))**2)
    if total < thres:
        return False
    else:
        return True

def main():
    epsthres=0.05
    rminthres=0.2
    # Parse command-line arguments
    if len(sys.argv) < 2:
        usage()
    argvIndex = 1
    lastFlag = ''
    mcin = ''
    while argvIndex < len(sys.argv):
        if sys.argv[argvIndex] in ['-h', '--help']:
            usage()
        elif sys.argv[argvIndex][0] == '-':
            lastFlag = sys.argv[argvIndex]
        else:
            if lastFlag == '-e':
                epsthres = float(sys.argv[argvIndex])
                lastFlag = ''
            elif lastFlag == '-s':
                rminthres = float(sys.argv[argvIndex])
                lastFlag = ''
            else:
                # assume we're reading the data file
                mcin = sys.argv[argvIndex]
        argvIndex += 1
    mcindat = readFile(mcin)
    datalines = []
    outputdat = []
    counter=0
    for line in mcindat:
        if line[0] == '#':
            sys.stdout.write(line)
        else:
            # Fill up datalines array
            datalines.append(counter)
            outputdat.append(True)
        counter+=1
    numAtms=int((len(mcindat[datalines[0]].split())-1)/2)
    for ele_i in range(len(datalines)):
        if outputdat[ele_i] == True:
            for ele_j in range(ele_i+1,len(datalines)):
                if outputdat[ele_j] == True:
                    if not epsdiff(mcindat[datalines[ele_i]],
                        mcindat[datalines[ele_j]],epsthres,numAtms):
                        outputdat[ele_j] = False
                    if not rmindiff(mcindat[datalines[ele_i]],
                        mcindat[datalines[ele_j]],epsthres,numAtms):
                        outputdat[ele_j] = False
    for ele_i in range(len(datalines)):
        if outputdat[ele_i] == True:
            sys.stdout.write(mcindat[datalines[ele_i]])

if __name__ == '__main__':
    main()
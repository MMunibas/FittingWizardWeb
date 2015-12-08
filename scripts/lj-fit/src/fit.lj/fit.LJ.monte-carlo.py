#!/usr/bin/env python
#
# Load one/multiple molecule(s), parameter file. Vary all parameters to yield
# best performers wrt ab initio calculation of molecule + water dimer
# complexes.
#
# Tristan Bereau, Dec. 2012
#

import sys,os,random,subprocess,math
dir=os.path.dirname(os.path.realpath(__file__))

def usage():
    print "fit.LJ.monte-carlo.py file.par -e E.comb1.dat [E.comb2.dat [...]]"
    print "   -f dimer1.ljf [dimer2.ljf [...]] -x TYPE [TYPE [...]] [-t temperature]"
    print "  file.par:    parameter file"
    print "  E.comb#.dat: list of E.comb.dat files (QM, elev, vdw energies of individual frames)"
    print "  dimer#.ljf:  list of dimer.ljf files (LJ inverse distances by atom type)"
    print "  TYPE: atom type to be excluded from the MC scheme"
    print "  -t temperature: set temperature for Monte Carlo simulation"
    exit(1)

def readFile(fileIn):
    try:
        f = open(fileIn,'r')
        s = f.readlines()
        f.close()
    except IOError, e:
        raise "I/O Error",e
    return s

def getAtomTypes(fList,xList):
    atomTypes = []
    exclTypes = ['HT','OT']
    for dimerFile in fList:
        for strHeader in readFile(dimerFile)[0].split():
            pair = strHeader[:strHeader.find('_')]
            if pair.find(':') != -1:
                ele1 = pair[:pair.find(':')]
                ele2 = pair[pair.find(':')+1:]
                if ele1 not in atomTypes and ele1 not in exclTypes:
                    atomTypes.append(ele1)
                if ele2 not in atomTypes and ele2 not in exclTypes:
                    atomTypes.append(ele2)
    print "#Optimize atom types",atomTypes
    for x in xList:
        if x not in atomTypes:
            print "Error: can't find",x,"in .ljf files"
            exit(1)
    return atomTypes

def scanAtomTypes(atomTypes,eList,fList,prevAT=[],prevEps=[],prevSig=[]):
    # Systematic scan of epsilon/sigma for all atom types.
    epsmin = 0.01
    epsmax = 0.30
    epsstp = 0.02
    sigmin = 0.5
    sigmax = 2.5
    sigstp = 0.2
    curAtomTypes = atomTypes
    eps = epsmin
    sig = sigmin
    while eps < epsmax:
        sig = sigmin
        while sig < sigmax:
            if len(atomTypes) > 1:
                scanAtomTypes(atomTypes[1:],eList,fList,prevAT+[atomTypes[0]],prevEps+[eps],prevSig+[sig])
            else:
                prms = {}
                for i in range(len(prevAT)):
                    prms[prevAT[i]] = [prevEps[i], prevSig[i]]
                    print prevAT[i],prevEps[i],prevSig[i],
                prms[atomTypes[0]] = [eps,sig]
                energy = getEnergy(eList,fList,prms)
                print atomTypes[0],eps,sig,energy
                sys.stdout.flush()
                curAtomTypes = atomTypes
            sig += sigstp
        eps += epsstp

def varyParamsRandom(prms,xList):
    newParams = {}
    for at in prms.keys():
        newParams[at] = [prms[at][0],prms[at][1]]
    epsmin = 0.01
    epsmax = 0.60
    epsstp = 0.05
    sigmin = 0.2
    sigmax = 3.0
    sigstp = 0.5
    # Which atom
    allowedAtom = False
    while not allowedAtom:
        trialAtomID = random.randint(0,len(prms.keys())-1)
        trialAtom   = prms.keys()[trialAtomID]
        if trialAtom not in xList:
            allowedAtom = True
    # eps=0 or sig=1
    epsOrSig = random.randint(0,1)
    if epsOrSig == 0:
        eps = prms[trialAtom][0]
        epsNew = -1
        while epsNew < epsmin or epsNew > epsmax:
            epsNew = eps + random.uniform(-epsstp*.5,epsstp*.5)
        newParams[trialAtom][0] = epsNew
    else:
        sig = prms[trialAtom][1]
        sigNew = -1
        while sigNew < sigmin or sigNew > sigmax:
            sigNew = sig + random.uniform(-sigstp*.5,sigstp*.5)
        newParams[trialAtom][1] = sigNew
    return newParams


def writeTmpPrmFile(params,fileName="tmp.prm"):
    try:
        f = open(fileName,'w')
    except IOError,e:
        raise "I/O Error",e
    for i in params.keys():
        f.write("{:8s} {:7.4f} {:7.4f}\n".format(i,-1.*params[i][0],params[i][1]))
    f.close()


def getEnergy(eList,fList,params,fileName="tmp.prm"):
    totEnergy = 0.0
    writeTmpPrmFile(params,fileName)
    outFile = fileName+".out"
    # Average over molecules
    for mol in range(len(eList)):
        try:
            f = open(outFile,"w")
        except IOError,e:
            raise "I/O Error",e
        subprocess.call(["python",dir+"/fit.LJ.water.constr.py", "-ene",eList[mol],"-ljf",fList[mol],"-prm",fileName],stdout=f)
        f.close()
        try:
            f = open(outFile,'r')
            s = f.readlines()
            f.close()
        except IOError,e:
            raise "I/O Error",e
        # The RMSE of the energy is on the fifth line, second "word."
        totEnergy += float(s[4].split()[1])
    return totEnergy



def outputPrmsHeader(params):
    print "{:8s}".format("#Energy"),
    for i in params.keys():
        print "{:15s}".format(i),
    print " "


def outputEnePrms(energy,params):
    print "{:7.4f}".format(energy),
    for i in params.keys():
        print "{:7.4f} {:7.4f}".format(params[i][0],params[i][1]),
    print " "
    sys.stdout.flush()


def monteCarlo(prm,atomTypes,eList,fList,xList,sweeps,temperature=1.0):
    # Initialize parameters [eps,sig] from PAR file
    params = {}
    try:
        f = open(prm,'r')
        s = f.readlines()
        f.close
    except IOError,e:
        raise "I/O Error",e
    foundNB = False
    foundNBpos = 0
    for i in range(len(s)):
        s[i] = s[i].split()
        if len(s[i]) > 0:
            if s[i][0] == "NONBONDED":
                foundNB = True
                foundNBpos = i
    if not foundNB:
        print "Can't find NONBONDED in parameter file"
        exit(1)
    for at in atomTypes:
        for i in range(foundNBpos+1,len(s)):
            if len(s[i]) > 0:
                if s[i][0] == at:
                    params[at] = [math.fabs(float(s[i][2])),
                        float(s[i][3])]
    energy = getEnergy(eList,fList,params)

    outputPrmsHeader(params)
    # Run number of sweeps
    for i in range(sweeps):
        # 1 sweep is len(atomTypes) steps
        for j in range(len(atomTypes)-len(xList)):
            trialParams = varyParamsRandom(params,xList)
            ene = getEnergy(eList,fList,trialParams)
            delta = ene - energy
            if delta < 0 or random.random() < math.exp(-delta/temperature):
                energy = ene
                params = trialParams          
        outputEnePrms(energy,params)

def main():
    # Parse command-line arguments
    if len(sys.argv) < 5:
        usage()
    prm = sys.argv[1]
    print "#Parameter file:",prm
    eList = []
    fList = []
    # exclusion list of atom types
    xList = []
    temperature=0.5
    argvIndex = 2
    lastFlag = ''
    while argvIndex < len(sys.argv):
        if sys.argv[argvIndex] in ['-h', '--help']:
            usage()
        if sys.argv[argvIndex][0] == '-':
            lastFlag = sys.argv[argvIndex]
        else:
            if lastFlag == '-e':
                eList.append(sys.argv[argvIndex])
            elif lastFlag == '-f':
                fList.append(sys.argv[argvIndex])
            elif lastFlag == '-x':
                xList.append(sys.argv[argvIndex])
            elif lastFlag == '-t':
                temperature = float(sys.argv[argvIndex])
            else:
                print "Unrecognized option",lastFlag
        argvIndex += 1
    print "#E.comb.dat files:",eList
    print "#dimer.ljf files: ",fList

    atomTypes = getAtomTypes(fList,xList)
    #scanAtomTypes(atomTypes,eList,fList)
    monteCarlo(prm,atomTypes,eList,fList,xList,1000,temperature)

    pass


if __name__ == '__main__':
    main()

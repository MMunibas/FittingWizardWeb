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
####################
#
# Applies precalculated Multipole coefficients to molecule stored in _l.pun file

import sys
import mtp_molcontainer
import mtp_tools
import numpy

coefffile = ''
pun_infile = ''
outfile = ''

for i in range(1,len(sys.argv)):
    if sys.argv[i] == '-coeff':
        coefffile = sys.argv[i+1]
    elif sys.argv[i] == '-punin':
        pun_infile = sys.argv[i+1]
    elif sys.argv[i] == '-out':
        outfile = sys.argv[i+1]
    elif sys.argv[i] == '-h':
        print "Usage: apply_mtp_coeffs.py -coeff [file] -punin [file] [-out file]" 
        exit(0)

if coefffile == '' or pun_infile == '':
    print "Usage: apply_mtp_coeffs.py -coeff [file] -punin [file] [-out file]" 
    exit(0)

if outfile == '':
    outfile = pun_infile[:pun_infile.index('.')]+'__'+coefffile[:coefffile.index('.')]+'-coeffs.pun'

###########
# Read molecule

mols = mtp_molcontainer.MolPop()
mols.add_molecule_from_punfile(pun_infile)

###########
# Read coefficients

f = open(coefffile,'r')
fr = f.readlines()
f.close()
coeffs = dict((i.split()[0], float(i.split()[1])) for i in fr)

###########
# Convert coeffs to mtp_tools acceptable dict

atypelist = list(set([atom.atype for atom in mols.molecules[0].atoms]))

coeff_set = {}
for atype in atypelist:
    coeff_set[(atype,'chrg')] = numpy.zeros(1)
    coeff_set[(atype,'dloc')] = numpy.zeros(3)
    coeff_set[(atype,'Qloc')] = numpy.zeros(5)
    
for key, value in coeffs.iteritems():
    atype = key[:key.index('_')]
    mtp_type = key[key.index('_')+1:]
    if mtp_type == 'Q00':
       coeff_set[(atype,'chrg')] = numpy.array([value])
    elif mtp_type == 'Q10':
       coeff_set[(atype,'dloc')][0] = value    
    elif mtp_type == 'Q11c':
       coeff_set[(atype,'dloc')][1] = value    
    elif mtp_type == 'Q11s':
       coeff_set[(atype,'dloc')][2] = value    
    elif mtp_type == 'Q20':
       coeff_set[(atype,'Qloc')][0] = value    
    elif mtp_type == 'Q21c':
       coeff_set[(atype,'Qloc')][1] = value    
    elif mtp_type == 'Q21s':
       coeff_set[(atype,'Qloc')][2] = value    
    elif mtp_type == 'Q22c':
       coeff_set[(atype,'Qloc')][3] = value    
    elif mtp_type == 'Q22s':
       coeff_set[(atype,'Qloc')][4] = value    
    else:
        print "Key Error in Coefficient file:",key

###########
# Apply MTP_coefficients

mols.setMTPs(coeff_set)

###########
# Write File to output

mols.molecules[0].write2punfile(filename=outfile)

print
print "Output file with MTP Coefficients from",coefffile
print " applied to",pun_infile,"written to: ",outfile
print

exit(0)












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
# This script transforms a .pun file into a charmm-readable .lpun file.
# Optimized Parameters can be given

def pun_2_charmm_lpun(punfile, lpunfile):
    
    import sys
    from . import mtp_tools
    
    parmfile = ''
    
    ##############
    # Read input
    
#    for i in range(len(sys.argv)):
#      if sys.argv[i] == '-pun':
#        punfile = sys.argv[i+1]
#      elif sys.argv[i] == '-par':
#        parmfile = sys.argv[i+1]
#      elif sys.argv[i] == '-h':
#        print("Usage: python pun2charmmlpun.py -pun [file] [-par [parfile]] [-h]")
#        exit(0)
    
    if punfile == '':
      print("Usage: python pun2charmmlpun.py -pun [file] [-par [parfile]] [-h]")
      exit(0)
    
    #############
    # Check that the file does not end in .lpun, otherwise quit.
    
    if punfile[punfile.rindex('.'):] == '.lpun':
      print("Error: the script will generate a .lpun file, please rename current file.")
      exit(1)
    
    # Read prmfile if given
    
    prms = {}
    if parmfile != '':
      import numpy
      f = open(parmfile,'r')
      a = f.readlines()
      f.close()
      for line in a:
        b = line.split()
        prms[(b[0][2:-2],b[1][1:-2])] = numpy.array([float(b[i+3]) for i in range(len(b)-3)])
    
    mol = mtp_tools.molecule()
    mol.readfrompunfile(punfile)
    mol.Calc_locMTP()
    
    if parmfile != '':
      for atom in mol.atoms:
        atom.chrg = prms[(atom.atype,'chrg')]
        atom.dloc = prms[(atom.atype,'dloc')]
        atom.Qloc = prms[(atom.atype,'Qloc')]
      mol.Calc_gloMTP()
    
    mol.adjust_charge()
    mol.write_localized_mtp_file(lpunfile)
    
     

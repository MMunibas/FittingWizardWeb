import os
from subprocess import call

def run_xyz_to_dcm_par(xyzfile, axisfile, parfile):

  scriptdir=os.path.dirname(os.path.realpath(__file__))
  script=scriptdir+"/comb-xyz-to-dcm.pl"
  call([script,xyzfile,axisfile,parfile])


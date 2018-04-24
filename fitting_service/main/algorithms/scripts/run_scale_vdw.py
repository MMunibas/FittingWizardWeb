import os
from subprocess import call

def run_scale_vdw(slu, top, par, sigfac, epsfac, inpdir):

  scriptdir=os.path.dirname(os.path.realpath(__file__))
  script=scriptdir+"/scale-vdw-inp.pl"
  call([script,slu,top,par,str(sigfac),str(epsfac),inpdir])

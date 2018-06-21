import os
from subprocess import call

def run_scale_vdw(slu, top, par, sigfac, epsfac, inpdir):

  scaled_par="scaled.par"
  scriptdir=os.path.dirname(os.path.realpath(__file__))
  script=scriptdir+"/scale-vdw-inp.pl"
  print("hello ",script,slu,top,par,str(sigfac),str(epsfac),inpdir,scaled_par)
  call([script,slu,top,par,str(sigfac),str(epsfac),inpdir,scaled_par])

  return scaled_par

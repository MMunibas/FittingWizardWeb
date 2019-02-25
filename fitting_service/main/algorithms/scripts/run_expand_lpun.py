import os
from subprocess import call

def run_expand_lpun(lpun, nmol, inpdir):

  pureliq_lpun_name="pureliquid.lpun"
  pureliq_lpun=inpdir + pureliq_lpun_name
  scriptdir=os.path.dirname(os.path.realpath(__file__))
  script=scriptdir+"/expand-lpun-for-identical-mols.sh"
  call([script,lpun,nmol,pureliq_lpun])

  return pureliq_lpun_name

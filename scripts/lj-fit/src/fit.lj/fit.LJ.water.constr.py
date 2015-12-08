#!/usr/bin/env python
#
# Non-linear least-squares fitting of the LJ parameters of a system molecule-H2O
# dimer.  With constraints.  Optional: bootstrap to estimate variability of
# parameters. 
#
# $0 -ene E.comb.dat -ljf dimer.ljf [-out FILE] [-prm FILE] 
#    [-bs NUM FILE]] [-v]
#
# out: Output MP2, PC, LJ_old, LJ_new1, LJ_new2
# (LJ_new* corresponds to iteration #*)
# prm: input parameters for simple calculation of LJ energies (no fit). Can be
#      used in combination with bootstrap.
#      file format: atom_type_i  eps_i  Rmin_i
# bs:  - number of times the bootstrap algorithm shall be run.
#      - file to export distribution of parameters (eps_1, Rmin1, eps_2,...)
# v:   verbose
#
# Hardcoded water parameters (TIP3):
#         eps       Rmin
#  OT  -0.152100  1.76820
#  HT  -0.046000  0.22450
#
# Assumes coefficients 12-6 are being fitted. 
# Uses Boltzmann-weight in fit target function at 300K.
#
# Tristan Bereau 20.2.2012
#

import os, sys, random, time
import numpy as np
from scipy import *
from scipy.optimize import fmin_cobyla
import multiprocessing

def usage():
  print "Usage:",sys.argv[0], \
      "-ene E.comb.dat -ljf dimer.ljf [-out FILE] [-prm FILE] " \
      "[-bs NUM FILE] [-v] [-unfweight] [-max E_max]"
  exit(1)

ene = ''
ljf = ''
out = ''
prm = ''
bsn = 0
bsf = ''
ver = False
unfweight = False
max_e = 100

if len(sys.argv) < 5:
  usage()
for i in range(len(sys.argv)):
  if sys.argv[1] == '-h':
    usage()
  if sys.argv[i] == '-ene':
    ene = sys.argv[i+1]
  if sys.argv[i] == '-ljf':
    ljf = sys.argv[i+1]
  if sys.argv[i] == '-out':
    out = sys.argv[i+1]
  if sys.argv[i] == '-prm':
    prm = sys.argv[i+1]
  if sys.argv[i] == '-bs':
    bsn = int(sys.argv[i+1])
    bsf = sys.argv[i+2]
  if sys.argv[i] == '-v':
    ver = True
  if sys.argv[i] == '-unfweight':
    print "Uniform weight selected"
    unfweight = True
  if sys.argv[i] == '-max':
    max_e = float(sys.argv[i+1])

try:
  f = open(ene,'r')
  s_ene = f.readlines()
  f.close()
except IOError as (errno,strerror):
  print "I/O error({0}): {1}".format(errno, strerror)
try:
  f = open(ljf,'r')
  s_ljf = f.readlines()
  f.close()
except IOError as (errno,strerror):
  print "I/O error({0}): {1}".format(errno, strerror)

energies = []
ljdists  = []
ljcoeffs = []
ljvals   = []
filelist = []

# Read in coefficients from ljf file header
str_ljf = s_ljf[0].split()
for i in range(1,len(str_ljf)):
  ljcoeffs.append(str_ljf[i])
  ljvals.append(int(str_ljf[i][str_ljf[i].rfind('_')+1:len(str_ljf[i])]))

#TIP3 parameters
tip3params = {}
tip3params["OT"] = [-0.152100,  1.76820]
tip3params["HT"] = [-0.046000,  0.22450]
# Extract atom types from ljcoeffs and mapping from atom types to ljcoeffs
atom_t  = ["OT","HT"]
mapping = []
for i in range(len(ljcoeffs)):
  t1 = ljcoeffs[i][0:ljcoeffs[i].find(':')]
  t2 = ljcoeffs[i][ljcoeffs[i].find(':')+1:ljcoeffs[i].rfind('_')]
  if t1 not in atom_t:
    atom_t.append(t1)
  if t2 not in atom_t:
    atom_t.append(t2)
  if t1 == "OT" or t1 == "HT":
    mapping.append([atom_t.index(t2), atom_t.index(t1)])
  else:
    mapping.append([atom_t.index(t1), atom_t.index(t2)])

num_types = len(atom_t)

# Create initial values for parameters (for Lorentz-Berthelot
# optimization). "-2" to remove TIP3 atom types
opt_p_LB = []
for i in range(len(atom_t)-2):
    # epsilon_i
    opt_p_LB.append(0.3)
    # Rmin_i
    opt_p_LB.append(1.3)

# loop over ljf, this file may be longer (in case some QM calculations
# have been skipped).  Only keep conformations that are found both in ljf and
# ene files.
for i in range(0,len(s_ljf)):
  str_ljf = s_ljf[i].split()
  file_ljf = str_ljf[0][str_ljf[0].find('/')+1:]
  for j in range(0,len(s_ene)):
    str_ene = s_ene[j].split()
    file_ene = str_ene[0][str_ene[0].find('/')+1:]
    if file_ljf in [file_ene, file_ene+'di']:
      filelist.append(file_ljf)
      eqm = float(str_ene[1])
      eel = float(str_ene[2])
      elj = float(str_ene[3])
      if eqm < max_e:
        energies.append([eqm, 
                         eel,
                         elj])
        tmp = []
        for k in range(1,len(str_ljf)):
          tmp.append(float(str_ljf[k]))
        ljdists.append(tmp)
        
E_qm = np.transpose(energies)[0]
E_el = np.transpose(energies)[1]
E_lj = np.transpose(energies)[2]

# Optimize E_lj = E_qm - E_elec
y = E_qm - E_el

lenEqm = len(E_qm)

def output_goodness(Elj):
  ss_tot=((y-y.mean())**2).sum()
  ss_err=((y-np.array(Elj))**2).sum()
  rsquared=1-(ss_err/ss_tot)
  print "----------------------------"
  print "       R^2: %5.2f" % rsquared
  print "      RMSE: %7.4f kcal/mol" % (sqrt(sum([ (y[i]-Elj[i])**2 
                                                     for i in
                                                     range(lenEqm)])/ lenEqm))
  print "Boltz-RMSE: %7.4f kcal/mol" % (sqrt(sum([exp(-.593*E_qm[i]) * 
                                                    (y[i]-Elj[i])**2 for i in
                                                    range(lenEqm)])/ 
                                               sum([exp(-.593*E_qm[i]) 
                                                    for i in range(lenEqm)])) )
  print "       MAE: %7.4f kcal/mol" % (sum([abs(y[i]-Elj[i]) for i in range(lenEqm)])/lenEqm)
  print " Boltz-MAE: %7.4f kcal/mol" % ( sum([exp(-.593*E_qm[i]) *
                                                abs(y[i]-Elj[i]) for i in 
                                                range(lenEqm)]) /
                                           sum([exp(-.593*E_qm[i]) 
                                                for i in range(lenEqm)]))
  print "----------------------------"

if ver:
  print "Number of points included in the fitting routine :",len(y)
  output_goodness(E_lj)

# Lorentz-Berthelot epsilon
def epsilon_ij(eps_i, eps_j):
    return np.sqrt(abs(eps_i * eps_j))

# Lorentz-Berthelot Rmin
def Rmin_ij(Rmin_i, Rmin_j):
    return abs(Rmin_i) + abs(Rmin_j)

# LJ function
def ljeval_LB(x, p):
  func = 0.    
  # x and mapping *must* have the same size
  for i in range(len(mapping)):   
    eps_ij = epsilon_ij( p[ (mapping[i][0]-2) *2   ], 
                         tip3params[atom_t[mapping[i][1]]][0] )
    rmin_ij = Rmin_ij  ( p[ (mapping[i][0]-2) *2+1 ],
                         tip3params[atom_t[mapping[i][1]]][1] )
    if ljvals[i] == 12:
      func +=       eps_ij * (rmin_ij**12 * x[i])
    elif ljvals[i] == 6:
      func += -2. * eps_ij * (rmin_ij**6  * x[i])
    else:
      print "LJ coeff needs to be 12 or 6."
      exit(1)
  return func

def output_energies(Elj):
  if ver:  print "Saving energies to file",out
  f = open(out,'w')
  f.write("%-8s\t%-8s\t%-8s\t%-8s\n" %("#E_qm","E_pc","E_lj","file"))
  for i in range(lenEqm):
    f.write('%8.3f\t'%E_qm[i]+'%8.3f\t'%E_el[i]+'%8.3f\t'%Elj[i]+'%s\n'%filelist[i])
  f.close()
  
# In case prm is activated, run energy calculations with input parameters and
# exit script
if prm != '':
  try:
    print "Reading parameters from file",prm
    f = open(prm,'r')
    s_prm = f.readlines()
    f.close()
  except IOError as (errno,strerror):
    print "I/O error({0}): {1}".format(errno, strerror)
  for i in range(len(s_prm)):
    str_prm = s_prm[i].split()
    for j in range(2,len(atom_t)):
      if atom_t[j] == str_prm[0]:
        opt_p_LB[2*(j-2)] = float(str_prm[1])
        opt_p_LB[2*(j-2)+1] = float(str_prm[2])

# Weight function #1
# Metropolis-type weight
# based on E_qm-E_el
def weight1(E):
  w = []
  for i in range(len(E)):
    if E[i] < 0:
      w.append(1.)
    else:
      w.append(exp(-.84317*E[i]))
  return w

# Weight function #2
# Metropolis-type weight
# based on min(E_qm-E_el,E_lj)
def weight2(Eqm,Eel,Elj):
  w = []
  for i in range(len(Eqm)):
    e=min(Eqm[i]-Eel[i],Elj[i])
    if e < 0:
      w.append(1.)
    else:
      w.append(exp(-.84317*e))
  return w

# Trivial weight function
def weight0(E):
  w = []
  for i in range(len(E)):
    w.append(1.)
  return w

# function to optimize -- includes a Boltzmann weight 
# factor one half included because LS routine squares target function.
# prefactor: .5*beta
# beta = 1/(kT)
# prefactor: .84317 kcal/mol
def f_opt_LB_cobyla(p, E_qm, E_el, weight, x):
  err = (E_qm - E_el - ljeval_LB(x, p)) * weight
  return (err**2).sum()

print "Optimization with LB mixing rule and constraints"

def vdw_radii(atom):
  if atom[0] == 'C':
    return 1.50
  elif atom in ['H', 'HX']:
    return 0.70
  elif atom[0] == 'H':
    return 0.70
  elif atom[0] == 'N':
    return 1.30
  elif atom[0] == 'O':
    return 1.30
  elif atom[0] == 'F':
    return 1.00
  elif atom[0] == 'P':
    return 1.50
  elif atom[0] == 'S':
    return 1.50
  elif atom[0:2].lower() == 'br':
    return 1.50
  elif atom[0] == 'I':
    return 1.50
  elif atom[0:2].lower() == 'cl':
    return 1.50

def cons_j(j):
  if j % 2:
    # Constraint on Rmin
    #return lambda p, *args: p[j]-1.20
    return lambda p, *args: p[j]-vdw_radii(atom_t[j/2+2])
  else:
    # Constraint on epsilon
    return lambda p, *args: (p[j]-0.01)

cons = [cons_j(j) for j in range(len(opt_p_LB))]

def output_parameters(parms):
  print "  type  epsilon  Rmin/2"
  for i in range(len(atom_t)-2):
    print "%6s  %7.4f %7.4f" \
        % (atom_t[i+2],-1.*abs(parms[2*i]), 
           abs(parms[2*i+1]))

def fitting(Eqm,Eel,ljd,uniform_weight,output=True):
  if uniform_weight == True:
    x = fmin_cobyla(f_opt_LB_cobyla, opt_p_LB,
                    cons, args=(Eqm, Eel,
                                np.array(weight0(Eqm)), 
                                np.transpose(ljd)),
                    maxfun=100000, iprint=0)
    
    opt_LJ = ljeval_LB(np.transpose(ljdists),x)

    if output:
      output_parameters(x)
      output_goodness(opt_LJ)
    if out != '': output_energies(opt_LJ)

    return x

  else:
    # 1st iteration
    x = fmin_cobyla(f_opt_LB_cobyla, opt_p_LB,
                    cons, args=(Eqm, Eel,
                                np.array(weight1(Eqm-Eel)), 
                                np.transpose(ljd)),
                    maxfun=100000, iprint=0)
    
    opt_LJ = ljeval_LB(np.transpose(ljdists),x)
    if ver:
      print "Iteration #1:"
      output_parameters(x)
      output_goodness(opt_LJ)
      
    # 2nd iteration
    x = fmin_cobyla(f_opt_LB_cobyla, opt_p_LB,
                    cons, args=(Eqm, Eel,
                                np.array(weight2(Eqm,Eel,opt_LJ)), 
                                np.transpose(ljd)),
                    maxfun=100000, iprint=0)
    
    opt_LJ2 = ljeval_LB(np.transpose(ljdists),x)
    
    if ver: print "Iteration #2:"
    if output:
      output_parameters(x)
      output_goodness(opt_LJ2)
    if out != '': output_energies(opt_LJ2)

    return x

def bootstrap_iter(i):
  string = "Running bootstrap iteration #%4d of %4d" %(i,bsn)
  sys.stdout.write ("\r"+string)
  sys.stdout.flush ()
  # Generate new distributions
  Eqm = []
  Eel = []
  ljd = []
  for j in range(lenEqm):
    index = rnd[i][j]
    Eqm.append(E_qm[index])
    Eel.append(E_el[index])
    ljd.append(ljdists[index])
  Eqm = np.array(Eqm)
  Eel = np.array(Eel)
  ljd = np.array(ljd)
  x_it = fitting(Eqm,Eel,ljd,unfweight,False)
  # update initial parameters for next fit
  opt_p_LB = x_it
  # Calculate statistics (RMSE and MAE)
  Elj = ljeval_LB(np.transpose(ljd),x_it)
  y1 = Eqm-Eel
  ss_tot=((y1-y1.mean())**2).sum()
  ss_err=((y1-np.array(Elj))**2).sum()
  rsquared=1-(ss_err/ss_tot)
  rmse = sqrt(sum([ (y1[i]-Elj[i])**2
                    for i in
                    range(lenEqm) ])/ lenEqm)
  mae  = sum([abs(y1[i]-Elj[i]) for i in range(lenEqm)])/lenEqm
  return [x_it,rsquared,rmse,mae]

def bs_accuracy(i):
  # Generate new distributions
  Eqm = []
  Eel = []
  ljd = []
  for j in range(lenEqm):
    index = rnd[i][j]
    Eqm.append(E_qm[index])
    Eel.append(E_el[index])
    ljd.append(ljdists[index])
  Eqm = np.array(Eqm)
  Eel = np.array(Eel)
  ljd = np.array(ljd)
  Elj = ljeval_LB(np.transpose(ljd),opt_p_LB)
  y1 = Eqm-Eel
  ss_tot=((y1-y1.mean())**2).sum()
  ss_err=((y1-np.array(Elj))**2).sum()
  rsquared=1-(ss_err/ss_tot)
  rmse = sqrt(sum([ (y1[i]-Elj[i])**2
                    for i in
                    range(lenEqm) ])/ lenEqm)
  mae  = sum([abs(y1[i]-Elj[i]) for i in range(lenEqm)])/lenEqm
  return [opt_p_LB,rsquared,rmse,mae]

random.seed()

def bootstrap():
  tasks = range(bsn)
  # Generate list of random numbers (can't parallelize that due to
  # time-dependent seed)
  for i in tasks:
    tmp = []
    for j in range(lenEqm):
      tmp.append(random.randint(9,lenEqm-1))
    rnd.append(tmp)

  # Open output file
  f = open(bsf,'w')
  f.write("#")
  for i in range(len(atom_t)-2):
    f.write("%4s.e  "%atom_t[i+2])
    f.write("%4s.R  "%atom_t[i+2])
  f.write("\n")

  # Calculate new parameters
  bs_results = []
  if prm == '':
    t0 = time.time()

    pool = multiprocessing.Pool(None)
    r = pool.map_async(bootstrap_iter, tasks, callback=bs_results.extend)
    bs_results = r.get(0xFFFF)

    sys.stdout.write ("\rbootstrap completed in %0.3f secs       \n" 
                      % (time.time()-t0))
  else:
    # Calculate accuracy of prm parameters    
    for i in tasks:
      bs_results.append(bs_accuracy(i))

  # Output parameters to file
  for j in tasks:
    for i in range(len(atom_t)-2):
      f.write("%7.4f " % (-1.*abs(bs_results[j][0][2*i])))
      f.write("%7.4f " % (abs(bs_results[j][0][2*i+1])))
    f.write("\n")
  f.close()

  print "Parameters exported to file",bsf
  
  # Calculate statistics for parameters.
  print "------- Parameters after bootstrap --------"
  print "  type  epsilon  (error)   Rmin/2  (error) "
  print "-------------------------------------------"
  for i in range(len(atom_t)-2):
    eps_list = np.array([bs_results[j][0][2*i] for j in tasks])
    Rmi_list = np.array([bs_results[j][0][2*i+1] for j in tasks])
    print "%6s  %7.4f (%7.4f) %7.4f (%7.4f)" \
        % (atom_t[i+2],-1.*abs(np.average(eps_list)), 
           eps_list.std()/sqrt(len(tasks)),
           abs(np.average(Rmi_list)),
           Rmi_list.std()/sqrt(len(tasks)))
  print "-------------------------------------------"
  rsqu_list = np.array([bs_results[j][1] for j in tasks])
  rmse_list = np.array([bs_results[j][2] for j in tasks])
  mae_list  = np.array([bs_results[j][3] for j in tasks])
  print "   R^2: %7.4f (%7.4f)" % \
      (np.average(rsqu_list), rsqu_list.std())
  print "  RMSE: %7.4f (%7.4f)" % \
      (np.average(rmse_list), rmse_list.std())
  print "   MAE: %7.4f (%7.4f)" % \
      (np.average(mae_list), mae_list.std())
        

if bsn > 0:
  rnd = []
  bootstrap()
else:
  # Not bootstrap, standard 2-iteration fitting.
  if prm != '':
    opt_LJ = ljeval_LB(np.transpose(ljdists),opt_p_LB)
  
    output_goodness(opt_LJ)
    if out != '': output_energies(opt_LJ)
    exit(0)
  else:
    xtest = fitting(E_qm,E_el,ljdists,unfweight)

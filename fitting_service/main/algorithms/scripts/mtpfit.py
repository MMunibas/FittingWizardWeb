#!/usr/bin/env python3

def mtpfit(pot_cube_file, dens_cube_file, lmax, qtot, mtpl_file, results):
  import sys
  import numpy as np
  from .cube import GaussianCube
  
  lam  = 1e-6 #regularization term (keeps coefficients small), 0 for no regularization
  f = open(mtpl_file,'w')  
    
  #############
  # Read command line input
  
  #def usage():
  #    print ("Usage: python3 fit.py -pot [potcube] -dens [denscube] [-lmax [lmax]]",\
  #          "[-qtot [qtot]]")
  
  #read cube files
  dens_cube = GaussianCube(dens_cube_file)
  pot_cube = GaussianCube(pot_cube_file)
  
  #extract grid values
  dens_xyz, dens_val = dens_cube.to_grid_vals()
  esp_xyz, esp_val = pot_cube.to_grid_vals()
  
  #atom coordinates
  atoms_xyz = np.asarray(pot_cube.atomsXYZ)
  
  #number of atoms
  Natom = atoms_xyz.shape[0]
  
  #number of spherical harmonics coefficients
  L = 0
  for l in range(lmax+1):
      L += 2*l+1
  
  def extract_grid_vals(xyz, dens, esp, lower_bound=1.0e-3, upper_bound=3.162277660e-4):
      idx = np.arange(dens.size)
      idx = idx[dens < lower_bound]
      xyz  = xyz[idx]
      dens = dens[idx]
      esp  = esp[idx]
      idx = np.arange(dens.size)
      idx = idx[dens > upper_bound]
      xyz  = xyz[idx]
      dens = dens[idx]
      esp  = esp[idx]
      return xyz, esp
  
  grid_xyz, grid_val = extract_grid_vals(dens_xyz, dens_val, esp_val)
  
  #gridsize
  Ngrid = grid_val.size
  
  '''print grid as xyz
  print(str(grid_val.size)+"\n")
  for xyz, val in zip(grid_xyz, grid_val):
      if val < 0:
          print("O " + str(xyz[0]) + " " + str(xyz[1]) + " " + str(xyz[2]) + " " + str(val))
      else:
          print("N " + str(xyz[0]) + " " + str(xyz[1]) + " " + str(xyz[2]) + " " + str(val))
  quit()
  #'''
  
  '''
  l,m indices of spherical harmonics
  cartesian vector between multipole center and evaluation point
  The functions are taken from the appendix of "The Theory of Intermolecular Forces" by A. J. Stone
  '''
  def multipole_esp(l, m, rvec):
      x = 0
      y = 1
      z = 2 
      rmag = np.linalg.norm(rvec)
      r = rvec/rmag
      #appropriate power law
      Rpow = 1/rmag**(l+1)
      if l == 0: #monopole
          return Rpow
      elif l == 1: #dipole
          if m == 0: #10
              return Rpow * r[z]
          elif m == 1: #11c
              return Rpow * r[x]
          elif m == -1: #11s
              return Rpow * r[y]
          else: 
              print('m =', m, ' is not supported for l =', l)
              quit()
      elif l == 2: #quadrupole
          if m == 0: #20
              return Rpow * 0.5 * (3*r[z]**2 - 1)
          elif m == 1: #21c
              return Rpow * np.sqrt(3) * r[x] * r[z]
          elif m == -1: #21s
              return Rpow * np.sqrt(3) * r[y] * r[z]
          elif m == 2: #22c
              return Rpow * 0.5 * np.sqrt(3) * (r[x]**2 - r[y]**2)
          elif m == -2: #22s
              return Rpow * np.sqrt(3) * r[x] * r[y]
          else: 
              print('m =', m, ' is not supported for l =', l)
              quit()
      elif l == 3: #octopole
          if m == 0: #30
              return Rpow * 0.5 * (5*r[z]**3 - 3*r[z])
          elif m == 1: #31c
              return Rpow * 0.25 * np.sqrt(6) * r[x] * (5*r[z]**2 - 1)
          elif m == -1: #31s
              return Rpow * 0.25 * np.sqrt(6) * r[y] * (5*r[z]**2 - 1)
          elif m == 2: #32c
              return Rpow * 0.5 * np.sqrt(15) * r[z] * (r[x]**2 - r[y]**2)
          elif m == -2: #32s
              return Rpow * np.sqrt(15) * r[x] * r[y] * r[z]
          elif m == 3: #33c
              return Rpow * 0.25 * np.sqrt(10) * r[x] * (r[x]**2 - 3*r[y]**2)
          elif m == -3: #33s
              return Rpow * 0.25 * np.sqrt(10) * r[y] * (3*r[x]**2 - r[y]**2)
          else: 
              print('m =', m, ' is not supported for l =', l)
              quit()
      elif l == 4: #hexadecapole
          if m == 0: #40
              return Rpow * 0.125 * (35*r[z]**4 - 30*r[z]**2 + 3)
          elif m == 1: #41c
              return Rpow * 0.25 * np.sqrt(10) * (7*r[x]*r[z]**3 - 3*r[x]*r[z])
          elif m == -1: #41s
              return Rpow * 0.25 * np.sqrt(10) * (7*r[y]*r[z]**3 - 3*r[y]*r[z]) 
          elif m == 2: #42c
              return Rpow * 0.25 * np.sqrt(5) * (7*r[z]**2 - 1) * (r[x]**2 - r[y]**2)
          elif m == -2: #42s
              return Rpow * 0.5 * np.sqrt(5) * (7*r[z]**2 - 1) * r[x] * r[y]
          elif m == 3: #43c
              return Rpow * 0.25 * np.sqrt(70) * r[x] * r[z] * (r[x]**2 - 3*r[y]**2)
          elif m == -3: #43s
              return Rpow * 0.25 * np.sqrt(70) * r[y] * r[z] * (3*r[x]**2 - r[y]**2)
          elif m == 4: #44c
              return Rpow * 0.125 * np.sqrt(35) * (r[x]**4 - 6*r[x]**2*r[y]**2 + r[y]**4)
          elif m == -4: #44s
              return Rpow * 0.5 * np.sqrt(35) * r[x] * r[y] * (r[x]**2 - r[y]**2)
          else: 
              print('m =', m, ' is not supported for l =', l)
              quit()
      elif l == 5: #ditriantapole
          if m == 0: #50
              return Rpow * 0.125 * (63*r[z]**5 - 70*r[z]**3 + 15*r[z])
          elif m == 1: #51c
              return Rpow * 0.125 * np.sqrt(15) * (21*r[x]*r[z]**4 - 14*r[x]*r[z]**2 + r[x])
          elif m == -1: #51s
              return Rpow * 0.125 * np.sqrt(15) * (21*r[y]*r[z]**4 - 14*r[y]*r[z]**2 + r[y])
          elif m == 2: #52c
              return Rpow * 0.25 * np.sqrt(105) * (3*r[x]**2*r[z]**3 - 3*r[y]**2*r[z]**3 - r[x]**2*r[z] + r[y]**2*r[z])
          elif m == -2: #52s
              return Rpow * 0.5 * np.sqrt(105) * (3*r[x]*r[y]*r[z]**3 - r[x]*r[y]*r[z])
          elif m == 3: #53c
              return Rpow * 0.0625 * np.sqrt(70) * (9*r[x]**3*r[z]**2 - 27*r[x]*r[y]**2*r[z]**2 - r[x]**3 + 3*r[x]*r[y]**2)
          elif m == -3: #53s
              return Rpow * 0.0625 * np.sqrt(70) * (27*r[x]**2*r[y]*r[z]**2 - 9*r[y]**3*r[z]**2 - 3*r[x]**2*r[y] + r[y]**3)
          elif m == 4: #54c
              return Rpow * 0.375 * np.sqrt(35) * (r[x]**4*r[z] - 6*r[x]**2*r[y]**2*r[z] + r[y]**4*r[z])
          elif m == -4: #54s
              return Rpow * 1.5 * np.sqrt(35) * (r[x]**3*r[y]*r[z] - r[x]*r[y]**3*r[z])
          elif m == 5: #55c
              return Rpow * 0.1875 * np.sqrt(14) * (r[x]**5 - 10*r[x]**3*r[y]**2 + 5*r[x]*r[y]**4)
          elif m == -5: #55s
              return Rpow * 0.1875 * np.sqrt(14) * (5*r[x]**4*r[y] - 10*r[x]**2*r[y]**3 + r[y]**5)
          else: 
              print('m =', m, ' is not supported for l =', l)
              quit()
      else:
          print('l =', l, ' is not supported')
          quit()
  
  #build design matrix
  A = np.zeros((Ngrid, Natom*L))
  for n in range(Ngrid):
      i = 0
      for l in range(lmax+1):
          for a in range(Natom):
              r = grid_xyz[n] - atoms_xyz[a]
              for m in range(-l,l+1):
                  A[n,i] = multipole_esp(l, m, r)
                  i += 1
  
  #build constraint matrix (so total charge is conserved)
  B = np.zeros((1,Natom*L))
  B[:,:Natom] = 1
  d = np.zeros((1))
  d[:] = qtot
  
  def lse(A, b, B, d, lam=0.0):
      """
      Equality-contrained least squares.
      The following algorithm minimizes ||Ax - b|| subject to the
      constrain Bx = d.
      Parameters
      ----------
      A : array-like, shape=[m, n]
      B : array-like, shape=[p, n]
      b : array-like, shape=[m]
      d : array-like, shape=[p]
      lam : regularization
      Reference
      ---------
      Matrix Computations, Golub & van Loan, algorithm 12.1.2
      Examples
      --------
      >>> A = np.array([[0, 1], [2, 3], [3, 4.5]])
      >>> b = np.array([1, 1])
      >>> # equality constrain: ||x|| = 1.
      >>> B = np.ones((1, 3))
      >>> d = np.ones(1)
      >>> lse(A.T, b, B, d)
      array([-0.5,  3.5, -2. ])
      """
      from scipy import linalg
      A, b, B, d = map(np.asanyarray, (A, b, B, d))
      p = B.shape[0]
      Q, R = linalg.qr(B.T)
      y = linalg.solve_triangular(R[:p, :p].T, d)
      A = np.dot(A, Q)
      if lam == 0: #unregularized
          z = linalg.lstsq(A[:, p:], b - np.dot(A[:, :p], y))[0].ravel()
      else: #regularized
          z = linalg.lstsq(A[:, p:].T.dot(A[:, p:]) + lam*np.eye(A[:,p:].shape[1]), A[:, p:].T.dot(b - np.dot(A[:, :p], y)))[0].ravel()
      return np.dot(Q[:, :p], y) + np.dot(Q[:, p:], z)
  
  
  x = lse(A, grid_val, B, d, lam)
  
  #write results to mtpl_file (passed as argument above)
  i = 0
  rmse = np.sqrt(np.mean((np.matmul(A,x)-grid_val)**2))
  f.write("Qtot" + str(np.sum(x[:7])) + '\n')
  f.write("# RMSE: " + str(rmse) + "Qa(l,m) [a = atom index]\n")
  f.write(str(lmax) + '\n')
  for l in range(lmax+1):
      for a in range(Natom):
          for m in range(-l,l+1):
              f.write('Q'+str(a+1)+'('+str(l)+','+str(m)+')' + '     ' + str(x[i]) + '\n')
              if l == 0:
                results['Atom'+str(a+1)+'_Q00'] = float(x[i])
              i += 1
  

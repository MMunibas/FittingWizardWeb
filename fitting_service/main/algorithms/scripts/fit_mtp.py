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
# Performs fitting of multipoles given one/several mtp_fittab file(s).
# Non-neutral residues handled by providing total charge of collection of
# molecules.
# Fits A^t A x = A^t b using scipy.linalg.solve.
# A^t A is used to provide square matrix
#
# Two different kinds of penalities are used:
# - First penalty "pref_cc" is used to make sure that the total charge equals 
#   the total charge given in the last line of the mtp_fittab file(s).
# - Second penalty "prefactor" is used to regularize the absolute values of
#   the point charges and the multipole coefficients
#
# The input data is stored and handled in sparse matrix format. This is 
# especially efficient for many mtp_fittab file(s) for different molceules.
#
# Initial version of this script
# Tristan Bereau (10.05.2012)
#
# Rewrote many of the functions in numpy to speedup the fitting.
# Christian Kramer (27.05.2013)
#
# Fixed some bugs and changed the solver to scipy.linalg.solve
# Christian Kramer (04.06.2013)
#
# Cleaned up the code, added Comments
# Christian Kramer (05.06.2013)

def print_exception_info(exception):
    traceback = exception.__traceback__
    print_traceback(traceback)

def print_traceback(traceback):
    print("file name:", traceback.tb_frame.f_code.co_filename)
    print("line number: ", traceback.tb_frame.f_lineno)
    if traceback.tb_next:
        print("---------------------------------------")
        print_traceback(traceback.tb_next)


#######################
# Initialize global variables, lists and dictionaries
npenalties = 0
tabf = []
ncoeff_entries = {}
coeffs_names_all = []
coeffs_names_mol = []
x_i = ""
rmse = None


def fit_mtp(rank, charges, outfile, penalty, off_hyd, tabfile): 
  try:

    import sys
    import scipy.sparse as sps
    import scipy.linalg as spl
    import numpy as np
    import json
    
    #######################
    # Set variable defaults
    
    verb = False    
    eps  = 1e-6                 # Threshold below which ESP contributions are set to zero (f(distance, angle)) 
#    rank = 2                # Maximum rank
    out = outfile                # Output file
    outpre = ""                # File to write coefficients for each step
#    off_hyd = False                # Turn off hydrogen multipoles (only keep their point charges)
    penalty_swt = True        # Turn off penalty scan (fix to default value)
    perconf = False                # Output error of individual molecules/conformations
    
    #######################
    # Penalty defaults
    prefactor = 1e-9                # Penalty for correlated coefficients
    pref_ini = -1.0                        # Penalty set on the command line interface
    pref_cc_ini = 1e2                # Initial penalty for charge control
    offshoot_threshold = penalty        # Finish increasing restraint #2 if all monopoles are within this threshold to the PC-only fit

    #######################
    # Initialize global variables, lists and dictionaries
    global npenalties, tabf, ncoeff_entries, coeffs_names_all, coeffs_names_mol, x_i
    
    tabf.append(tabfile)
    
    #######################
    # Functions
    
    def usage():
        """Print usage information"""
        print ("Usage: fit.mtp.py [-v] [-rank N]", \
              "[-o coeffs.dat] [-op file.dat] [-hyd]", \
              "[-np] [-pen lambda] [-l file.txt] [-perconf]", \
              "[-MonoConv #] fittab1.txt [fittab2.txt [...]]")
        print ("     -chg: total charge")
        print ("       -v: verbose")
        print ("    -rank: maximum rank (default: 2)")
        print ("       -o: output coefficients")
        print ("      -op: output prefactor vs. bond increment coefficients")
        print ("     -hyd: ignore all hydrogen multipoles", \
            "(only keep their point charges)")
        print ("     -pen: lambda value for monopole convergence")
        print ("      -np: no monopole-convergence penalty")
        print ("       -l: load individual PCs")
        print (" -perconf: output RMS error per conformation")
        print ("-MonoConv: Threshold for convergence towards PConly ESP fit (default: 0.1)")
        print ()
        raise Exception('Incorrect arguments for fit.mtp.py')
    
    
    def read_coeffs(list_files):
        """Read formatted input coefficients"""
        global row, col, dat, esp, esp_chg, row_chg, col_chg, dat_chg
        global rows_per_conf, ncoeff_entries
        # Read coefficients; bookkeep row, column, and associated data points
        # of each element parsed.
        row = np.zeros(0,dtype=np.int64)
        col = np.zeros(0,dtype=np.int64)
        dat = np.zeros(0,dtype=np.float)
        rows_per_conf = []
        # Same for chrg penalties
        row_chg = np.zeros(0,dtype=np.int64)
        col_chg = np.zeros(0,dtype=np.int64)
        dat_chg = np.zeros(0,dtype=np.float)
        # ESP values
        esp = np.zeros(0,dtype=np.float)
        esp_chg = np.zeros(0,dtype=np.float)
        # Row index; will be incremented for each gridpoint
        global row_i, chrg_i
        row_i  = 0
        chrg_i = 0
        print ("Parsing files:")
        for i,infile in enumerate(list_files):
            print (" ",infile)
            try:
                f = open(infile,'r')
            except:
                print ("Error: Could not find",infile,". Exiting.")
                raise Exception('File not found: '+infile)
            readf = [line.split() for line in f.readlines()]
            f.close()
            col_idx = []
            for j in range(1,len(readf[0])-1):
                if readf[0][j] in coeffs_names:
                    col_idx.append((j,coeffs_names.index(readf[0][j])))
            row_i_i = np.zeros(len(col_idx)*len(readf),dtype=np.int64)
            col_i_i = np.zeros(len(col_idx)*len(readf),dtype=np.int64)
            dat_i_i = np.zeros(len(col_idx)*len(readf),dtype=np.float)
            esp_i_i = np.zeros(len(readf),dtype=np.float)
            row_chg_i_i = np.zeros(len(col_idx)*len(readf),dtype=np.int64)
            col_chg_i_i = np.zeros(len(col_idx)*len(readf),dtype=np.int64)
            dat_chg_i_i = np.zeros(len(col_idx)*len(readf),dtype=np.float)
            esp_chg_i_i = np.zeros(len(readf),dtype=np.float)
            cnt = 0
            chg_cnt = 0
            row_i_count = 0
            row_chg_i_count = 0
            readf = readf[1:]
            for j,line in enumerate(readf):
              if line[0][0:2] == "Pt":
                  for k in col_idx:
                      val = float(line[k[0]])
                      if abs(val) > eps:
                          row_i_i[cnt] = int(row_i)
                          col_i_i[cnt] = k[1]
                          dat_i_i[cnt] = val
                          cnt += 1
                  esp_i_i[row_i_count] = float(line[-1])
                  row_i += 1
                  row_i_count += 1
              if line[0][0:2] == "Ch":
                  for k in col_idx:
                      val = float(line[k[0]])
                      if abs(val) > eps:
                          row_chg_i_i[chg_cnt] = int(chrg_i)
                          col_chg_i_i[chg_cnt] = k[1]
                          dat_chg_i_i[chg_cnt] = val
                          chg_cnt += 1
                  esp_chg_i_i[row_chg_i_count] = float(line[-1])
                  chrg_i += 1
                  row_chg_i_count += 1
            row = np.append(row,row_i_i[:cnt])
            col = np.append(col,col_i_i[:cnt])
            dat = np.append(dat,dat_i_i[:cnt])
            esp = np.append(esp,esp_i_i[:row_i_count])
            row_chg = np.append(row_chg,row_chg_i_i[:chg_cnt])
            col_chg = np.append(col_chg,col_chg_i_i[:chg_cnt])
            dat_chg = np.append(dat_chg,dat_chg_i_i[:chg_cnt])
            esp_chg = np.append(esp_chg,esp_chg_i_i[:row_chg_i_count])
    
            # Update rows_per_conf if we're dealing with all files
            if len(list_files) == len(tabf):
                # Special case: only one molecule. Don't update rows_per_conf if it already exists.
                if len(list_files) != 1 or len(rows_per_conf) != 1:
                    rows_per_conf.append(row_i_count)
        
        readf = ''
        if penalty_swt == True:                # Count the frequency of column entries for each column
            print ("Penalized coefficients: all. Penalty on PCs is 10 times stronger than on Dipoles and Quadrupoles.")
            ncoeff_entries = dict(zip(coeffs_names,np.bincount(col)))
    
    
    def calc_residuals(Amat,Bvec,coeffs,perconf=False):
        """Calculate residuals and other performance metrics"""
        residuals = []
        matmul = Bvec - Amat*coeffs
        residuals = matmul*627.51
        r2 = 1-sum(residuals**2)/sum((Bvec*627.51)**2)
        rel_MAE = sum(abs(residuals))/sum(abs(Bvec*627.51))
        if perconf:
            print ("  RMSE and Mean Relative Error per conformation:")
            last_j = 0
            for i in range(len(rows_per_conf)):
                res_i = residuals[last_j:last_j+rows_per_conf[i]]
                esp_i = Bvec[last_j:last_j+rows_per_conf[i]]*627.51
                rmse_i = np.sqrt(sum(res_i**2)/len(res_i))
                r2_i = 1-sum(res_i**2)/sum(esp_i**2)
                rel_MAE_i = sum(abs(res_i))/sum(abs(esp_i))
                print ("       %30s: %7.4f kcal/mol RMSE; %7.4f Relative MAE; %7.5f R^2 ESP" % (tabf[i], rmse_i, rel_MAE_i, r2_i))
                last_j += rows_per_conf[i]
             
        return [r2,
                sum(abs(residuals))/len(residuals),
                np.sqrt(sum(residuals**2)/len(residuals)),
                rel_MAE]
    
    
    def add_penalties(chg_control=True):
        """Add penalties to keep parameters in range """
        global npenalties, row, col, dat, esp
        addtl_rows = 0
    
        # Add penalty for net charge
        if chg_control == True:
            for i in range(len(row_chg)):
                row = np.append(row,int(row_i + row_chg[i]))
                col = np.append(col,int(col_chg[i]))
                dat = np.append(dat,pref_cc * dat_chg[i])
            for i in range(chrg_i):
                esp = np.append(esp,pref_cc * esp_chg[i])
            addtl_rows = chrg_i
    
        # Add penalty to all coefficients
        for i in ncoeff_entries.keys():
            if ncoeff_entries[i] > 0:
                row = np.append(row,int(row_i+addtl_rows+npenalties))
                col = np.append(col,int(coeffs_names.index(i)))
                underscore = i.index("_")
                if float(i[underscore+2:underscore+3]) == 0:                # Default: Charges get 10 times larger penalty than dipoles and quadrupoles
                    dat = np.append(dat,10 * prefactor)
                    esp = np.append(esp,10 * prefactor * x_comb_init[int(coeffs_names.index(i))])
                else:
                    dat = np.append(dat,prefactor)        
                    esp = np.append(esp,0.0)
                npenalties += 1
    
    
    def del_penalties(chg_control=True):
        """Remove penalities from previous round """
        global row, col, dat,esp, npenalties
    
        # Remove existing penalties
        if chg_control == True and len(row_chg) > 0:
            row = row[:-len(row_chg)]
            col = col[:-len(col_chg)]
            dat = dat[:-len(dat_chg)]
            esp = esp[:-chrg_i]
        if npenalties > 0:
            row = row[:-npenalties]
            col = col[:-npenalties]
            dat = dat[:-npenalties]
            esp = esp[:-npenalties]
            npenalties = 0
    
    
    def measure_chg_control():
        """Calculate deviation from target total charge"""
        # Penalties are encoded in row_chg[], col_chg[], dat_chg[], esp_chg[]. 
        # This routine expects one row (indexed in row_chg) per molecule.
    
        cc = sps.coo_matrix((dat_chg,(row_chg,col_chg)),shape=(max(row_chg)+1,len(x_i))).todense()
        tot_chg = np.dot(np.array(cc),x_i)
        chg_dev = tot_chg - esp_chg
        return max(abs(chg_dev))
    
    
    def iterative(A, b, solver=spl.solve):
        """Solve linear equation system"""
        AtA = (A.transpose()*A).todense()
        Atb = A.transpose()*b
        return solver(AtA, Atb)
    
    
    def optimization(penalize):
        """Run optimization of charge penalties and find final parameters """
        global x_i, prefactor, pref_cc, row, col, dat, esp, rmse
        pref_cc = float(pref_cc_ini)
        print ("*****************************************************")
    
        if penalize and pref_ini >= 0.0:
            prefactor = pref_ini
        elif penalize:
            prefactor = 6.4e-3        # Initial restraint if not set by user
        else:
            prefactor = 1e-6
    
        if outpre != '':
            fp = open(outpre,'w')
            fp.write("#penalty   %7s %11s" % ("error", "max_chg_dev"))
            for i in range(len(coeffs_names)):
                fp.write("%25s " % (coeffs_names[i]))
            fp.write("\n")
            fp.close()
    
    # Build reference matrix to calculate residuals.
        A_ref = sps.coo_matrix((dat, (row,col)),shape=(row_i,len(coeffs_names))).tocsr()
        b_ref = np.array(esp)
    
    # 1st: find appropriate pref_cc to control total charge
        print ("Solving linear equation system with penalty factor %7.1e" % prefactor)
        add_penalties(False)
        A = sps.coo_matrix((dat,(row,col)),shape=(row_i+npenalties,len(coeffs_names))).tocsr()
        print (row_i,npenalties,coeffs_names)
        b = np.array(esp)
        x_i = iterative(A, b)
        del_penalties(False)
        max_chg_dev = measure_chg_control()
        residuals = calc_residuals(A_ref,b_ref,x_i)
        
    # Next rounds with gradual increase of charge control until max charge deviation is less than 1e-12
        while max_chg_dev > 1e-10 and pref_cc < 1e+12:                        
            pref_cc *= 10
            if verb: print ("    charge-control penalty: %7.1e" % pref_cc)
    
            add_penalties()
            A = sps.coo_matrix((dat,(row,col)),shape=(row_i+chrg_i+npenalties,len(coeffs_names))).tocsr()
            b = np.array(esp)
            x_i = iterative(A, b)
            del_penalties()
            new_max_chg_dev = measure_chg_control()
            residuals = calc_residuals(A_ref,b_ref,x_i)
            
            if new_max_chg_dev > max_chg_dev:
                 pref_cc /= 10
                 break
            max_chg_dev = new_max_chg_dev
            if verb: 
                print ("      Max charge deviation:    %7.4e" % max_chg_dev)
                print ("      RMSE:                  %7.4f kcal/mol" %residuals[2])
                print ("      Mean absolute error:   %7.4f kcal/mol" %residuals[1])
                print ("      Relative MAE:          %7.4f " %residuals[3])
                print ("      R^2 predicted ESP:     %7.5f " %residuals[0])
                print ()
    
    # 2nd: increase penalty until PCs are within desired range or coefficients don't vary too much any more.
        while prefactor < 1e2:
            # Output results from previous run
            for i in range(len(coeffs_names)):
                if coeffs_names[i][0:2] == "BI":
                    print ("%5d %30s %18.15f" % (i+2,coeffs_names[i],x_i[i]))
    
            if outpre != '':
                fp = open(outpre,'a')
                fp.write("%7.4e %7.4f %11.4e" % (prefactor, residuals[2], max_chg_dev))
                for i in range(len(coeffs_names)):
                    fp.write("%25.20f " % (x_i[i]))
                fp.write("\n")
                fp.close()
    
    # calculate largest charge coefficients and residuals
            largest_Q0 = max(abs(value) for i,value in enumerate(x_i) if coeffs_names[i][-4:] == "_Q00")
            if num_bi: largest_bi = max(abs(value) for i,value in enumerate(x_i) if coeffs_names[i][0:2] == "BI")
            residuals = calc_residuals(A_ref,b_ref,x_i,perconf=perconf)
    
    # Write summary
            print ()
            print ("  Restraint:                           %7.4e" % prefactor)
            print ("  RMSE:                                %7.4f kcal/mol" %residuals[2])
            print ("  Mean absolute error:                 %7.4f kcal/mol" %residuals[1])
            print ("  Relative MAE:                        %7.4f " %residuals[3])
            print ("  R^2 predicted ESP:                    %7.5f " %residuals[0])
            if num_bi: print ("  Strength of largest bond increment:   %7.4e" % largest_bi)
            print ("  Strength of largest monopole:         %7.4e" % largest_Q0)
            print ("  Max charge deviation:                 %7.4e" % max_chg_dev)
    
            if penalty_swt and penalize and pref_ini < 0.0:

    # Check whether monopoles are close enough to ESP-only monopoles
                max_offshoot = max([abs(x_i[i] - x_comb_init[i]) for i,name in enumerate(coeffs_names) if "Q00" in name])
                
    # Stop scan if offshoot is less than threshold set above
                print ("  Monopole convergence:                 %7.4e" % max_offshoot)
                print ("---------------------------")
                if max_offshoot < offshoot_threshold:
                    print ("  Monopole convergence has been reached.")
                    break
                if prefactor == 0:
                    prefactor = 1e-3
                else:
                    prefactor *= 2
            else:
                 break
    
    # Calculate new coefficients with adapted threshold
            add_penalties()
            A = sps.coo_matrix((dat,(row,col)),shape=(row_i+chrg_i+npenalties,len(coeffs_names))).tocsr()
            b = np.array(esp)
            x_i = iterative(A, b)
            max_chg_dev = measure_chg_control()
            del_penalties()
    
    
        print ("*****************************************************")
        print ("Results:")
        print ("  RMSE:                      %7.4f kcal/mol" %residuals[2])
        print ("  Mean absolute error:       %7.4f kcal/mol" %residuals[1])
        print ("  Relative MAE:              %7.4f " %residuals[3])
        print ("  R^2 predicted ESP:          %7.5f " %residuals[0])
        print ("  Max charge deviation:      %7.4e e" % max_chg_dev)
        calc_residuals(A_ref,b_ref,x_i,perconf=perconf)
        rmse = residuals[2]
    
    def printMTPNorms(coeffs_names,x_i):
        # Group MTP coefficients by atom types and print each dipole and
        # quadrupole norm.
        print ("  Norms of MTP coefficients:")
        atomType   = ''
        typeCoeffs = []
        typeDip    = 0.00
        typeQuad   = 0.00
        for nameID in range(len(coeffs_names)):
            name = coeffs_names[nameID]
            currentName = name[0:name.find('_Q')]
            if atomType != currentName:
                if atomType != '':
                    for atypeID in typeCoeffs:
                        if coeffs_names[atypeID].find('_Q1') != -1:
                            typeDip += x_i[atypeID]**2
                        elif coeffs_names[atypeID].find('_Q2') != -1:
                            typeQuad += x_i[atypeID]**2
                    print ("%15s: |Q1| = %7.4f  |Q2| = %7.4f" % \
                        (atomType,np.sqrt(typeDip),np.sqrt(typeQuad)))
                atomType   = currentName
                typeCoeffs = []
                typeDip    = 0.00
                typeQuad   = 0.00
            typeCoeffs.append(nameID)
        for atypeID in typeCoeffs:
            if coeffs_names[atypeID].find('_Q1') != -1:
                typeDip += x_i[atypeID]**2
            elif coeffs_names[atypeID].find('_Q2') != -1:
                typeQuad += x_i[atypeID]**2
        print ("%15s: |Q1| = %7.4f  |Q2| = %7.4f" % \
            (atomType,np.sqrt(typeDip),np.sqrt(typeQuad)))
        return
    
    
    def opt_esp(list_files,penalize,rnk):
        """Run optimization within"""
        global x_i, coeffs_names, x_comb_init
    
    # Get coefficient names that should be fit
        coeffs_names = []
        for name in coeffs_names_all:
            idx_ = name.index("_")
            if name[0:2] == "BI" or int(name[idx_+2:idx_+3]) <= rnk:
                if not (off_hyd == True and name[0] == 'H' and int(name[idx_+2:idx_+3]) > 0):
                    coeffs_names.append(name) 
    
        print (coeffs_names_all)
        print (coeffs_names)
    
        read_coeffs(list_files)
        try:
            x_comb_init
        except:
            x_comb_init = np.zeros(len(coeffs_names))
        
        # Initial guess
        x_i = np.zeros(len(coeffs_names))
        rmse = optimization(penalize)
        if penalize:
            # Only run the routine for the fit with penalties.
            printMTPNorms(coeffs_names,x_i)
        # Print results
        print ("Final coefficients ("+str(len(coeffs_names))+"):")
        for i in range(len(coeffs_names)):
            print ("%25s %18.15f" % (coeffs_names[i],x_i[i]))
    
    
    #####################################
    # Main Program
    #####################################
    
    ###########
    # Read input
    
#    for i in range(1,len(sys.argv)):
#        if sys.argv[i] == '-v':
#            verb = True
#        elif sys.argv[i] == '-rank':
#            rank = int(sys.argv[i+1])
#        elif sys.argv[i] == '-o':
#            out = sys.argv[i+1]
#        elif sys.argv[i] == '-op':
#            outpre = sys.argv[i+1]
#        elif sys.argv[i] == '-hyd':
#            off_hyd = True
#        elif sys.argv[i] == '-pen':
#            pref_ini = float(sys.argv[i+1])
#        elif sys.argv[i] == '-np':
#            penalty_swt = False
#        elif sys.argv[i] == '-l':
#            load_pc = sys.argv[i+1]
#        elif sys.argv[i] == '-perconf':
#            perconf = True
#        elif sys.argv[i] == '-MonoConv':
#            offshoot_threshold = float(sys.argv[i+1])
#        elif sys.argv[i-1] not in ['-rank', '-o', '-pen', \
#                                   '-op', '-l', '-MonoConv']:
#            tabf.append(sys.argv[i])
    
    if rank == 0: offshoot_threshold = 0.1
    if tabf == []: usage()
    
    assert rank >= 0 and rank <3
    
    ###########
    # Read header of each file and analyze descriptors. 
    # Skip Columns with a too high rank and hydrogen multipoles (if desired)
    
    for infile in tabf:
        coeffs_names_mol_tmp = []
        try:
            f = open(infile,'r')
        except:
            print ("Error: Could not find",infile,". Exiting.")
            raise Exception('File not found: '+infile)
        readf = f.readline().split()
        f.close()
        if readf[0] != 'PtID' or readf[-1] != 'aiESP':
            print ("Error: Format error in header of",tabf[i])
            raise Exception('Format error in header of '+tabf[i])
        for j in range(1,len(readf)-1):
            underscore = readf[j].index("_")
            if readf[j][0:2] == "BI" or int(readf[j][underscore+2:underscore+3]) <= rank:
                if not (off_hyd == True and readf[j][0] == 'H' and int(readf[j][underscore+2:underscore+3]) > 0):
                    coeffs_names_all.append(readf[j])
                    coeffs_names_mol_tmp.append(readf[j])
        coeffs_names_mol.append(sorted(set(coeffs_names_mol_tmp)))
    
    ###########
    # Sort coefficient names prior to parsing all MTP coefficients.
    coeffs_names_all = sorted(set(coeffs_names_all))
    
    num_bi = len([i for i in coeffs_names_all if i[0:2] == "BI"])
    if verb == True:
        print ("Number of coefficients:",len(coeffs_names_all),"(includes",num_bi,"bond increments).")
    
    ############
    # Optimize monopoles of each molecule, unless we can load a file with the PCs
    x_comb_init = np.zeros(len(coeffs_names_all))
    print ("Parsing PCs")
    for key, value in charges.items():
        x_comb_init[coeffs_names_all.index(key)] = value
    if len(x_comb_init) != len(coeffs_names_all):
        print ("Warning: length of",load_pc,"and number of coefficients not compatible.")
 
    ############
    # Output initial PCs
    print ("\n*****************************************************")
    print ("Initial coefficients for combined fit:")
    for j in range(len(coeffs_names_all)):
        print ("%25s %18.15f" % (coeffs_names_all[j], x_comb_init[j]))
    
    ############
    # Combined fit 
    print (" Fitting full model with all MTP parameters given.")
    print ()
    opt_esp(tabf,True,rank)
    
    #############
    # Output
    fitted_charges={}
    if out != '':
        print ("Writing coefficients to",out)
        f = open(out,'w')
        for i in range(len(coeffs_names)):
            f.write("%s %18.15f\n" % (coeffs_names[i],x_i[i]))
            fitted_charges[coeffs_names[i]]=x_i[i]
        f.close()
    print ("returning rmse "+str(rmse)+" and fitted multipoles")
    return rmse, fitted_charges

  except Exception as ex:
     print_exception_info(ex)
     raise ex

from .cluster.unibasel_slurm import generate_charmm_setup_script, number_of_cpu_cores, charmm_executable, ld_path, \
    env_path, mpi_executable, mpi_flags, scratch_dir_name, generate_charmm_twostep_setup_script
from .toolkit import *
from .charmm_input import *
from .scripts.run_scale_vdw import *
from shutil import move
import json
import sys
import math
import time


@register
# Routine to perform a LJ-Fit. Components are:
#	1. gas-phase calculation on single solute molecule to calculate part of vaporization enthalpy
#	2. simulation of pure-liquid box made up of solute molecule for vaporization enthalpy
#	3. Thermodynamic integration (TI) to calculate solvation free energy of solute, comprising:
#	   i) gas-phase TI for VDW (no electrostatics)
#	   ii) gas-pahse TI for electrostatic term (VDW turned on)
#	   iii) solution-phase TI for VDW (no solute electrostatics, solute in water box)
#	   iv) solution-phase TI for electrostatics (solute VDW turned on)
#
# Note that TI lambda windows are automatically subdivided if the variance for that window is too large
#
# References: DOI 10.1021/acs.jcim.6b00280

def ljfit(ctx):
    tot_charmm_dyna_steps=150000 #150000 # number of MD steps in total (including equilibration)
    charmm_equil_steps=50000 #50000 # number of equilibration steps
    MAX_SUB=3 # max allowed number of subdivisions for TI calculations

    lmb0 = 0.0
    lmb1 = 1.0

    results = {}  # main results list
    dg_gas=0.0
    dg_liq=0.0
    dg_solv=0.0 # variables to accumulate total solvation free energy components from TI simulations
    dg_solv_vdw_gas=0.0
    dg_solv_vdw_solv=0.0
    dg_solv_elec_gas=0.0
    dg_solv_elec_solv=0.0

    # parse parameters
    ctx.log.info("parameters passed:")

    for parameter, value in ctx.parameters.items():
        ctx.log.debug("input parameter {} is set to {}".format(parameter, value))

    try:
        par = ctx.parameters["lj_filename_charmm_parameter"]
        top = ctx.parameters["lj_filename_charmm_topology"]
        slu = ctx.parameters["lj_filename_solute_pdb"]
        slv = ctx.parameters["lj_filename_solvent_pdb"]
        slv_res = ctx.parameters["lj_filename_charmm_restart"]
        lpun = ctx.parameters["lj_filename_mtpl_lpun"]
        pureliq = ctx.parameters["lj_filename_pureliquid_pdb"]
        dlmbElec = float(ctx.parameters["lj_ti_lambda_window_size_electrostatic"])
        dlmbVDW = float(ctx.parameters["lj_ti_lambda_window_size_vdw"])
        T = float(ctx.parameters["lj_charmm_simulation_temperature"])
        epsfac = float(ctx.parameters["lj_scaling_factor_eps"])
        sigfac = float(ctx.parameters["lj_scaling_factor_sig"])
        refDG = float(ctx.parameters["reference_solvation_energy"])
        refDens = float(ctx.parameters["reference_liquid_density"])
        refDH = float(ctx.parameters["reference_vaporization_enthalpy"])
    except ValueError:
        pass

    ctx.log.info("Input files:\n\t{}".format("\n\t".join(ctx.input_dir.list_files_recursively())))
    ctx.set_running_status('Setting up run')

    # scale LJ parameters to trial values for this fitting step
    inpdir = ctx.input_dir.full_path + "/"
    scaled_par=run_scale_vdw(inpdir + slu, inpdir + top, inpdir + par, sigfac, epsfac, inpdir)

    # set up and submit gas-phase calculation for vaporization enthalpy in "dens" folder
    ctx.log.info("writing input for gas phase calculation:")
    gas_inp_dir = ctx.input_dir.subdir("dens").full_path
    gas_inp_name = gas_inp_dir + "/gas.inp"
    gas_out_dir = ctx.run_out_dir.subdir("dens").full_path
    gas_out_name = gas_out_dir + "/gas.out"
    gas_inp_file = write_gas_inp(ctx, gas_inp_name, top, scaled_par, slu, lpun).name

    ctx.log.info("submitting gas-phase calculation:")
    ctx.set_running_status('submitting gas-phase calculation')
    create_charmm_submission_script(ctx, "run-gas.sh", gas_inp_file, gas_out_name, "dens")
####    gas_out_name="/home/wfit/FittingWizardWeb/fitting_service/data/2018-05-31_17-37-57-878275_luhq4/2018-05-31_17-37-57-885123_uNYv3/output/dens/gas.out" #######
    job_id = ctx.schedule_job(ctx.input_dir.full_path + "/dens/run-gas.sh")

    # set up pure liquid calculation for vaporization enthalpy and density in "dens" folder
    ctx.log.info("writing input for pure liquid calculation:")
    dens_inp_dir = ctx.input_dir.subdir("dens").full_path
    dens_inp_name = dens_inp_dir + "/density.inp"
    dens_out_dir = ctx.run_out_dir.subdir("dens").full_path
    dens_out_name = dens_out_dir + "/density.out"
    dens_inp_file = write_dens_inp(ctx, dens_inp_name, top, scaled_par, pureliq, lpun, T).name

    ctx.log.info("submitting density calculation:")
    create_charmm_submission_script(ctx, "run.sh", dens_inp_file, dens_out_name, "dens")

    # Now set up thermodynamic integration run for solute in water box
    ti_vdw_gas_inp_dir=ctx.input_dir.subdir("ti/vdw/gas").full_path + "/"
    ti_vdw_solv_inp_dir=ctx.input_dir.subdir("ti/vdw/solv").full_path + "/"
    ti_elec_gas_inp_dir=ctx.input_dir.subdir("ti/elec/gas").full_path + "/"
    ti_elec_solv_inp_dir=ctx.input_dir.subdir("ti/elec/solv").full_path + "/"


    tiElecGasJobScripts, tiElecSolvJobScripts, tiOutElecGasFileTrajread, tiOutElecSolvFileTrajread = \
                         write_ti_elec_inp(ctx,ti_elec_gas_inp_dir,ti_elec_solv_inp_dir,lmb0,lmb1,
                         dlmbElec,top,slu,slv,lpun,scaled_par,slv_res,T,tot_charmm_dyna_steps,charmm_equil_steps)

    tiVDWGasJobScripts, tiOutVDWGasFile = \
                         write_ti_vdw_gas_inp(ctx,ti_vdw_gas_inp_dir,lmb0,lmb1,dlmbVDW,
                         top,slu,slv,lpun,scaled_par,slv_res,T,tot_charmm_dyna_steps,charmm_equil_steps)

    tiVDWSolvJobScripts, tiOutVDWSolvFile = \
                         write_ti_vdw_solv_inp(ctx,ti_vdw_solv_inp_dir,lmb0,lmb1,dlmbVDW,
                         top,slu,slv,lpun,scaled_par,slv_res,T,tot_charmm_dyna_steps,charmm_equil_steps)

    write_ti_loop_str(ctx,tot_charmm_dyna_steps,charmm_equil_steps)

####    dens_out_name="/home/wfit/FittingWizardWeb/fitting_service/data/2018-05-31_17-37-57-878275_luhq4/2018-05-31_17-37-57-885123_uNYv3/output/dens/density.out" #######
    ctx.set_running_status('submitting density calculation')
    dens_job_id = ctx.schedule_job(ctx.input_dir.full_path + "/dens/run.sh")
    ctx.terminate_if_canceled()

    ctx.set_running_status('submitting TI jobs')

    ti_elec_gas_job_id = []
    for i in range(0,len(tiElecGasJobScripts)):
       ti_elec_gas_job_id.append(ctx.schedule_job(tiElecGasJobScripts[i]))
       ctx.terminate_if_canceled()

    ti_elec_solv_job_id = []
    for i in range(0,len(tiElecSolvJobScripts)):
       ti_elec_solv_job_id.append(ctx.schedule_job(tiElecSolvJobScripts[i]))
       ctx.terminate_if_canceled()

    ti_vdw_gas_job_id = []
    for i in range(0,len(tiVDWGasJobScripts)):
       ti_vdw_gas_job_id.append(ctx.schedule_job(tiVDWGasJobScripts[i]))
       ctx.terminate_if_canceled()

    ti_vdw_solv_job_id = []
    for i in range(0,len(tiVDWSolvJobScripts)):
       ti_vdw_solv_job_id.append(ctx.schedule_job(tiVDWSolvJobScripts[i]))
       ctx.terminate_if_canceled()

    time.sleep(15)
    ctx.wait_for_all_jobs()


    # check whether density and delta H_vap jobs finished cleanly:
    check_charmm_status(ctx,gas_out_name)
    check_charmm_status(ctx,dens_out_name)
    # check whether TI jobs finished cleanly:
    for i in range(0,len(tiOutElecGasFileTrajread)):
       check_charmm_status(ctx,tiOutElecGasFileTrajread[i])
    for i in range(0,len(tiOutElecSolvFileTrajread)):
       check_charmm_status(ctx,tiOutElecSolvFileTrajread[i])
    for i in range(0,len(tiOutVDWGasFile)):
       check_charmm_status(ctx,tiOutVDWGasFile[i])
    for i in range(0,len(tiOutVDWSolvFile)):
       check_charmm_status(ctx,tiOutVDWSolvFile[i])

    # gather results for vaporization enthalpy
    parse_dens_out(ctx, slu, top, results, T, gas_out_name, dens_out_name)

    ctx.log.info("Density read from file " + dens_out_name + ": " + str(results["pure_liquid_density"]) + " g/cm^3\n")
    ctx.log.info("Vaporization Enthalpy: " + str(results["vaporization_enthalpy"]) + " kcal/mol\n")

    # gather TI results and subdivide windows as necessary:
    for i in range(0,len(tiOutElecGasFileTrajread)):
       dg_lambda = parse_ti_out_2step(ctx,tiOutElecGasFileTrajread[i]) * dlmbElec
       dg_solv -= dg_lambda
       dg_solv_elec_gas += dg_lambda
    for i in range(0,len(tiOutElecSolvFileTrajread)):
       dg_lambda = parse_ti_out_2step(ctx,tiOutElecSolvFileTrajread[i]) * dlmbElec
       dg_solv += dg_lambda
       dg_solv_elec_solv += dg_lambda

    all_converged=False
    iterations=0
    while not all_converged:
       all_converged=True
       if iterations > MAX_SUB:
          ctx.log.info("Subdivided too many times ("+str(iterations)+"). Please check output files and reduce lambda_vdw TI window size if necessary. Exiting.")
          raise Exception('Subdivided too many times. Limit is '+MAX_SUB)
       dlmbVDW /= 2.0
       tiVDWGasSubdivScripts = []
       tiOutVDWGasSubdivFile = []
       tiVDWSolvSubdivScripts = []
       tiOutVDWSolvSubdivFile = []
       tmpScripts = []
       tmpFiles = []
       for i in range(0,len(tiOutVDWGasFile)):
          converged, dg_lambda = parse_ti_out(ctx,tiOutVDWGasFile[i])
          if converged:
             dg_solv += dg_lambda
             dg_solv_vdw_gas += dg_lambda
          else: # subdivide window
             all_converged=False
             ctx.set_running_status('subdividing TI windows, not yet converged')
             srcdir=os.path.dirname(tiOutVDWGasFile[i])
             filename=os.path.basename(tiOutVDWGasFile[i])
             words=filename.split('_') 
             lstart=float(words[0])
             lstop=float(words[1])
             
             move(srcdir, str(ctx.run_out_dir.subdir(srcdir+"/../not_converged/").full_path))
             tmpScripts, tmpFiles = \
                            write_ti_vdw_gas_inp(ctx,ti_vdw_gas_inp_dir,lstart,lstop,dlmbVDW,
                            top,slu,slv,lpun,scaled_par,slv_res,T,tot_charmm_dyna_steps,charmm_equil_steps)
             tiVDWGasSubdivScripts.extend(tmpScripts)
             tiOutVDWGasSubdivFile.extend(tmpFiles)
       for i in range(0,len(tiOutVDWSolvFile)):
          converged, dg_lambda = parse_ti_out(ctx,tiOutVDWSolvFile[i])
          if converged:
             dg_solv -= dg_lambda
             dg_solv_vdw_solv += dg_lambda
          else: # subdivide window
             all_converged=False
             ctx.set_running_status('subdividing TI windows, not yet converged')
             srcdir=os.path.dirname(tiOutVDWSolvFile[i])
             filename=os.path.basename(tiOutVDWSolvFile[i])
             words=filename.split('_')  
             lstart=float(words[0])
             lstop=float(words[1])
             move(srcdir, str(ctx.run_out_dir.subdir(srcdir+"/../not_converged/").full_path))          
             tmpScripts, tmpFiles = \
                            write_ti_vdw_solv_inp(ctx,ti_vdw_solv_inp_dir,lstart,lstop,dlmbVDW,
                            top,slu,slv,lpun,scaled_par,slv_res,T,tot_charmm_dyna_steps,charmm_equil_steps)
             tiVDWSolvSubdivScripts.extend(tmpScripts)
             tiOutVDWSolvSubdivFile.extend(tmpFiles)
   
       tiOutVDWGasFile=tiOutVDWGasSubdivFile
       tiOutVDWSolvFile=tiOutVDWSolvSubdivFile
       ti_vdw_gas_job_id=[]
       for i in range(0,len(tiVDWGasSubdivScripts)):
           ti_vdw_gas_job_id.append(ctx.schedule_job(tiVDWGasSubdivScripts[i]))
           ctx.terminate_if_canceled()
       for i in range(0,len(tiVDWSolvSubdivScripts)):
           ti_vdw_solv_job_id.append(ctx.schedule_job(tiVDWSolvSubdivScripts[i]))
           ctx.terminate_if_canceled()
  
       time.sleep(5) 
       ctx.wait_for_all_jobs()
       iterations += 1

    results["dg_solv_vdw_gas"] = dg_solv_vdw_gas
    results["dg_solv_vdw_solv"] = dg_solv_vdw_solv
    results["dg_solv_elec_gas"] = dg_solv_elec_gas
    results["dg_solv_elec_solv"] = dg_solv_elec_solv
    results["dg_tot_gas_phase"] = dg_solv_vdw_gas + dg_solv_elec_gas
    results["dg_tot_solution_phase"] = dg_solv_vdw_solv + dg_solv_elec_solv
    results["dg_total"] = dg_solv
    results["score_dg"]=(refDG-dg_solv)**2
    results["score_density"]=(refDens-results["pure_liquid_density"])**2
    results["score_dh"]=(refDH-results["vaporization_enthalpy"])**2
    results["score_total"]=5.0*results["score_dg"] + 3.0*results["score_dh"] + \
		results["score_density"]

    ctx.write_results(results)

#################################################################################################

def copytree(src, dst, symlinks=False, ignore=None):
    import shutil
    for item in os.listdir(src):
       s = os.path.join(src, item)
       d = os.path.join(dst, item)
       if os.path.isdir(s):
           shutil.copytree(s, d, symlinks, ignore)
       else:
           shutil.copy2(s, d)

#################################################################################################

def _get_and_log_job_status(ctx, job_id, expected_status):
    job_status = ctx.job_status(job_id)
    ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))


#################################################################################################

# Routine to parse vaporization enthalpy / density output
# Stages comprise:
#	1. obtain residue name of solute molecule from solute PDB file
#	2. calculate molecular mass for solute molecule using topology file
#	3. obtain gas-phase potential energy from CHARMM output
#	4. obtain solution-phase potential energy of solute and average box volume from CHARMM output
#	5. combine results to calculate liquid density and vaporization enthalpy

def parse_dens_out(ctx, slu, top, results, T, gas_out_name, dens_out_name):
    kB = 0.0019872041  # kcal/(mol.K)

    density = 0.0
    delta_H_vap = 0.0
    mmass = 0.0
    Nat = 0  # num atoms in solute
    ncons = 0  # num constrained DoF (SHAKE)

    status = 0

    res = ""
    resF = False
    masses = {}

    ctx.log.info("Calculating Molar Mass")
    # parse solute pdb file to get solute molecule type
    with ctx.input_dir.open_file(slu, "r") as pdb:
        for line in pdb:
            words = line.split()
            if len(words) > 3:
               if words[0].upper() == "ATOM":
                   if res and words[3] != res:
                       raise Exception(
                           "More than one residue type (" + res + " and " + words[3] + ") in solute pdb " + slu)
                   res = words[3]
        ctx.log.info("RES type is " + res)
    pdb.close()
    ctx.log.info("Reading Atomic Masses from Topology file " + top)

    # parse topology file to calculate molecular mass for solute molecule
    with ctx.input_dir.open_file(top, "r") as topo:
        for line in topo:
            words = line.split()
            if len(words) > 0:
                if words[0].upper() == "MASS":
                    masses[words[2]] = words[3]
                if words[0].upper() == "RESI" and words[1] == res:
                    resF = True
                elif words[0].upper() == "RESI":
                    resF = False
                if resF == True and words[0].upper() == "ATOM":
                    if words[2] in masses:
                        mmass += float(masses[words[2]])
                    else:
                        raise Exception("Atom type " + words[2] + " not found in list of atomic masses")
    if mmass == 0.0:
        raise Exception("Residue " + res + " not found in topology file " + top + " or has zero mass")
    ctx.log.info("Molecular mass for " + res + " is " + str(mmass))

    # parse CHARMM gas phase output file to extract simulation output
    with ctx.run_out_dir.open_file(gas_out_name, "r") as gas_output:
        for line in gas_output:
            words = line.split()
            if len(words) > 3:
                if words[0] == "NORMAL" and words[1] == "TERMINATION":
                    status = 1
                if words[0] == "MINI>":
                    gas_ener = float(words[2])
                if words[1] == "constraints" and words[2] == "will":
                    ncons = float(words[0])

    if status == 0:
        raise Exception("Job " + gas_out_name + " did not finish successfully")
    else:
        status = 0

    # parse CHARMM pure liquid output file to extract simulation output
    with ctx.run_out_dir.open_file(dens_out_name, "r") as liq_output:
        for line in liq_output:
            words = line.split()
            if len(words) > 1:
                if words[0].upper() == "RESIDUE" and words[1].upper() == "SEQUENCE":
                    nres = float(words[3])
                if words[0].upper() == "AVER" and words[1].upper() == "PRESS>":
                    avg_vol = float(words[6])  # ok that AVER PRESS> appears a few times as we want the last occurrence
                if words[0].upper() == "NORMAL" and words[1].upper() == "TERMINATION":
                    status = 1
                if words[0].upper() == "AVER>":
                    avg_liq_ener = float(words[5])  # energy without K.E. (we want the last occurrence)

    if status == 0:
        raise Exception("Job " + dens_out_name.name + " did not finish successfully")
    else:
        status = 0

        # calculate avg density of simulation box
    density = mmass * nres / (0.6022 * avg_vol)

    # calculate vaporization enthalpy
    delta_H_vap = gas_ener + 0.5 * T * kB * (3 * Nat - 6 - ncons) - avg_liq_ener / nres + T * kB

    # gather results
    results["molar_mass"] = mmass
    results["pure_liquid_density"] = density
    results["vaporization_enthalpy"] = delta_H_vap

#################################################################################################

def create_charmm_submission_script(ctx,
                                    filename,
                                    charmm_input_file_name,
                                    charmm_output_file_name,
                                    workdir_name,
                                    number_of_cores=None):
    number_of_cores = number_of_cores if number_of_cores is not None else number_of_cpu_cores
    with ctx.input_dir.subdir(workdir_name).open_file(filename, "w") as script_file:
        script_file.write(generate_charmm_setup_script(charmm_input_file_name,
                                                       charmm_output_file_name,
                                                       ctx.run_out_dir.subdir(workdir_name).full_path,
                                                       charmm_executable,
                                                       number_of_cores,
                                                       ctx._calculation_id,
                                                       ld_path,
                                                       env_path,
                                                       mpi_executable,
                                                       mpi_flags,
                                                       scratch_dir_name,
                                                       ctx.input_dir.full_path))

#################################################################################################

def create_charmm_twostep_submission_script(ctx,
                                    filename,
                                    charmm_input_file1_name,
                                    charmm_output_file1_name,
                                    charmm_input_file2_name,
                                    charmm_output_file2_name,
                                    workdir_name,
                                    number_of_cores=None):
    number_of_cores = number_of_cores if number_of_cores is not None else number_of_cpu_cores
    with ctx.input_dir.subdir(workdir_name).open_file(filename, "w") as script_file:
        script_file.write(generate_charmm_twostep_setup_script(charmm_input_file1_name,
                                                       charmm_output_file1_name,
                                                       charmm_input_file2_name,
                                                       charmm_output_file2_name,
                                                       ctx.run_out_dir.subdir(workdir_name).full_path,
                                                       charmm_executable,
                                                       number_of_cores,
                                                       ctx._calculation_id,
                                                       ld_path,
                                                       env_path,
                                                       mpi_executable,
                                                       mpi_flags,
                                                       scratch_dir_name,
                                                       ctx.input_dir.full_path))

#################################################################################################

def write_ti_elec_inp(ctx, ti_elec_gas_inp_dir, ti_elec_solv_inp_dir, lmb0, lmb1, dlmbElec, top, slu, slv, lpun, par, slv_res, T, tot_charmm_dyna_steps, charmm_equil_steps):

    if abs(int(round((lmb1-lmb0)/dlmbElec))-(lmb1-lmb0)/dlmbElec) > 0.000000001: # if lambda range not exactly divisible by delta_lambda
       ctx.log.info("The lambda range ("+str(lmb0)+" - "+str(lmb1)+") is not divisible by delta lambda for electrostatics "+str(dlmbElec)+" (remainder = "+str((lmb1-lmb0)%dlmbElec)+")\n")
       raise Exception('Invalid delta lambda for electrostatics (range is not divisible by lambda)')
    nlmbElec=int(round((lmb1-lmb0)/dlmbElec))
    lmbds=lmb0

    tiOutElecGasFileTrajread = []
    tiOutElecSolvFileTrajread = []
 
    tiElecGasJobScripts = []
    tiElecSolvJobScripts = []

    for i in range (0,nlmbElec):
        stub=str("%.6f"%lmbds)+"_"+str("%.6f"%(lmbds+dlmbElec))
        tiInpElecGasFile=stub+"_ti_elec_gas.inp"
        with ctx.input_dir.subdir(ti_elec_gas_inp_dir+stub).open_file(tiInpElecGasFile, "w") as ti_gas_inp:
            ti_gas_inp.write(write_ti_elec_gas_inp(top,par,slu,lpun,lmbds,lmbds+dlmbElec,charmm_equil_steps,tot_charmm_dyna_steps,T))

        tiInpElecGasTrajreadFile=stub+"_ti_elec_gas_trajread.inp"
        with ctx.input_dir.subdir(ti_elec_gas_inp_dir+stub).open_file(tiInpElecGasTrajreadFile, "w") as ti_gas_tr_inp:
            ti_gas_tr_inp.write(write_ti_elec_gas_trajread_inp(top,par,slu,lpun,lmbds,lmbds+dlmbElec,T))

        tiOutElecGasFile=stub+"_ti_elec_gas.log"
        tiOutElecGasFileTrajread.append(stub+"_ti_elec_gas_trajread.log")
        tiElecGasJobScripts.append(ctx.run_out_dir.subdir("ti/elec/gas/"+stub).full_path+"/run-ti-elec-gas.sh")
        create_charmm_twostep_submission_script(ctx, "run-ti-elec-gas.sh", ti_elec_gas_inp_dir+stub+"/"+
                                        tiInpElecGasFile, tiOutElecGasFile,
                                        ti_elec_gas_inp_dir+stub+"/"+tiInpElecGasTrajreadFile,
                                        tiOutElecGasFileTrajread[i],
                                        ctx.run_out_dir.subdir("ti/elec/gas/"+stub).full_path)
        # add path to make the output file easier to track
        tiOutElecGasFileTrajread[i]=(ctx.run_out_dir.subdir("ti/elec/gas/"+stub).full_path+"/"+tiOutElecGasFileTrajread[i])
####        tiOutElecGasFileTrajread.append("/home/wfit/FittingWizardWeb/fitting_service/data/2018-05-31_17-37-57-878275_luhq4/2018-05-31_17-37-57-885123_uNYv3/output/ti/elec/gas/"+stub+"/"+tiOutElecGasFileTrajread[i]) ####

        tiInpElecSolvFile=stub+"_ti_elec_solv.inp"
        with ctx.input_dir.subdir(ti_elec_solv_inp_dir+stub).open_file(tiInpElecSolvFile, "w") as ti_solv_inp:
            ti_solv_inp.write(write_ti_elec_solv_inp(top,par,slu,slv,slv_res,lpun,lmbds,lmbds+dlmbElec,
                              charmm_equil_steps, tot_charmm_dyna_steps,T))

        tiInpElecSolvTrajreadFile=stub+"_ti_elec_solv_trajread.inp"
        with ctx.input_dir.subdir(ti_elec_solv_inp_dir+stub).open_file(tiInpElecSolvTrajreadFile, "w") as ti_solv_tr_inp:
            ti_solv_tr_inp.write(write_ti_elec_solv_trajread_inp(top,par,slu,slv,lpun,lmbds,lmbds+dlmbElec,T))

        tiOutElecSolvFile=stub+"_ti_elec_solv.log"
        tiOutElecSolvFileTrajread.append(stub+"_ti_elec_solv_trajread.log")
        tiElecSolvJobScripts.append(ctx.run_out_dir.subdir("ti/elec/solv/"+stub).full_path+"/run-ti-elec-solv.sh")
        create_charmm_twostep_submission_script(ctx, "run-ti-elec-solv.sh", ti_elec_solv_inp_dir+stub+"/"+
                                        tiInpElecSolvFile, tiOutElecSolvFile,
                                        ti_elec_solv_inp_dir+stub+"/"+tiInpElecSolvTrajreadFile,
                                        tiOutElecSolvFileTrajread[i],
                                        ctx.run_out_dir.subdir("ti/elec/solv/"+stub).full_path)
        # add path to make the output file easier to track
        tiOutElecSolvFileTrajread[i]=(ctx.run_out_dir.subdir("ti/elec/solv/"+stub).full_path+"/"+tiOutElecSolvFileTrajread[i])
####        tiOutElecSolvFileTrajread.append("/home/wfit/FittingWizardWeb/fitting_service/data/2018-05-31_17-37-57-878275_luhq4/2018-05-31_17-37-57-885123_uNYv3/output/ti/elec/solv/"+stub+"/"+tiOutElecSolvFileTrajread[i]) ####

        lmbds += dlmbElec
    return tiElecGasJobScripts, tiElecSolvJobScripts, tiOutElecGasFileTrajread, tiOutElecSolvFileTrajread


#################################################################################################

def write_ti_vdw_gas_inp(ctx, ti_vdw_gas_inp_dir, lmb0, lmb1, dlmbVDW, top, slu, slv, lpun, par, slv_res, T, tot_charmm_dyna_steps, charmm_equil_steps):

    if abs(int(round((lmb1-lmb0)/dlmbVDW))-(lmb1-lmb0)/dlmbVDW) > 0.000000001: # if lambda range not exactly divisible by delta_lambda
       ctx.log.info("The lambda range ("+str(lmb0)+" - "+str(lmb1)+") is not divisible by delta lambda for VDW "+str(dlmbVDW)+" (remainder = "+str((lmb1-lmb0)%dlmbVDW)+")\n")
       raise Exception('Invalid delta lambda for VDW (range is not divisible by lambda)')
    nlmbVDW=int(round((lmb1-lmb0)/dlmbVDW))
    lmbds=lmb0

    tiOutVDWGasFile = []
    tiVDWGasJobScripts = []

    for i in range (0,nlmbVDW):
        stub=str("%.6f"%lmbds)+"_"+str("%.6f"%(lmbds+dlmbVDW))
        tiInpVDWGasFile=stub+"_ti_vdw_gas.inp"
        with ctx.input_dir.subdir(ti_vdw_gas_inp_dir+stub).open_file(tiInpVDWGasFile, "w") as ti_gas_inp:
            ti_gas_inp.write(write_ti_vdw_gas_inp_text(top,par,slu,
                              lmbds, lmbds+dlmbVDW, charmm_equil_steps, tot_charmm_dyna_steps,T))
        tiOutVDWGasFile.append(stub+"_ti_vdw_gas.log")
        tiVDWGasJobScripts.append(ctx.run_out_dir.subdir("ti/vdw/gas/"+stub).full_path+"/run-ti-vdw-gas.sh")
        create_charmm_submission_script(ctx, "run-ti-vdw-gas.sh", ti_vdw_gas_inp_dir+stub+"/"+tiInpVDWGasFile,
                                        tiOutVDWGasFile[i],ctx.run_out_dir.subdir("ti/vdw/gas/"+stub).full_path)
        # add path to make the output file easier to track
        tiOutVDWGasFile[i]=(ctx.run_out_dir.subdir("ti/vdw/gas/"+stub).full_path+"/"+tiOutVDWGasFile[i])
####        tiOutVDWGasFile.append("/home/wfit/FittingWizardWeb/fitting_service/data/2018-05-31_17-37-57-878275_luhq4/2018-05-31_17-37-57-885123_uNYv3/output/ti/vdw/gas/"+stub+"/"+tiOutVDWGasFile[i]) ####

        lmbds += dlmbVDW
    return tiVDWGasJobScripts, tiOutVDWGasFile

#################################################################################################

def write_ti_vdw_solv_inp(ctx, ti_vdw_solv_inp_dir, lmb0, lmb1, dlmbVDW, top, slu, slv, lpun, par, slv_res, T, tot_charmm_dyna_steps, charmm_equil_steps):

    if abs(int(round((lmb1-lmb0)/dlmbVDW))-(lmb1-lmb0)/dlmbVDW) > 0.000000001: # if lambda range not exactly divisible by delta_lambda
       ctx.log.info("The lambda range ("+str(lmb0)+" - "+str(lmb1)+") is not divisible by delta lambda for VDW "+str(dlmbVDW)+" (remainder = "+str(abs(int(round((lmb1-lmb0)/dlmbVDW))-(lmb1-lmb0)/dlmbVDW))+") ")
       raise Exception('Invalid delta lambda for VDW (range is not divisible by lambda)')
    nlmbVDW=int(round((lmb1-lmb0)/dlmbVDW))
    lmbds=lmb0

    tiOutVDWSolvFile = []
    tiVDWSolvJobScripts = []

    for i in range (0,nlmbVDW):
        stub=str("%.6f"%lmbds)+"_"+str("%.6f"%(lmbds+dlmbVDW))
        tiInpVDWSolvFile=stub+"_ti_vdw_solv.inp"
        with ctx.input_dir.subdir(ti_vdw_solv_inp_dir+stub).open_file(tiInpVDWSolvFile, "w") as ti_solv_inp:
            ti_solv_inp.write(write_ti_vdw_solv_inp_text(top,par,
                              slu,slv,slv_res,lmbds,lmbds+dlmbVDW,charmm_equil_steps,tot_charmm_dyna_steps,T))
        tiOutVDWSolvFile.append(stub+"_ti_vdw_solv.log")
        tiVDWSolvJobScripts.append(ctx.run_out_dir.subdir("ti/vdw/solv/"+stub).full_path+"/run-ti-vdw-solv.sh")
        create_charmm_submission_script(ctx, "run-ti-vdw-solv.sh", ti_vdw_solv_inp_dir+stub+"/"+tiInpVDWSolvFile,
                                        tiOutVDWSolvFile[i], ctx.run_out_dir.subdir("ti/vdw/solv/"+stub).full_path)
        # add path to make the output file easier to track
        tiOutVDWSolvFile[i]=(ctx.run_out_dir.subdir("ti/vdw/solv/"+stub).full_path+"/"+tiOutVDWSolvFile[i])
####        tiOutVDWSolvFile.append("/home/wfit/FittingWizardWeb/fitting_service/data/2018-05-31_17-37-57-878275_luhq4/2018-05-31_17-37-57-885123_uNYv3/output/ti/vdw/solv/"+stub+"/"+tiOutVDWSolvFile[i]) ####

        lmbds += dlmbVDW
    return tiVDWSolvJobScripts, tiOutVDWSolvFile

#################################################################################################

def write_ti_loop_str(ctx, tot_charmm_dyna_steps, charmm_equil_steps):
    with ctx.input_dir.open_file("loop.str","w") as loop_str:
       loop_txt="""LABEL SNAP
TRAJ READ
SET TIME ?TIME
ENERGY CUTIM 14.0
INCR NTRAJ BY 1
IF @NTRAJ .LT. {steps} GOTO SNAP
""".format(steps=int((tot_charmm_dyna_steps-charmm_equil_steps)/100))
       loop_str.write(loop_txt)

#################################################################################################

def check_charmm_status(ctx,outfile):
    clean=False
    with open(outfile,"r") as charmm_out:
      for line in charmm_out:
         words = line.split()
         if len(words) > 4: 
            if words[0] == "NORMAL" and words[1] == "TERMINATION" and words[3] == "NORMAL" \
                        and words[4] == "STOP":
               clean=True
    if not clean:
       ctx.log.info("CHARMM run "+outfile+" did not exit cleanly, exiting routine\n")
       raise Exception('CHARMM run '+outfile+' did not terminate cleanly')
    else:
       ctx.log.info("CHARMM run "+outfile+" completed successfully\n")

#################################################################################################

def parse_ti_out(ctx,outfile):
    converged=False
    with open(outfile,"r") as charmm_out:
      for line in charmm_out:
         words = line.split()
         if len(words) > 8:
            if words[0] == "PERTURBATION>" and words[1] == "Slow" and words[4] == "EPRTOT=":
               eprtot=float(words[5])
         if len(words) == 3:
            if words[0] == "DIFFLC" and words[1] == "=":
               if float(words[2]) < 0.5:
                  converged=True
                  ctx.log.info("CHARMM TI run "+outfile+" converged successfully: DIFFLC="+words[2]+"\n")
               else:
                  ctx.log.info("CHARMM TI run "+outfile+" not converged: DIFFLC="+words[2]+"\n")

    return converged, eprtot

#################################################################################################

def parse_ti_out_2step(ctx,outfile):
    no=0
    trajEne=[]
    with open(outfile,"r") as charmm_out:
      for line in charmm_out:
         words = line.split()
         if "ENER EXTERN>" in line:
            trajEne.append(float(words[3]))
            no += 1
            if math.isnan(trajEne[-1]):
               ctx.log.info("Error. MTP energy is NaN in "+outfile)
               raise Exception('NaN energy in CHARMM MTP run '+outfile)
         if "ENER IMAGES>" in line:
            trajEne.append(float(words[3]))

    if len(trajEne) == 0:
       ctx.log.info("Error. No energy recorded in "+outfile)
       raise Exception('No energy found in '+outfile)

    avg = sum(trajEne)/no
    std = math.sqrt(sum([(i-avg)**2 for i in trajEne]))/no 

    return avg



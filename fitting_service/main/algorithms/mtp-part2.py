from .toolkit import *
from subprocess import call
from .scripts.fit_mtp import fit_mtp
from .scripts.run_xyz_to_dcm_par import run_xyz_to_dcm_par
from .scripts.mtp_prm_to_pun import mtp_prm_to_pun
from .scripts.pun2charmmlpun import pun_2_charmm_lpun
from .cluster.unibasel_slurm import number_of_cpu_cores, \
    scratch_dir_name, generate_mol_chg_fit_script, cubefit_exe
import os.path
import json

@register

# Routine to perform a molecular MDCM-Fit. Components are:
#	1. Read in atomic MDCM models from setup calculations
#	2. Use different combinations of atomic models to fit molecular models
#          with differential evolution
#

def mtpfit_part2(ctx):

    results = {} # main results list

    # working directory
    work_dir = ctx.run_out_dir.full_path + "/"

    # parse parameters
    ctx.log.info("parameters passed:")

    for parameter, value in ctx.parameters.items():
       ctx.log.debug("input parameter {} is set to {}".format(parameter, value))

    try:
       num_charges = ctx.parameters["mdcm_total_charges"]
    except ValueError:
       pass
    
    # parse saved parameters from part 1 of fit:
    json_parameters = work_dir + '../../params_part1.json'
    with open(json_parameters) as json_file:  
      part1_parameters = json.load(json_file)

    for parameter1, value1 in part1_parameters.items():
       ctx.log.debug("input parameter {} is set to {}".format(parameter1, value1))

    try:
       xyz = part1_parameters["xyz"]
       ncore = part1_parameters["ncore"]
       pot_cube_file = part1_parameters["pot_cube_file"]
       dens_cube_file = part1_parameters["dens_cube_file"]
       mtpl_file = part1_parameters["mtpl_file"]
       part1_dir = part1_parameters["part1_dir"]
       axis_file = ctx.input_dir.full_path + "/" + part1_parameters["axis_file"]
    except ValueError:
       pass

    basename = os.path.splitext(xyz)[0]

    ctx.log.info("Input files:\n\t{}".format("\n\t".join(ctx.input_dir.list_files_recursively())))

    # run fit
    ctx.set_running_status(str('Starting MDCM molecular fit routine with '+str(num_charges)+' charges'))
    script_name='mdcm_mol_fit.sh'
    job_name=str(num_charges)+"_chgs_per_atom"
    create_mol_chg_fit_submission_script(ctx, script_name, mtpl_file, pot_cube_file,
                   dens_cube_file, job_name, cubefit_exe, num_charges, part1_dir, ncore)
    job_id = ctx.schedule_job(ctx.input_dir.full_path + "/" + script_name)
    ctx.wait_for_all_jobs() 
    # get_rmse()...
    ctx.log.info("Finished fit")
    ctx.set_running_status('Finished MDCM molecular fitting routine')

    fit_log = job_name + "_molecular_charge_fit.out"
    ctx.log.info("Parsing "+fit_log)
    fit_rmse, fitted_charges = parse_fitlog(ctx, fit_log)
    fitted_chg_results={}
    for i in range(len(fitted_charges)):
       fitted_chg_results["Q"+str(i)+"_X"] = fitted_charges[i][0]
       fitted_chg_results["Q"+str(i)+"_Y"] = fitted_charges[i][1]
       fitted_chg_results["Q"+str(i)+"_Z"] = fitted_charges[i][2]
       fitted_chg_results["Q"+str(i)+"_Q00"] = fitted_charges[i][3]
    fitted_xyz_file = str(num_charges) + "charges.xyz"
    ctx.log.info("Writing MDCM xyz file for web interface")
    ui_xyz_file = create_ui_xyz(ctx, xyz, fitted_xyz_file, num_charges)
    comb_xyz_file = create_comb_xyz(ctx, xyz, fitted_xyz_file, num_charges)

    # convert fit output to mdcm file
    dcm_par_file = work_dir + basename + ".dcm"
    ctx.log.info("Writing MDCM parameter file "+dcm_par_file)
    comb_xyz_file = work_dir + comb_xyz_file
    run_xyz_to_dcm_par(comb_xyz_file, axis_file, dcm_par_file)

    # gather results 
    ctx.log.info("Gathering results to results.json")
    ctx.log.info("RMSE = "+ str(fit_rmse) + ", chgs = " + str(fitted_charges))
    results={
       "mdcm_fit_RMSE" : fit_rmse,
       "mdcm_fit_results" : fitted_chg_results,
       "ui_xyz_file" : ui_xyz_file
    }
    ctx.write_results(results)



################################################################################################

def _get_and_log_job_status(ctx, job_id, expected_status):
        job_status = ctx.job_status(job_id)
        ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))


################################################################################################

def parse_fitlog(ctx, mdcm_fit_log):

    kj_to_kcal = 0.239006
    flag=0
    rmse=-999
    charges=[]
    with ctx.run_out_dir.open_file(mdcm_fit_log, "r") as f_log:
        for line in f_log:
            words = line.split()
            if len(words) == 6:
                if words[0] == "Best" and words[1] == "found":
                    flag = 1
            if len(words) == 2 and flag == 1:
                if words[0] == "RMSE":
                    flag = 0
                    rmse = float(words[1])
            if len(words) == 4 and flag == 2:
                charges.append([words[0], words[1], words[2], words[3]])
            if len(words) == 4:
                if words[0] == "x[bohr]" and words[1] == "y[bohr]":
                    flag = 2
                    
        ctx.log.info("RMSE of fit is " + str(rmse))
    f_log.close()
    return rmse, charges

################################################################################################

def create_mol_chg_fit_submission_script(ctx,
                                          filename,
                                          multipole_file,
                                          pot_cube_file,
                                          dens_cube_file,
                                          job_name,
                                          cubefit_exe,
                                          num_charges,
                                          part1_dir,
                                          number_of_cores=None):
    number_of_cores = number_of_cores if number_of_cores is not None else number_of_cpu_cores
    with ctx.input_dir.open_file(filename, "w") as script_file:
        script_file.write(generate_mol_chg_fit_script(multipole_file,
                                                       pot_cube_file,
                                                       dens_cube_file,
                                                       job_name,
                                                       number_of_cores,
                                                       ctx.run_out_dir.full_path,
                                                       cubefit_exe,
                                                       part1_dir,
                                                       num_charges))

################################################################################################

def create_ui_xyz(ctx,
                  xyzfile,
                  mdcmfile,
                  num_charges):
   coords=[]
   natm=0
   l=1
   i=0

   ui_file = str(num_charges) + "_ui_file.xyz"

   with ctx.input_dir.open_file(xyzfile, "r") as f_xyz:
        for line in f_xyz:
            words = line.split()
            if l==1:
              tatm = int(words[0])
            if l > 2 and l <= tatm+2:
              coords.append(["H", float(words[1])*5, float(words[2])*5,
                           float(words[3])*5])
              natm=natm+1
            l=l+1

   l=1
   with ctx.run_out_dir.open_file(mdcmfile, "r") as f_mdcm:
        for line in f_mdcm:
            words = line.split()
            if l==1:
              tatm = int(words[0])
            if l > 2 and l <= tatm+2:
              coords.append([words[0], float(words[1])*5, float(words[2])*5,
                           float(words[3])*5])
              natm=natm+1
            l=l+1

   with ctx.run_out_dir.open_file(ui_file, "w") as f_ui:
     f_ui.write(str(natm) + "\n\n")
     for atm in range(len(coords)):
       f_ui.write(coords[atm][0] + " " + str(coords[atm][1]) + " " + 
              str(coords[atm][2]) + " " + str(coords[atm][3]) + "\n")
     f_ui.write("\n")

   f_xyz.close()
   f_mdcm.close()
   f_ui.close()
   return ui_file

################################################################################################

def create_comb_xyz(ctx,
                  xyzfile,
                  mdcmfile,
                  num_charges):
   coords=[]
   natm=0
   l=1
   i=0

   comb_file = str(num_charges) + "_chg_mdcm_and_nuclei.xyz"

   with ctx.input_dir.open_file(xyzfile, "r") as f_xyz:
        for line in f_xyz:
            words = line.split()
            if l==1:
              tatm = int(words[0])
            if l > 2 and l <= tatm+2:
              coords.append([words[0], float(words[1]), float(words[2]),
                           float(words[3])," "])
              natm=natm+1
            l=l+1

   l=1
   with ctx.run_out_dir.open_file(mdcmfile, "r") as f_mdcm:
        for line in f_mdcm:
            words = line.split()
            if l==1:
              tatm = int(words[0])
            if l > 2 and l <= tatm+2:
              coords.append(["X", float(words[1]), float(words[2]),
                           float(words[3]), float(words[4])])
              natm=natm+1
            l=l+1

   with ctx.run_out_dir.open_file(comb_file, "w") as f_ui:
     f_ui.write(str(natm) + "\n\n")
     for atm in range(len(coords)):
       f_ui.write(coords[atm][0] + " " + str(coords[atm][1]) + " " +
              str(coords[atm][2]) + " " + str(coords[atm][3]) + " " +
              str(coords[atm][4]) + "\n")
     f_ui.write("\n")

   f_xyz.close()
   f_mdcm.close()
   f_ui.close()
   return comb_file

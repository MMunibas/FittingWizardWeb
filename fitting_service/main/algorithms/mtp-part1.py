import json
import os.path

from .cluster.unibasel_slurm import generate_gau_setup_script, number_of_cpu_cores, \
    gau_login_script, scratch_dir_name, gau_formchk, gdma, cubegen, fieldcomp, babel, \
    generate_atom_chg_fit_script, cubefit_exe
from .gaussian_input import *
from .scripts.calc_LRA import calculate_LRA
from .scripts.mtp_fittab_maker import mk_fittab_mtp
from .scripts.mtpfit import mtpfit
from .toolkit import *


@register
# Routine to run initial calculations for an MDCM-Fit. Components are:
#	1. Run gaussian to generate a checkpoint file
#	2. Use formchk and cubegen to generate an ESP grid
#       3. Run mtpfit.py to fit high-ranking atomic multipoles to the MEP
#       4. Run pcubefit.x to fit atomic MDCM charge models to the atomic electrostatic potential
#       5. Run pcubefit.x to fit molecular charge models to the MEP

def mtpfit_part1(ctx):
    LMAX=5 # maximum atomic multipole rank for multipole fitting
    max_chgs_per_atom=4 # maximum number of MDCM charges to fit per atom to provide initial guesses for molecular fit

    results = {}  # main results list

    # parse parameters
    ctx.log.info("parameters passed:")

    for parameter, value in ctx.parameters.items():
        ctx.log.debug("input parameter {} is set to {}".format(parameter, value))

    try:
        xyz = ctx.parameters["mtp_gen_filename_xyz"]
        charge = ctx.parameters["mtp_gen_molecule_charge"]
        multiplicity = ctx.parameters["mtp_gen_molecule_multiplicity"]
        cmd = ctx.parameters["mtp_gen_gaussian_input_commandline"]
        ncore = ctx.parameters["mtp_gen_gaussian_num_cores"]
        axisFile = ctx.parameters["dcm_axis_filename"]
    except ValueError:
        pass

    ctx.log.info("Input files:\n\t{}".format("\n\t".join(ctx.input_dir.list_files_recursively())))

    ctx.log.info("Writing Gaussian input files\n")
    ctx.set_running_status('setting up Gaussian calculations')

    # set global calculation directory
#    calc_out_dir=ctx.input_dir.subdir("../output/").full_path + "/"

    # set up Gaussian single point calculation to generate checkpoint file
    basename = os.path.splitext(xyz)[0]
    xyz_file_name = ctx.input_dir.full_path + '/' + xyz
    ctx.log.info("Setting up Gaussian input file\n")
    mtp_inp_dir = ctx.input_dir.full_path + '/'
    gau_inp_name = mtp_inp_dir + basename + ".com"
#    mtp_out_dir = ctx.run_out_dir.subdir("mtp").full_path + "/"
    mtp_out_dir = ctx.run_out_dir.full_path + "/"
    gau_out_name = mtp_out_dir + basename + ".out"
    chk_name = mtp_out_dir + basename + ".chk"
    fchk_name = mtp_out_dir + basename + ".fchk"
    gau_inp_file = write_gaussian_inp(ctx, xyz, gau_inp_name, chk_name, cmd, charge, multiplicity, ncore).name

    # set up cubegen grid parameters for ESP grid file
    grid_pars = calc_grid_specs(ctx, gau_inp_file)
    pot_cube_file = basename + ".pot.cube"
    dens_cube_file = basename + ".dens.cube"

    # now run the prepared inputs from a single submission script
    ctx.log.info("submitting gaussian calculations:")
    ctx.set_running_status('submitting Gaussian calculations')
    create_gau_submission_script(ctx,
                                 "run-gau.sh", gau_inp_file, gau_out_name, chk_name, fchk_name, 
                                 grid_pars, pot_cube_file, dens_cube_file, ncore)

    job_id = ctx.schedule_job(ctx.input_dir.full_path + "/run-gau.sh") 
    ctx.wait_for_all_jobs() 

    ctx.log.info("jobs completed")

    # fit high-ranking (l=LMAX) multipoles to MEP in Gaussian cube files, write output to mtpl_file
    ctx.log.info("Fitting atomic multipoles")
    ctx.set_running_status('Fitting atomic multipoles') 
    mtpl_file = mtp_out_dir + basename + "-mtpl.dat"
    pot_cube_file = mtp_out_dir + pot_cube_file
    dens_cube_file = mtp_out_dir + dens_cube_file
    mtpfit(pot_cube_file, dens_cube_file, LMAX, charge, mtpl_file, results)

    # now fit atomic charge models to atomic multipoles using "pcubefit.x" (O. Unke). Each number of charges is
    # submitted as a separate job (i.e. job 1 fits 1 charge per atom, job 2 fits 2 charges per atom and so on...)
    ctx.log.info("Fitting atomic charge models to atomic multipole expansions")
    ctx.set_running_status('Fitting atomic charge models to atomic multipoles')
    for i in range(1, max_chgs_per_atom+1):
      job_name=str(i)+"_chgs_per_atom"
      script_name="fit_"+job_name+".sh"
      create_atom_chg_fit_submission_script(ctx, script_name, mtpl_file, pot_cube_file, dens_cube_file, 
                   i, job_name, cubefit_exe, ncore)
      job_id = ctx.schedule_job(ctx.input_dir.full_path + "/" + script_name)
    ctx.wait_for_all_jobs()

    # now store parameters in json file for molecular fitting
    part1_params = {'mtpl_file': mtpl_file, 'pot_cube_file':pot_cube_file,
                 'dens_cube_file':dens_cube_file, 'ncore':ncore, 'xyz':xyz,
                 'part1_dir':mtp_out_dir, 'axis_file':axisFile}
    with ctx.run_out_dir.open_file("../../params_part1.json", "w") as json_file:
       json.dump(part1_params, json_file)

    # gather results for subsequent fitting steps
    ctx.log.info("Gathering results")

    data={}
    data["mdcm_fit_results"]=results
    ctx.write_results(data)
#    with ctx.run_out_dir.open_file("results.json", "w") as json_file:
#        json.dump(results, json_file)


#################################################################################################

def _get_and_log_job_status(ctx, job_id, expected_status):
    job_status = ctx.job_status(job_id)
    ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))


#################################################################################################
# Grid specifications for ESP cube file generation

def calc_grid_specs(ctx, gau_inp_file):
    grid_stepsize = 0.1
    grid_extra_space = 5

    xyz = []
    with ctx.input_dir.open_file(gau_inp_file, "r") as gau_inp:
        flag = 0
        for line in gau_inp:
            words = line.split()
            if flag == 1 and len(words) < 4:
                flag = 0
            elif flag == 1 and len(words) >= 4:
                xyz.append([words[1], words[2], words[3]])
            if flag == 0 and len(words) == 4 and words[0] == "Single" and words[1] == "point" and words[2] == "energy":
                flag = 2
            if flag == 2 and len(words) == 2:
                flag = 1

    gau_inp.close()

    xmin = None
    xmax = None
    ymin = None
    ymax = None
    zmin = None
    zmax = None

    for i in range(len(xyz)):
        if xmin == None or xmin > float(xyz[i][0]):
            xmin = float(xyz[i][0])
        if xmax == None or xmax < float(xyz[i][0]):
            xmax = float(xyz[i][0])
        if ymin == None or ymin > float(xyz[i][1]):
            ymin = float(xyz[i][1])
        if ymax == None or ymax < float(xyz[i][1]):
            ymax = float(xyz[i][1])
        if zmin == None or ymin > float(xyz[i][2]):
            zmin = float(xyz[i][2])
        if zmax == None or xmax < float(xyz[i][2]):
            zmax = float(xyz[i][2])

    range_x = round(xmax - xmin + 2 * grid_extra_space, 1)
    range_y = round(ymax - ymin + 2 * grid_extra_space, 1)
    range_z = round(zmax - zmin + 2 * grid_extra_space, 1)

    xstart = round(xmin - grid_extra_space, 1)
    ystart = round(ymin - grid_extra_space, 1)
    zstart = round(zmin - grid_extra_space, 1)

    grid_spec = '"-1, ' + str(xstart) + ', ' + str(ystart) + ', ' + str(zstart) + '\n ' + str(
        int(1 + range_x / grid_stepsize)) + ', ' + str(grid_stepsize) + ', 0.0, 0.0\n ' + str(
        int(1 + range_y / grid_stepsize)) + ', 0.0, ' + str(grid_stepsize) + ', 0.0\n ' + str(
        int(1 + range_z / grid_stepsize)) + ', 0.0, 0.0, ' + str(grid_stepsize) + '" '

    return grid_spec


#################################################################################################
# VDW file for ESP exclusion during multipole fitting

def write_vdw_file(ctx, gau_inp_file, vdw_file_name, mtp_order):
    vdw_radii = {'H': 2.268,
                 'He': 2.301,
                 'Du': 0.0,
                 'B': 3.931,
                 'C': 3.496,
                 'N': 2.91,
                 'O': 2.646,
                 'F': 2.551,
                 'P': 3.591,
                 'S': 3.496,
                 'Cl': 3.42,
                 'Br': 3.685,
                 'I': 4.063,
                 'Fe': 4.0,
                 'Zn': 2.80,
                 '1': 2.268,
                 '2': 2.301,
                 '0': 0.0,
                 '5': 3.931,
                 '6': 3.496,
                 '7': 2.91,
                 '8': 2.646,
                 '9': 2.551,
                 '15': 3.591,
                 '16': 3.496,
                 '17': 3.42,
                 '35': 3.685,
                 '53': 4.063,
                 '26': 4.0,
                 '30': 2.80}

    atm = []
    with ctx.input_dir.open_file(gau_inp_file, "r") as gau_inp:
        flag = 0
        for line in gau_inp:
            words = line.split()
            if flag == 1 and len(words) < 4:
                flag = 0
            elif flag == 1 and len(words) >= 4:
                atm.append(words[0])
            if flag == 0 and len(words) == 4 and words[0] == "Single" and words[1] == "point" and words[2] == "energy":
                flag = 2
            if flag == 2 and len(words) == 2:
                flag = 1

    gau_inp.close()

    with ctx.run_out_dir.open_file(vdw_file_name, 'w') as vdw_file:
        vdw_file.write('\n')
        for i in range(len(atm)):
            if atm[i] in vdw_radii:
               radius=vdw_radii[atm[i]]
            else:
               radius=3.5
            vdw_file.write(str(vdw_radii[atm[i]]) + '\n')
            vdw_file.write(str(mtp_order) + '\n\n')

    vdw_file.close()

def create_atom_chg_fit_submission_script(ctx,
                                          filename,
                                          multipole_file,
                                          pot_cube_file,
                                          dens_cube_file,
                                          chgs_per_atom,
                                          job_name,
                                          cubefit_exe,
                                          number_of_cores=None):
    number_of_cores = number_of_cores if number_of_cores is not None else number_of_cpu_cores 
    with ctx.input_dir.open_file(filename, "w") as script_file:
        script_file.write(generate_atom_chg_fit_script(multipole_file,
                                                       pot_cube_file,
                                                       dens_cube_file,
                                                       chgs_per_atom,
                                                       job_name,
                                                       number_of_cores,
                                                       ctx.run_out_dir.full_path,
                                                       cubefit_exe))

def create_gau_submission_script(ctx,
                                 filename,
                                 gau_input_file_name,
                                 gau_output_file_name,
                                 chk_file,
                                 fchk_file,
                                 grid_spec,
                                 pot_cube_file,
                                 dens_cube_file,
                                 number_of_cores=None):
    number_of_cores = number_of_cores if number_of_cores is not None else number_of_cpu_cores
    with ctx.input_dir.open_file(filename, "w") as script_file:
        script_file.write(generate_gau_setup_script(gau_input_file_name,
                                                    gau_output_file_name,
                                                    ctx.run_out_dir.full_path,
                                                    number_of_cores,
                                                    ctx._calculation_id,
                                                    gau_login_script,
                                                    scratch_dir_name,
                                                    gau_formchk,
                                                    chk_file,
                                                    fchk_file,
                                                    grid_spec,
                                                    cubegen,
                                                    pot_cube_file,
                                                    dens_cube_file))

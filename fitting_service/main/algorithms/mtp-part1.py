import json
import os.path

from .cluster.unibasel_grid_engine import generate_gau_setup_script, number_of_cpu_cores, \
    gau_login_script, scratch_dir_name, gau_formchk, gdma, cubegen, fieldcomp, babel
from .gaussian_input import *
from .scripts.calc_LRA import calculate_LRA
from .scripts.mtp_fittab_maker import mk_fittab_mtp
from .toolkit import *


@register
# Routine to run initial calculations for an MTP-Fit. Components are:
#	1. Run gaussian to generate a checkpoint file
#	2. Use formchk and cubegen to generate an ESP grid
#       3. Run GDMA to obtain reference atomic multipoles
#       4. Run helper scripts for atom typing, to define local axes and to set up fitting table
# References: DOI 10.1021/acs.jcim.6b00280

def mtpfit_part1(ctx):
    results = {}  # main results list

    # parse parameters
    ctx.log.info("parameters passed:")

    for parameter, value in ctx.parameters.items():
        ctx.log.debug("input parameter {} is set to {}".format(parameter, value))

    try:
        xyz = ctx.parameters["xyz"]
        charge = ctx.parameters["charge"]
        multiplicity = ctx.parameters["multiplicity"]
        cmd = ctx.parameters["cmd"]
        ncore = ctx.parameters["ncore"]
    except ValueError:
        pass

    ctx.log.info("Input files:\n\t{}".format("\n\t".join(ctx.input_dir.list_files_recursively())))

    ctx.log.info("Writing Gaussian and GDMA input files\n")
    # set up Gaussian single point calculation to generate checkpoint file
    basename = os.path.splitext(xyz)[0]
    xyz_file_name = ctx.input_dir.full_path + '/' + xyz
    ctx.log.info("Setting up Gaussian input file\n")
    mtp_inp_dir = ctx.input_dir.subdir("mtp").full_path + "/"
    gau_inp_name = mtp_inp_dir + basename + ".com"
    mtp_out_dir = ctx.run_out_dir.subdir("mtp").full_path + "/"
    gau_out_name = mtp_out_dir + basename + ".out"
    chk_name = mtp_out_dir + basename + ".chk"
    fchk_name = mtp_out_dir + basename + ".fchk"
    gau_inp_file = write_gaussian_inp(ctx, xyz, gau_inp_name, chk_name, cmd, charge, multiplicity, ncore).name

    # set up GDMA calculation on Gaussian fchk file
    pun_name = basename + ".gdma.pun"
    gdma_inp_name = basename + ".gdma.inp"
    gdma_out_name = basename + ".gdma.log"
    gdma_sh = mtp_out_dir + basename + ".gdma.sh"
    gdma_fchk = basename + ".fchk"
    gdma_inp_file = write_gdma_inp(ctx, mtp_out_dir, gdma_inp_name, gdma_out_name, gdma_fchk, pun_name, 0).name

    # set up cubegen grid parameters for ESP grid file
    grid_pars = calc_grid_specs(ctx, gau_inp_file)
    cube_file = basename + ".pot.cube"

    # write vdw radius file for fitting script (used to exclude ESP inside atomic radius)
    vdw_file_name = basename + ".vdw"
    write_vdw_file(ctx, gau_inp_file, vdw_file_name, "mtp", 0)

    # get sdf file name
    sdf_file_name = mtp_out_dir + basename + ".sdf"

    # now run the prepared inputs from a single submission script
    ctx.log.info("submitting gaussian and gdma calculations:")
    create_gau_submission_script(ctx,
                                 "run-gau.sh", gau_inp_file, gau_out_name, "mtp", chk_name, fchk_name, gdma_inp_name,
                                 gdma_out_name, grid_pars, cube_file, pun_name, vdw_file_name, xyz_file_name,
                                 sdf_file_name, ncore)

    job_id = ctx.schedule_job(ctx.input_dir.full_path + "/mtp/run-gau.sh")
    ctx.wait_for_all_jobs()

    ctx.log.info("jobs completed")

    ##    mtp_out_dir="/home/wfit/FittingWizardWeb/fitting_service/data/2018-04-30_12-29-56-773298_PXy2d/2018-04-30_12-29-56-779055_6_c02/output/mtp/"
    ##    sdf_file_name="/home/wfit/FittingWizardWeb/fitting_service/data/2018-04-30_12-29-56-773298_PXy2d/2018-04-30_12-29-56-779055_6_c02/output/mtp/nma.sdf"
    # now calculate local reference axes
    ctx.log.info("Calculating local reference axes for " + sdf_file_name)
    calculate_LRA(sdf_file_name)

    # and generate fitting table
    local_pun_name = basename + "_l.pun"
    ctx.log.info("Generating fitting table for " + cube_file + ", " + vdw_file_name + ", " + local_pun_name)
    mk_fittab_mtp(mtp_out_dir + cube_file, mtp_out_dir + vdw_file_name, mtp_out_dir + local_pun_name)

    # gather files for subsequent fitting steps
    ctx.log.info("Gathering results")

    with ctx.run_out_dir.open_file("results.json", "w") as json_file:
        json.dump(results, json_file)


#################################################################################################33

def _get_and_log_job_status(ctx, job_id, expected_status):
    job_status = ctx.job_status(job_id)
    ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))


#################################################################################################33
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


#################################################################################################33
# VDW file for ESP exclusion during multipole fitting

def write_vdw_file(ctx, gau_inp_file, vdw_file_name, sub, mtp_order):
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
                 'Zn': 2.80}

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

    with ctx.run_out_dir.subdir(sub).open_file(vdw_file_name, 'w') as vdw_file:
        vdw_file.write('\n')
        for i in range(len(atm)):
            vdw_file.write(str(vdw_radii[atm[i]]) + '\n')
            vdw_file.write(str(mtp_order) + '\n\n')

    vdw_file.close()


def create_gau_submission_script(ctx,
                                 filename,
                                 gau_input_file_name,
                                 gau_output_file_name,
                                 workdir_name,
                                 chk_file,
                                 fchk_file,
                                 gdma_inp_name,
                                 gdma_out_name,
                                 grid_spec,
                                 cube_file,
                                 gdma_pun_file,
                                 vdw_file_name,
                                 xyz_file_name,
                                 sdf_file_name,
                                 number_of_cores=None):
    number_of_cores = number_of_cores if number_of_cores is not None else number_of_cpu_cores
    with ctx.input_dir.subdir(workdir_name).open_file(filename, "w") as script_file:
        script_file.write(generate_gau_setup_script(gau_input_file_name,
                                                    gau_output_file_name,
                                                    ctx.run_out_dir.subdir(workdir_name).full_path,
                                                    number_of_cores,
                                                    ctx._calculation_id,
                                                    gau_login_script,
                                                    scratch_dir_name,
                                                    gau_formchk,
                                                    chk_file,
                                                    fchk_file,
                                                    gdma,
                                                    gdma_inp_name,
                                                    gdma_out_name,
                                                    grid_spec,
                                                    cubegen,
                                                    cube_file,
                                                    fieldcomp,
                                                    gdma_pun_file,
                                                    vdw_file_name,
                                                    babel,
                                                    xyz_file_name,
                                                    sdf_file_name))

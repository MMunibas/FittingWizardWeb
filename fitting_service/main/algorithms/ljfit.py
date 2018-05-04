from .cluster.unibasel_grid_engine import generate_charmm_setup_script, number_of_cpu_cores, charmm_executable, ld_path, \
    env_path, mpi_executable, mpi_flags, scratch_dir_name
from .toolkit import *
from .charmm_input import *
from .scripts.run_scale_vdw import *
import json


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
    results = {}  # main results list

    # parse parameters
    ctx.log.info("parameters passed:")

    for parameter, value in ctx.parameters.items():
        ctx.log.debug("input parameter {} is set to {}".format(parameter, value))

    try:
        par = ctx.parameters["filename_charmm_parameter"]
        top = ctx.parameters["filename_charmm_topology"]
        slu = ctx.parameters["filename_solute_pdb"]
        slv = ctx.parameters["filename_solvent_pdb"]
        lpun = ctx.parameters["filename_mtpl_lpun"]
        pureliq = ctx.parameters["filename_pureliquid_pdb"]
        lmb0 = float(ctx.parameters["ti_lambda_0"])
        lmb1 = float(ctx.parameters["ti_lambda_1"])
        dlmbElec = float(ctx.parameters["ti_lambda_window_electrostatic"])
        dlmbVDW = float(ctx.parameters["ti_lambda_window_vdw"])
        T = float(ctx.parameters["charmm_simulation_temperature"])
        epsfac = float(ctx.parameters["lj_scaling_factor_eps"])
        sigfac = float(ctx.parameters["lj_scaling_factor_sig"])
    except ValueError:
        pass

    ctx.log.info("Input files:\n\t{}".format("\n\t".join(ctx.input_dir.list_files_recursively())))

    # scale LJ parameters to trial values for this fitting step
    inpdir = ctx.input_dir.name + "/"
    run_scale_vdw(inpdir + slu, inpdir + top, inpdir + par, sigfac, epsfac, ctx.input_dir.name)

    # set up and submit gas-phase calculation for vaporization enthalpy in "dens" folder
    ctx.log.info("writing input for gas phase calculation:")
    gas_inp_dir = ctx.input_dir.subdir("dens").full_path
    gas_inp_name = gas_inp_dir + "/gas.inp"
    gas_out_dir = ctx.run_out_dir.subdir("dens").full_path
    gas_out_name = gas_out_dir + "/gas.out"
    gas_inp_file = write_gas_inp(ctx, gas_inp_name, top, slu, lpun).name

    ctx.log.info("submitting gas-phase calculation:")
    create_charmm_submission_script(ctx, "run-gas.sh", gas_inp_file, gas_out_name, "dens")
    #####    gas_out_name="/home/wfit/FittingWizardWeb/fitting_service/data/mike-test-lj/gas.out" #######
    job_id = ctx.schedule_job(ctx.input_dir.full_path + "/dens/run-gas.sh")

    # set up and submit pure liquid calculation for vaporization enthalpy and density in "dens" folder
    ctx.log.info("writing input for pure liquid calculation:")
    dens_inp_dir = ctx.input_dir.subdir("dens").full_path
    dens_inp_name = dens_inp_dir + "/density.inp"
    dens_out_dir = ctx.run_out_dir.subdir("dens").full_path
    dens_out_name = dens_out_dir + "/density.out"
    dens_inp_file = write_dens_inp(ctx, dens_inp_name, top, pureliq, lpun, T).name

    ctx.log.info("submitting density calculation:")
    create_charmm_submission_script(ctx, "run.sh", dens_inp_file, dens_out_name, "dens")

    #####    dens_out_name="/home/wfit/FittingWizardWeb/fitting_service/data/mike-test-lj/density.out" #######
    job_id = ctx.schedule_job(ctx.input_dir.full_path + "/dens/run.sh")
    ctx.wait_for_all_jobs()

    # gather results for vaporization enthalpy
    parse_dens_out(ctx, slu, top, results, T, gas_out_name, dens_out_name)

    ctx.log.info("Density read from file " + dens_out_name + ": " + str(results["pure_liquid_density"]) + " g/cm^3\n")
    ctx.log.info("Vaporization Enthalpy: " + str(results["vaporization_enthalpy"]) + " kcal/mol\n")

    with ctx.run_out_dir.open_file("results.json", "w") as json_file:
        json.dump(results, json_file)


#################################################################################################33

def _get_and_log_job_status(ctx, job_id, expected_status):
    job_status = ctx.job_status(job_id)
    ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))


#################################################################################################33

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
        raise Exception("Job " + gas_out_name.name + " did not finish successfully")
    else:
        status = 0

    # parse CHARMM pure liquid output file to extract simulation output
    with ctx.run_out_dir.open_file(dens_out_name, "r") as liq_output:
        for line in liq_output:
            words = line.split()
            if len(words) > 0:
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
                                                       ctx.input_dir.name))

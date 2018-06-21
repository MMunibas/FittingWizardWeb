from .toolkit import *
from subprocess import call
from .scripts.fit_mtp import fit_mtp
from .scripts.mtp_prm_to_pun import mtp_prm_to_pun
from .scripts.pun2charmmlpun import pun_2_charmm_lpun
import os.path
import json

@register

# Routine to perform an MTP-Fit. Components are:
#	1. Run gaussian to generate a checkpoint file
#	2. use formchk and cubegen to generate an ESP grid
#
# References: DOI 10.1021/acs.jcim.6b00280

def mtpfit_part2(ctx):

    results = {} # main results list

    # parse parameters
    ctx.log.info("parameters passed:")

    for parameter, value in ctx.parameters.items():
       ctx.log.debug("input parameter {} is set to {}".format(parameter, value))

    try:
       tabfile = ctx.parameters["mtp_fitting_table_filename"]
       threshold = ctx.parameters["mtp_fitting_threshold"]
       rank = ctx.parameters["mtp_fitting_rank"]
       ignore_H = ctx.parameters["mtp_fitting_flag_ignore_H"]
       initial_charges = ctx.parameters["mtp_fitting_initial_charges"]
#       fit_number = ctx.parameters["mtp_fit_number"]
    except ValueError:
       pass
    
    # set global calculation directory
    calc_out_dir=ctx.input_dir.subdir("../output/").full_path + "/" 

    tabfile = calc_out_dir + tabfile
    fit_outfile = ctx.run_out_dir.full_path + "/fitting_log.txt"

    ctx.log.info("Input files:\n\t{}".format("\n\t".join(ctx.input_dir.list_files_recursively())))

    # run fit
    ctx.set_running_status('Starting fit_mtp routine')
    fit_rmse, fitted_multipoles = fit_mtp(rank, initial_charges, fit_outfile, threshold, ignore_H, tabfile)
    ctx.log.info("Finished fit")
    ctx.set_running_status('Finished fit_mtp routine')

    # convert fit output to pun file
    fitted_pun = ctx.run_out_dir.full_path + "/fitted_multipoles.pun"
    ref_pun = calc_out_dir + "gdma_ref.pun"
##    ref_pun = "/home/wfit/FittingWizardWeb/fitting_service/data/mike-test-mtp/output/nma_l.pun" ###
    ctx.log.info("Creating fitted punch file "+fitted_pun+" from reference pun file "+ref_pun)
    mtp_prm_to_pun(fit_outfile, ref_pun, fitted_pun)
  
    # convert fitted pun file to CHARMM lpun format
    fitted_lpun = ctx.run_out_dir.full_path + "/fitted_multipoles.lpun"
    ctx.log.info("Converting fitted punch file "+fitted_pun+" to charmm lpun format")
    pun_2_charmm_lpun(fitted_pun, fitted_lpun)

    # gather results for vaporization enthalpy
    ctx.log.info("Gathering results to results.json")
    results={
       "mtp_fit_RMSE" : fit_rmse,
       "mtp_fit_results" : fitted_multipoles
    }
    ctx.write_results(results)



################################################################################################

def _get_and_log_job_status(ctx, job_id, expected_status):
        job_status = ctx.job_status(job_id)
        ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))


################################################################################################

def parse_fieldcomp(ctx, fieldcomp_log):

    kj_to_kcal = 0.239006
    flag=0
    with ctx.run_out_dir.open_file(fieldcomp_log, "r") as f_log:
        for line in f_log:
            words = line.split()
            if len(words) == 4:
                if words[2] == "total" and words[3] == "space":
                    flag = 1
            if len(words) == 4 and flag == 1:
                if words[0] == "difference" and words[1] == "average:":
                    flag = 0
                    rmse = float(words[2]) * kj_to_kcal
                    
        ctx.log.info("RMSE of fit is " + str(rmse))
    f_log.close()
    return rmse


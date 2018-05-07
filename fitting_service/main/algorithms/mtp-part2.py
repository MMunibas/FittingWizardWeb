from .toolkit import *
from subprocess import call
from .scripts.fit_mtp import fit_mtp
from .scripts.mtp_prm_to_pun import mtp_prm_to_pun
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
       chgfile = ctx.parameters["mtp_fitting_charge_filename"]
       fit_number = ctx.parameters["mtp_fit_number"]
    except ValueError:
       pass
    
    # set global calculation directory
    calc_out_dir=ctx.input_dir.subdir("../output/").full_path + "/" 

    tabfile = calc_out_dir+"mtpfittab.txt"
    chgfile = ctx.input_dir.full_path + "/" + chgfile
    fit_outfile = ctx.run_out_dir.full_path + "/fit_" + str(fit_number) + ".txt"

    ctx.log.info("Input files:\n\t{}".format("\n\t".join(ctx.input_dir.list_files_recursively())))

    # run fit
    fit_mtp(rank, chgfile, fit_outfile, threshold, ignore_H, tabfile)
    ctx.log.info("Finished fit")

    # convert fit output to pun file
    new_pun = ctx.output_dir.full_path + "/fit_" + str(fit_number) + ".pun"
    ref_lpun = calc_out_dir + "gdma_ref.pun"
    ref_lpun = "/home/wfit/FittingWizardWeb/fitting_service/data/mike-test-mtp/output/nma.gdma.pun" ####
    ctx.log.info("Creating fitted punch file "+new_pun)
    mtp_prm_to_pun(fit_outfile, ref_lpun, new_pun)

    # gather results for vaporization enthalpy
#    parse_mtp_out(ctx,mtp_out_name)

#    ctx.log.info("Density read from file "+dens_out_name+": "+str(results["density"])+" g/cm^3\n")

    with ctx.run_out_dir.open_file("results.json", "w") as json_file:
       json.dump(results,json_file)



#################################################################################################33

def _get_and_log_job_status(ctx, job_id, expected_status):
        job_status = ctx.job_status(job_id)
        ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))


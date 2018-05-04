from .toolkit import *
from subprocess import call
from .scripts.fit_mtp import fit_mtp
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
       tabfile = ctx.parameters["tabfile"]
       threshold = ctx.parameters["threshold"]
       rank = ctx.parameters["rank"]
       ignore_H = ctx.parameters["ignore_H"]
       chgfile = ctx.parameters["chgfile"]
       outfile = ctx.parameters["outfile"]
    except ValueError:
       pass
    
    chgfile = ctx.input_dir.full_path + "/" + chgfile
    outfile = ctx.run_out_dir.full_path + "/" + outfile

    ctx.log.info("Input files:\n\t{}".format("\n\t".join(ctx.input_dir.list_files_recursively())))

    fit_mtp(rank, chgfile, outfile, threshold, ignore_H, tabfile)


    # gather results for vaporization enthalpy
#    parse_mtp_out(ctx,mtp_out_name)

#    ctx.log.info("Density read from file "+dens_out_name+": "+str(results["density"])+" g/cm^3\n")

    with ctx.run_out_dir.open_file("results.json", "w") as json_file:
       json.dump(results,json_file)



#################################################################################################33

def _get_and_log_job_status(ctx, job_id, expected_status):
        job_status = ctx.job_status(job_id)
        ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))


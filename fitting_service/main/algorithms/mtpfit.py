from .toolkit import *
from subprocess import call
from .gaussian_input import *
import os.path
import json

@register

# Routine to perform an MTP-Fit. Components are:
#	1. Run gaussian to generate a checkpoint file
#	2. use formchk and cubegen to generate an ESP grid
#
# References: DOI 10.1021/acs.jcim.6b00280

def mtpfit(ctx):

    results = {} # main results list

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

    # set up and submit Gaussian calculation to calculate ESP across grid
    basename=os.path.splitext(xyz)[0]
    ctx.log.info("Setting up Gaussian input file\n")
    mtp_inp_dir=ctx.input_dir.subdir("mtp").full_path
    gau_inp_name=mtp_inp_dir+"/"+basename+".com"
    mtp_out_dir=ctx.output_dir.subdir("mtp").full_path
    gau_out_name=mtp_out_dir+"/"+basename+".out"
    chk_name=mtp_out_dir+"/"+basename+".chk"
    gau_inp_file=write_gaussian_inp(ctx,xyz,gau_inp_name,chk_name,cmd,charge,multiplicity,ncore).name

    ctx.log.info("submitting gaussian calculation:")
    ctx.create_gau_submission_script("run-gau.sh", gau_inp_file, gau_out_name, "mtp")
#####    gas_out_name="/home/wfit/FittingWizardWeb/fitting_service/data/mike-test-lj/gas.out" #######
    job_id = ctx.schedule_job(ctx.input_dir.full_path+"/mtp/run-gau.sh")

    ctx.wait_for_all_jobs()

    # gather results for vaporization enthalpy
#    parse_mtp_out(ctx,mtp_out_name)

#    ctx.log.info("Density read from file "+dens_out_name+": "+str(results["density"])+" g/cm^3\n")

    with ctx.output_dir.open_file("results.json","w") as json_file:
       json.dump(results,json_file)

#################################################################################################33

def _get_and_log_job_status(ctx, job_id, expected_status):
        job_status = ctx.job_status(job_id)
        ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))

#################################################################################################33



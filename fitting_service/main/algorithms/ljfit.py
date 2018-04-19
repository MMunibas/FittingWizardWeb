from .toolkit import *
from subprocess import call
from .charmm_input import *
import json

@register
def ljfit(ctx):
    results = {}

    ctx.log.info("parameters passed:")

    for parameter, value in ctx.parameters.items():
       ctx.log.debug("input parameter {} is set to {}".format(parameter, value))

    try:
       par = ctx.parameters["par"]
       top = ctx.parameters["top"]
       slu = ctx.parameters["slu"]
       slv = ctx.parameters["slv"]
       lpun = ctx.parameters["lpun"]
       pureliq = ctx.parameters["pureliq"]
       lmb0 = float(ctx.parameters["lmb0"])
       lmb1 = float(ctx.parameters["lmb1"])
       dlmb = float(ctx.parameters["dlmb"])
       T = float(ctx.parameters["T"])
       epsfac = float(ctx.parameters["epsfac"])
       sigfac = float(ctx.parameters["sigfac"])
    except ValueError:
       pass
    
    ctx.log.info("Input files:\n\t{}".format("\n\t".join(ctx.input_dir.list_files_recursively())))
    ctx.log.info("writing input for density calculation:")

    dens_inp_dir=ctx.input_dir.subdir("dens").full_path
    dens_inp_name = dens_inp_dir+"/density.inp"
    dens_out_dir=ctx.output_dir.subdir("dens").full_path
    dens_out_name = dens_out_dir+"/density.out"
    dens_inp_file=write_dens_inp(ctx,dens_inp_name,par,top,pureliq,lpun,T).name

    ctx.log.info("submitting density calculation:")
    ctx.create_charmm_submission_script("run.sh", dens_inp_file, dens_out_name, "dens")
    job_id = ctx.schedule_job(ctx.input_dir.full_path+"/dens/run.sh")
    ctx.wait_for_all_jobs()

    read_dens_out(ctx,slu,top,results,T)

    ctx.log.info("Density read from file "+dens_out_name+": ")
    ctx.log.info(str(results["density"])+" g/cm^3\n")

    with ctx.output_dir.open_file("results.json","w") as json_file:
       json.dump(results,json_file)

#################################################################################################33

def _get_and_log_job_status(ctx, job_id, expected_status):
        job_status = ctx.job_status(job_id)
        ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))

def read_dens_out(ctx,slu,top,results,T):

    density = 0.0
    delta_H_vap = 0.0
    mmass = 0.0

    res = ""
    resF = False
    masses = {}

    ctx.log.info("Calculating Molar Mass")
    with ctx.input_dir.open_file(slu,"r") as pdb:
       for line in pdb:
          words = line.split()
          if words[0].upper() == "ATOM":
             if res and words[3] != res:
                raise Exception("More than one residue type ("+res+" and "+words[3]+") in solute pdb "+slu)
             res=words[3]
       ctx.log.info("RES type is "+res)          
    pdb.close()
    ctx.log.info("Reading Atomic Masses from Topology file "+top)
    with ctx.input_dir.open_file(top,"r") as topo:
       for line in topo:
          words = line.split()
          if len(words) > 0:
             if words[0] == "MASS":
                masses[words[2]]=words[3]
             if words[0] == "RESI" and words[1] == res:
                resF = True
             elif words[0] == "RESI":
                resF = False
             if resF == True and words[0] == "ATOM":
                if words[2] in masses:
                   mmass += float(masses[words[2]])
                else:
                   raise Exception("Atom type "+words[2]+" not found in list of atomic masses")
    if mmass == 0.0 :
       raise Exception("Residue "+res+" not found in topology file "+top+" or has zero mass")
    ctx.log.info("Molecular mass for "+res+" is "+str(mmass))

    #######
    nres=150
    avg_vol=2480.75479
    
    #######

    density=mmass*nres/(0.6022*avg_vol)

    results["molar_mass"] = mmass
    results["density"] = density
    results["delta_H_vap"] = delta_H_vap
    



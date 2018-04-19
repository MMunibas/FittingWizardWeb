from .toolkit import *
from subprocess import call
import json

@register
def mike_test(ctx):
    dens_inp_name = "density.inp"
    dens_out_name = "density.out"
    results = {}

    #ctx.log.info("running in directory:")
    #ctx.schedule_job("pwd")
    #ctx.wait_for_all_jobs()
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

    densfile=write_dens_inp(ctx,dens_inp_name,par,top,pureliq,lpun,T).name
    dens_out_file=ctx.output_dir.name+'/'+dens_out_name

    ctx.log.info("submitting density calculation:")
    ctx.create_charmm_submission_script("run.sh", densfile, dens_out_file, "dens")
    job_id = ctx.schedule_job(ctx.input_dir.full_path+"/dens/run.sh")
    ctx.wait_for_all_jobs()

    read_dens_out(ctx,slu,top,results,T)

    ctx.log.info("Density read from file "+dens_out_file+": ")
    ctx.log.info(str(results["density"])+" g/cm^3\n")

    with ctx.output_dir.open_file("results.json","w") as json_file:
       json.dump(results,json_file)

#################################################################################################33

def _get_and_log_job_status(ctx, job_id, expected_status):
        job_status = ctx.job_status(job_id)
        ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))

#################################################################################################33

def write_dens_inp(ctx,dens_inp_name,par,top,pureliq,lpun,T):

    inpDir = ctx.input_dir.name

    charmmContent = """* CHARMM input file for pureliquid.pdb
* Pure Liquid simulation with MTPs
* generated on Thu Mar 29 12:37:41 CEST 2018
* by user wfit on machine Linux amd64 4.4.0-116-generic
*

bomlev 0
prnlev 2

set temp """

    charmmContent = charmmContent+str(T)+"\nset common "+inpDir+'\n'

    charmmContent = charmmContent+"""
! read parameters and coordinates
read rtf card name -\n @common/"""
    charmmContent = charmmContent+top+"\n"
    charmmContent = charmmContent+"read param card name -\n @common/"
    charmmContent = charmmContent+par+"\n"
    charmmContent = charmmContent+"""

OPEN UNIT 10 CARD READ NAME -\n"""

    charmmContent = charmmContent+pureliq+"\n"

    charmmContent = charmmContent+"""

READ SEQUENCE PDB UNIT 10
GENERATE SOLU
REWIND UNIT 10
READ COOR PDB UNIT 10
CLOSE UNIT 10

CRYSTAL DEFI CUBIC 28. 28. 28. 90. 90. 90.
CRYSTAL BUILD nope 0
image byres xcen 0.0 ycen 0.0 zcen 0.0 sele all end

! Non bonded parameters
NBONDS ATOM EWALD PMEWALD KAPPA 0.43  -
        FFTX 32 FFTY 32 FFTZ 32 ORDER 4 -
        CUTNB 14.0  CTOFNB 12.0 CTONNB 10.0 -
        LRC VDW VSWITCH -
        INBFRQ -1 IMGFRQ -1

SHAKE BONH PARA SELE ALL END

OPEN UNIT 40 CARD READ NAME -\n"""

    charmmContent = charmmContent+lpun+"\n"

    charmmContent = charmmContent+"""
MTPL MTPUNIT 40 ron2 10 roff2 12 ron3 9 roff3 11 -
        ron4 8 roff4 10 ron5 7 roff5 9
CLOSE UNIT 40

scalar mass stat
calc pmass = int ( ?stot  /  50.0 )
calc tmass = @pmass * 10

mini sd nstep 1000

mini abnr nstep 100

set tmin 50
dyna leap verlet start -                    ! use leap-frog verlet integrator
   timestep 0.001 nstep 40000 nprint 1000 - ! run 10K steps @ 1 fs time-steps
   firstt @tmin finalt @temp tbath @temp -      ! heat from @tmin K to @temp K (200 K)
   ihtfrq 1000 teminc 5 ieqfrq 0 -          ! heat the system 5K every 2500 steps
   iasors 1 iasvel 1 iscvel 0 ichecw 0 -    ! assign velocities via a Gaussian
   ntrfrq 500 -                             ! stop rotation and translation
   iseed  11033 -                           ! pick a random seed for the
   echeck 100.0                             ! If energy changes more than 100

dyna leap cpt nstep 40000 timestep 0.001 -
  nprint 100 nsavc 100 iuncrd 50 ntrfrq 200 -
  iprfrq 50000 inbfrq -1 imgfrq 50 ihtfrq 0 -
  ieqfrq 0 -
  pint pconst pref 1 pgamma 5 pmass @pmass -
  hoover reft @temp tmass @tmass firstt @temp

dyna leap nstep 40000 timestep 0.001 -
  nprint 100 nsavc 100 iuncrd 50 ntrfrq 200 -
  iprfrq 40000 inbfrq -1 imgfrq 50 ihtfrq 0 -
  ieqfrq 0 -
  cpt pint pconst pref 1 pgamma 0 pmass @pmass -
  hoover reft @temp tmass @tmass

STOP
"""

    with ctx.output_dir.open_file(dens_inp_name, "w") as densfile:
       densfile.write(charmmContent)
    densfile.close()

    return densfile

#################################################################################################33

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
    



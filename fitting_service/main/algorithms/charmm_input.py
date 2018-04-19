##########################################################################
# Density Input

def write_dens_inp(ctx,dens_inp_name,par,top,pureliq,lpun,T):

    charmmContent = """* CHARMM input file for pureliquid.pdb
* Pure Liquid simulation with MTPs
* generated on Thu Mar 29 12:37:41 CEST 2018
* by user wfit on machine Linux amd64 4.4.0-116-generic
*

bomlev 0
prnlev 2 node 0

set temp {T}

! read parameters and coordinates
read rtf card name "$CHMDIR/{top}"
read param card name "$CHMDIR/{par}"

OPEN UNIT 10 CARD READ NAME "$CHMDIR/{pureliq}"

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

OPEN UNIT 40 CARD READ NAME "$CHMDIR/{lpun}"
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
""".format(T=T, inp_dir=ctx.input_dir.name, top=top, par=par, pureliq=pureliq, lpun=lpun)

    with ctx.input_dir.open_file(dens_inp_name, "w") as dens_file:
       dens_file.write(charmmContent)
    dens_file.close()

    return dens_file

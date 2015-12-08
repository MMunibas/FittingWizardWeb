#!/bin/bash

# Generate CHARMM input files to run density calculations for various LJ
# parameters (MTP optional). Assumes crd and psf are called pureliquid.???. Read
# in par file, temperature of density calculation, and parameters to be varied.
# Read parameters from mc run.
#
# Tristan Bereau 1.10.2012

###############################################
# Directory of the lj-fit.pc-mtp package
# Resolves sym links.
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
    DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    SOURCE="$(readlink "$SOURCE")"
    # if    #$SOURCE was a relative symlink, we need to resolve it relative to
    #the path where the symlink file was located
    [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" 
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"/..
################################################

function die
{
    echo $1
    exit 1
}

[ -z $8 ] && echo "missing arguments: file.top file.par gas.pdb file.lpun TEMP M file.dat maxEnergy [processID]" && \
    echo "where" && \
    echo "  file.top: topology file" && \
    echo "  file.par: parameter file" && \
    echo "   gas.pdb: single-molecule pdb" && \
    echo " file.lpun: lpun file" && \
    echo "      TEMP: temperature [K]" && \
    echo "         M: molar mass of one molecule [g/mol]" && \
    echo "  file.dat: set of trial parameters from Monte Carlo simulation" && \
    echo " maxEnergy: maximum energhy of trial parameters" && \
    echo "-processID: analyze run with process ID processID." && \
    exit 1

[ ! -f $DIR/cfg_parser.sh ] && die "Can't find cfg_parser.sh"
source $DIR/cfg_parser.sh 
cfg_parser $DIR/config.ini
cfg.section.remliq

# Get number of cores from SGE
ncpu=$ncpu
# Define directories
#mkdir -p /scratch/$USER/$JOB_ID
tempdir=.
#tempdir=/scratch/$USER/$JOB_ID
#oridir=${SGE_O_WORKDIR}
oridir=.
# Directory name on remote cluster
dir_name=""

# array for ab initio fitting.
read_parameter=""
# temporary file for ab initio fitting.
fitf=eval.par.fit.tmp.dat
# variable for ab initio fitting.
rmse=0.0
# minimum RMSE
minrmse=1000000

# counter for simulation storage
counter=0

function check_file
{
    [ ! -f $1 ] && die "Missing file $1"  
}



check_file pureliquid.crd
check_file pureliquid.psf
check_file box.size

[ ! -f $DIR/fit.lj/fit.LJ.water.constr.py ] && \
    die "Error. Can't find script fit.LJ.water.constr.py in package dir."

# Script arguments
top=$1
par=$2
parNonbonded=-1
parNonbondedinit=$(cat $par | awk '/NONBONDED/{print NR}')
parNonbondedfinal=
readline=$parNonbondedinit
while [ $parNonbonded -eq -1 ]; do
    parNonbondedtmp=($(cat $par | awk -v readline=$readline 'NR==readline' | tr " " "\n"))
    lastEle=`echo "${#parNonbondedtmp[@]} - 1" | bc -l`
    if [ ${parNonbondedtmp[$lastEle]} != "-" ]; then
        parNonbondedfinal=$readline
        parNonbonded=0
    fi
    let readline+=1
done
gas=$3
lpun=$4
mtp=1
if [ $lpun == "pc" ]; then
    # Don't use MTP
    mtp=0
fi
tmppar=tmp.$par
temp=$5
kT=`echo "0.593*$temp/298." | bc -l`

mol=$6
echo "Temperature: $temp K"
echo "Molar mass: $mol g/mol"
N=`tail -2 pureliquid.pdb | head -1 | awk '{printf "%d\n",substr($0,23,4)}'`
echo "Number of molecules: $N"
Nat=`awk '{if ($0~"ATOM" || $0~"HETATM")print $0}' $gas | wc -l`
echo "Number of atoms per molecule: $Nat"
[ $Nat == "0" ] && \
    die "Can't read number of atoms in molecule. Exiting."
box=`cat box.size`
parms_out="parms.out.txt"
echo "" > $parms_out
npt_results=""
deltaH=0.0

# Force field trial parameters
trialfile=$7
# CHeck integrity of file (need "#Energy..." on line 5)
head -n 5 $trialfile | grep "Energy" >/dev/null 2>&1 || \
    die "Can't read \"Energy...\" in $trialfile"
head -n 5 $trialfile | awk '/Energy/' | grep "density" && \
    die "Error: $trialfile contains previous thermodynamic results"
parmstmp=$(echo `head -n 5 $trialfile | awk '/Energy/{print substr($0,8)}'` | tr " " "\n")
parms=
numparms=0
for atom in $parmstmp; do
    let numparms+=1
    parms=(${parms[@]} $atom)
done
echo "Optimizing $numparms parameter(s): ${parms[@]}"

maxEnergy=$8
echo "Maximum energy allowed: $maxEnergy"
# Analyze previous run
prevproc=''
[ ! -z $9 ] && prevproc=$9 && echo "Analyzing previous run $prevproc"
if [ "$prevproc" == "" ]; then
    proc=$$
else
    proc=$prevproc
fi


function new_par 
{
    # new_par $function $curEnrgy ${curParms[@]} 

    function=$1
    curEnrgy=$2
    curParms=$3
    
    if [ "$function" -eq "1" ]; then
        :> $tmppar
        cat $par | head -n +$parNonbondedfinal > $tmppar
        for (( i=0; i<${#parms[@]}; i++ )); do
            negEps=${curParms[2*$i]}
            [ `echo "$negEps > 0" | bc -l` -eq 1 ] && \
                negEps=`echo "-1.*${curParms[2*$i]}" | bc -l`
            printf "%s %7.4f %7.4f %7.4f\n" ${parms[$i]} 0.0 $negEps ${curParms[2*$i+1]} >> $tmppar
        done
        npt_prod ori
    elif [ "$function" -eq "2" ]; then
        dir=prod$proc.$counter
        cd $dir
            calc_density
            calc_vapor
            print_out_parms
        cd ..
        let "counter += 1"
    fi
} 



function npt_prod
{
    # $1 = "ori" or "new"
    #    "ori": rely on original NPT heating initial conditions.
    #    "new": rely on last NPT prod initial conditions.
    #           will give error message if either input files do not exist.

    input="pureliquid"

    rm -f pureliquid_npt2.crd

    dir=prod$proc.$counter
    
    mkdir -p $dir 
    cd $dir
    cp ../${input}.crd .
    cp ../pureliquid.psf .
    cp ../$par .
    cp ../$top .
    cp ../$lpun . >/dev/null 2>&1
    cp ../$gas .
    cp ../$tmppar .
    cat > pureliquid_npt2.inp <<EOF
* Prepare
*

bomlev 0
prnlev 2

!-- read parameters and coordinates
read rtf   card name -
  $oridir/$top
read param card name -
  $oridir/$tmppar

OPEN UNIT 10 CARD READ NAME -
  $oridir/pureliquid.psf
READ PSF CARD UNIT 10
CLOSE UNIT 10

OPEN UNIT 10 CARD READ NAME -
  $oridir/$input.crd
READ COOR CARD UNIT 10
CLOSE UNIT 10

CRYSTAL DEFI CUBIC $box $box $box 90. 90. 90.
CRYSTAL BUILD nope 0
image byres xcen 0.0 ycen 0.0 zcen 0.0 sele all end

NBONDS ATOM EWALD PMEWALD KAPPA 0.43  -
  FFTX 32 FFTY 32 FFTZ 32 ORDER 4 -
  CUTNB 14.0  CTOFNB 12.0 CTONNB 10.0 -
  LRC VDW VSWITCH -
  INBFRQ -1 IMGFRQ -1

SHAKE BONH PARA SELE ALL END

EOF

    if [ $mtp == "1" ]; then
	cat >> pureliquid_npt2.inp <<EOF
OPEN UNIT 40 CARD READ NAME $lpun
MTPL MTPUNIT 40 ron2 10 roff2 12 ron3 9 roff3 11 -
     ron4 8 roff4 10 ron5 7 roff5 9 
CLOSE UNIT 40
EOF
    fi

    cat >> pureliquid_npt2.inp <<EOF
scalar mass stat
calc pmass = int ( ?stot  /  50.0 )
calc tmass = @pmass * 10

mini sd 200 print 100

calc nstep  = 40000
calc tmin   = 0.0
calc dtemp  = $temp - @tmin
calc teminc = 10
calc ihtfrq = int(@nstep * @teminc / @dtemp)

dyna leap verlet start -                     ! use leap-frog verlet integrator
   timestep 0.001 nstep @nstep nprint 1000 - ! run 10K steps @ 1 fs time-steps
   firstt @tmin finalt $temp tbath $temp -   ! heat from @tmin K to $temp K (200 K)
   ihtfrq @ihtfrq teminc @teminc ieqfrq 0 -  ! heat the system @teminc K every ihtfrq steps
   iasors 1 iasvel 1 iscvel 0 ichecw 0 -     ! assign velocities via a Gaussian
   ntrfrq 500 -                              ! stop rotation and translation
   iseed  11033 -                            ! pick a random seed for the
   echeck 100.0                              ! If energy changes more than 100

dyna leap cpt nstep 40000 timestep 0.001 -
  nprint 100 nsavc 100 iuncrd 50 ntrfrq 200 -
  iprfrq 50000 inbfrq -1 imgfrq 50 ihtfrq 0 -
  ieqfrq 0 -
  pint pconst pref 1 pgamma 5 pmass @pmass -
  hoover reft $temp tmass @tmass firstt $temp


dyna leap nstep 40000 timestep 0.001 -
  nprint 100 nsavc 100 iuncrd 50 ntrfrq 200 -
  iprfrq 40000 inbfrq -1 imgfrq 50 ihtfrq 0 -
  ieqfrq 0 -
  cpt pint pconst pref 1 pgamma 0 pmass @pmass -
  hoover reft $temp tmass @tmass 

STOP

EOF

    rsync -az ../$dir $sshaddress:$workdir/$dir_name > /dev/null 2>&1
    # Try to download the output file
    scp $sshaddress:$workdir/$dir_name/$dir/pureliquid_npt2.$charmmout . >/dev/null 2>&1    

    if [ ! -f pureliquid_npt2.$charmmout ]; then
	   echo "Submitting NPT production simulation..."
	   ssh $sshaddress "cd $workdir/$dir_name/$dir; $charmmsub -N p$proc.$counter -p $ncpu pureliquid_npt2.inp"
    fi

    echo "Submitting gas-phase simulation..."
    cat > gas_phase.inp <<EOF
* Prepare
*

bomlev 0
prnlev 2

!-- read parameters and coordinates
read rtf   card name -
  $oridir/$top
read param card name -
  $oridir/$tmppar

OPEN UNIT 10 CARD READ NAME -
  $oridir/$gas
READ SEQUENCE PDB UNIT 10
GENERATE SOLU
REWIND UNIT 10
READ COOR PDB UNIT 10
CLOSE UNIT 10

NBONDS NBXMOD 5 ATOM CDIEL EPS 1.0 SHIFT VATOM VDISTANCE -
  VSWITCH CUTNB 99.0 CTOFNB 98.0 CTONNB 97. E14FAC 1.0

SHAKE BONH PARA SELE ALL END

EOF

    if [ $mtp == "1" ]; then
	cat >> gas_phase.inp <<EOF
OPEN UNIT 40 CARD READ NAME $lpun
MTPL MTPUNIT 40
CLOSE UNIT 40
EOF
    fi

    cat >> gas_phase.inp <<EOF
mini sd nstep 500 print 10

DYNA LEAP STRT NSTEP 20000 TIMESTEP 0.001 -
  NTRFRQ 100 -
  IPRFRQ 0 INBFRQ -1 IMGFRQ 250 -
  TBATH 0. RBUF 0. ILBFRQ 10 FIRSTT 0. -
  NPRINT 1000 NSAVC -1

STOP

EOF

    rsync -az ../$dir $sshaddress:$workdir/$dir_name > /dev/null 2>&1
    # Try to download the output file
    scp $sshaddress:$workdir/$dir_name/$dir/gas_phase.$charmmout . >/dev/null 2>&1

    if [ ! -f gas_phae.$charmmout ]; then
	   ssh $sshaddress "cd $workdir/$dir_name/$dir; $charmmsub -N g$proc.$counter -p 1 gas_phase.inp"
    fi

    cd ..
    let "counter += 1"
}


function calc_density
{
    [ ! -f pureliquid_npt2.$charmmout ] && die "Can't find pureliquid_npt2.$charmmout"
    L=0.0
    d=0.0
    if [ `tail -100 pureliquid_npt2.$charmmout | \
            grep " NORMAL TERMINATION BY " | wc -l` \
        -eq "0" ]; then
        echo "Error in CHARMM NPT run: gas_phase.inp"
    else
        L=`grep "AVER PRESS>" pureliquid_npt2.$charmmout | tail -1 | \
             awk '{print $7^(1/3.)}'`
        echo "Box length [A]:      $L"
        
        d=`echo "$mol * $N / (0.602 * $L^3)" | bc -l`
    fi
    printf "** Density  [g/cm^3]: %7.4f\n" $d

    npt_results=($L $d)
}

function calc_vapor
{
    [ ! -f gas_phase.$charmmout ] && die "Can't find gas_phase.$charmmout"
    deltaH=0.0
    if [ `tail -100 gas_phase.$charmmout | \
            grep " NORMAL TERMINATION BY " | wc -l` \
        -eq "0" ]; then
        echo "Error in CHARMM gas-phase run: gas_phase.inp"
    else
        cons=0
        grep "constraints will" gas_phase.$charmmout >/dev/null
        if [ $? -eq 0 ]; then
    	cons=`grep "constraints will" gas_phase.$charmmout | tail -1 | awk '{print $1}'`
        fi
        egas=`grep 'AVER>' gas_phase.$charmmout | awk '$1=="AVER>"' |tail -1 | awk '{print $6}'`
        egas=`echo "$egas + .5*$kT*(3*$Nat - 6 - $cons)" | bc -l`
        eliq=`grep 'AVER>' pureliquid_npt2.$charmmout | awk '$1=="AVER>"' | tail -1 | awk '{print $6}'`
        eliq=`echo "$eliq/$N" | bc -l`
        deltaH=`echo "$egas - $eliq + $kT" | bc -l`
    fi
    printf "** DeltaH [kcal/mol]: %7.4f\n" $deltaH

}

function print_out_parms_header
{
    echo -ne "# " >> $parms_out
    for parami in ${parms[@]}; do
        printf "%-15s " $parami >> $parms_out
    done
    printf "%11s %11s\n" "Heat Vapor" "density">> $parms_out
    echo -ne "# " >> $parms_out
    for parami in ${parms[@]}; do
        printf "%7s %7s " "eps" "Rmin" >> $parms_out
    done
    printf "%11s %11s\n" "[kcal/mol]" "[g/cm^3]">> $parms_out
}

function print_out_parms
{
    # Print eps and Rmin of all parameters
    echo -ne "  " >> ../$parms_out
    for parami in ${parms[@]}; do
        offset=`tail -n +$parNonbondedfinal $par | awk -v prm=$parami '{if (prm == $1) print NR}'`
        if [ "$offset" == "" ]; then
            die "can't find atom type $parami"
        fi
        cat $tmppar | awk -v linpar=$parNonbondedfinal -v prm=$parami \
            '{if (NR>linpar && prm == $1) printf "%7.4f %7.4f ",$3,$4}' >> ../$parms_out
    done
    printf "%11.4f %11.4f\n" $deltaH ${npt_results[1]} >> ../$parms_out
}


function vary_parms
{
    # Read in $trialfile. Either submit density
    # calculations, or analyze results.

    # Options:
    #     1 to submit density and heat vap. calculations
    #     2 to analyze results once calculations are finished.
    inputpar=$1

    echo "Reading in $trialfile file"
    trialfileLine=`awk 'END{print NR}' $trialfile`
    line=0
    while [[ $line -le $trialfileLine ]]; do
        curEnrgy=$(awk -v line=$line '{if (NR == line && substr($0,0,1) != "#")
            {print substr($0,0,8)}}' $trialfile)
        curParms=($(awk -v line=$line '{if (NR == line && substr($0,0,1) != "#")
            {print substr($0,8)}}' $trialfile | tr " " "\n"))
        if [ ${#curParms[@]} -gt 0 ] && [ `echo "$curEnrgy < $maxEnergy" | bc -l` -eq 1 ]; then
            echo $curEnrgy ${curParms[@]}
            new_par $inputpar $curEnrgy ${curParms[@]}
        fi
        let line++
     done

    [ $inputpar -eq 2 ] && print_out_parms
}


# Try to establish connection with remote cluster
# Test connection
sshaddress=${user}@${hostname}
ssh $sshaddress "hostname" >/dev/null
[ $? -ne 0 ] && die "Cannot connect to remote computer $hostname"
# Create directory for calculations
existing_dirs=`ssh $sshaddress "ls $workdir"`
dir_index=0
dir_name=`printf "dir%03d\n" $index_trial`
max_index=999
while [[ $existing_dirs =~ $dir_name ]]; do
    let dir_index+=1
    dir_name=`printf "dir%03d\n" $dir_index`
    [ $dir_index -ge $max_index ] && \
        die "Error. No free directory in remote cluster."
done
ssh $sshaddress "mkdir -p $workdir/$dir_name"
echo "Submitting simulations to $hostname:$workdir/$dir_name"

print_out_parms_header
counter=0
vary_parms 1
echo "All simulations have been submitted. Waiting."
while [ `ssh $sshaddress "qstat | grep $user | grep \"$proc\" | wc -l"` -gt "0" ]; do
    sleep 60
done
echo "All simulations are done. Starting analysis."
rsync -az --exclude 'core.*' $sshaddress:$workdir/$dir_name/* . > /dev/null 2>&1
counter=0
vary_parms 2

ssh $sshaddress "cd $workdir; rm -rf $dir_name"

cat parms.out.txt

rm -f $tmppar $fitf

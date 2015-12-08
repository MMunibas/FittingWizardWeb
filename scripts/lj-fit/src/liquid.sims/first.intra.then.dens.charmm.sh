#!/bin/bash

# Generate CHARMM input files to run density calculations for various LJ
# parameters (MTP optional). Assumes crd and psf are called pureliquid.???. Read
# in par file, temperature of density calculation, and parameters to be varied.
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


[ -z $9 ] && echo "missing arguments: file.top file.par gas.pdb file.lpun TEMP 'prms' M eps_num Rmin_num [processID]" && \
    echo "where" && \
    echo " -file.top: topology file" && \
    echo " -file.par: parameter file" && \
    echo "  -gas.pdb: single-molecule pdb" && \
    echo "-file.lpun: lpun file" && \
    echo "     -TEMP: temperature [K]" && \
    echo "   -'prms': one or two (use quotes) varying parameter(s)." && \
    echo "        -M: molar mass of one molecule [g/mol]" && \
    echo "  -eps_num: number of eps  incremental steps between density calculations"&& \
    echo " -Rmin_num: number of Rmin incremental steps between density calculations" && \
    echo "-processID: analyze run with process ID processID." && \
    exit 1

[ ! -f $DIR/cfg_parser.sh ] && echo "Can't find cfg_parser.sh" && exit 1
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
    [ ! -f $1 ] && echo "Missing file $1" && exit 1    
}

check_file E.comb.dat
check_file dimer.ljf
check_file pureliquid.crd
check_file pureliquid.psf
check_file box.size

[ ! -f $DIR/fit.lj/fit.LJ.water.constr.py ] && \
    echo "Error. Can't find script fit.LJ.water.constr.py in package dir." &&
    exit 1

# Script arguments
top=$1
par=$2
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
parmstmp=$6
parmsindex=1
# Are there multiple atom types?
more=0
echo $parmstmp | grep " " 1>/dev/null 2>&1 && more=1
if [ $more -eq 0 ]; then
    parms=$parmstmp
else
    while [ $more == 1 ]; do
        parmscurrent=(`echo $parmstmp | cut -f$parmsindex -d" "`)
        if [ "$parmscurrent" != "" ]; then
            parms=(${parms[@]} $parmscurrent)
        else
            more=0
        fi
        let parmsindex+=1
    done
fi
numparms=${#parms[@]}
echo "Optimizing $numparms parameter(s): ${parms[@]}"
[ "$numparms" -gt "2" ] && echo "Too many varying atom types" && exit 1
[ "$numparms" -eq "0" ] && echo "No parameter" && exit 1
mol=$7
echo "Temperature: $temp K"
echo "Molar mass: $mol g/mol"
N=`tail -2 pureliquid.pdb | head -1 | awk '{printf "%d\n",substr($0,23,4)}'`
echo "Number of molecules: $N"
Nat=`awk '{if ($0~"ATOM" || $0~"HETATM")print $0}' $gas | wc -l`
echo "Number of atoms per molecule: $Nat"
[ $Nat == "0" ] && \
    echo "Can't read number of atoms in molecule. Exiting." && \
    exit 1
echo ""
box=`cat box.size`
parms_out="parms.out.txt"
echo "" > $parms_out
npt_results=""
deltaH=0.0

# Force field variation parameters
Rmin_bound=0.5
Rmin_var=1.0
Rmin_step=`echo "scale=4;2.*$Rmin_var / $9" | bc -l`
eps_bound=-0.01
eps_var=0.1
eps_step=`echo "scale=4;2.*$eps_var / $8" | bc -l`
prevproc=''
[ ! -z ${10} ] && prevproc=${10} && echo "Analyzing previous run $prevproc"
if [ "$prevproc" == "" ]; then
    proc=$$
else
    proc=$prevproc
fi

function read_par
{
    # $1 is atom type to be extracted
    linpar=`grep NONBONDED $par -n | cut --delim=":" -f1`
    read_parameter=`tail -n +$linpar $par | awk -v prm=$1 '{if (prm == $1) print $3,$4}'`
    if [ "$read_parameter" == "" ]; then
        echo "can't find atom type $1"
        exit 1
    fi
    read_parameter=($read_parameter)
}

function run_fit
{
    $DIR/fit.lj/fit.LJ.water.constr.py \
        -ene E.comb.dat -ljf dimer.ljf -prm $fitf > opt.tmp
    rmse=`grep " RMSE" opt.tmp | awk '{print $2}'`
    rm -f opt.tmp
}

function add_other_coefs
{
    # Find other parameters by reading dimer.ljf header
    dimhead=(`head -n 1 dimer.ljf`)
    othercoefs=(${parms[@]} "HT" "OT")
    for (( i=1; i<${#dimhead[@]}; i++ )); do
        pair=${dimhead[$i]%_*}
        ele1=${pair%:*} 
        ele2=${pair#*:}
        ele1new=1
        ele2new=1
        for (( j=0; j<${#othercoefs[@]}; ++j )); do
            [ "${othercoefs[$j]}" == "$ele1" ] && ele1new=0
        done
        if [ "$ele1new" == "1" ]; then
            read_par $ele1
            echo $ele1 ${read_parameter[0]} ${read_parameter[1]} >> $fitf
            othercoefs=(${othercoefs[@]} $ele1)
        fi
        for (( j=0; j<${#othercoefs[@]}; ++j )); do
            [ "${othercoefs[$j]}" == "$ele2" ] && ele2new=0
        done
        if [ "$ele1new" == "2" ]; then
            read_par $ele2
            echo $ele2 ${read_parameter[0]} ${read_parameter[1]} >> $fitf
            othercoefs=(${othercoefs[@]} $ele2)
        fi
    done
}



function new_par 
{
    # $1 is atom type
    # $2 is new eps
    # $3 is new Rmin
    # $4 is 0 for ab initio RMSE; 
    #       1 to submit density calc; 
    #       2 to analyze density result
    # $5 optional atom type 2
    # $6 optional new eps2
    # $7 optional new Rmin2
    [ -z $4 ] && echo "missing arguments in subroutine new_par" && exit 1
    compute=""
    [ "$4" -eq "1" ] && compute="density"
    [ "$4" -eq "2" ] && compute="density"
    [ "$4" -eq "0" ] && compute="ab init"
    
    echo "Calculating $compute for atom type $1 (eps: $2; Rmin: $3)"
    if [ ! -z $7 ]; then
        echo "                    and atom type $5 (eps: $6; Rmin: $7)"
    fi

    echo "$1 $2 $3" > $fitf
    [ ! -z $7 ] && echo "$5 $6 $7" >> $fitf
    add_other_coefs
    run_fit
    prefac=1.4
    if [ "$4" -eq "0" ]; then
        echo $rmse $minrmse
        [ `echo "$rmse < $minrmse" | bc -l` == "1" ] && \
            minrmse=$rmse
    elif [ "$4" -eq "1" ]; then
        if [ `echo "$rmse < $prefac*$minrmse" | bc -l` == "1" ]; then
            linpar=`grep NONBONDED $par -n | cut --delim=":" -f1`
            let linpar-=1
            head -n -$linpar $par > $tmppar
            let linpar+=1
            
            offset=`tail -n +$linpar $par | awk -v prm=$1 '{if (prm == $1) print NR}'`
            if [ "$offset" == "" ]; then
                echo "can't find atom type $1"
                exit 1
            fi
            cat $par | awk -v linpar=$linpar -v offset=$offset \
                -v eps=$2 -v Rmin=$3 '{if (NR==linpar+offset-1) 
                   print $1,"      0.000000\t",eps,"\t",Rmin; 
                 else print $0}' > $tmppar 
            if [ ! -z $7 ]; then
            # Replace atom type 2
                offset=`tail -n +$linpar $par | awk -v prm=$5 '{if (prm == $1) print NR}'`
                if [ "$offset" == "" ]; then
                    echo "can't find atom type $5"
                    exit 1
                fi
                cat $tmppar | awk -v linpar=$linpar -v offset=$offset \
                    -v eps=$6 -v Rmin=$7 '{if (NR==linpar+offset-1) 
                   print $1,"      0.000000\t",eps,"\t",Rmin; 
                 else print $0}' > $tmppar.bak
                mv $tmppar.bak $tmppar
            fi

            # Run the simulation and calculate the density
            npt_prod ori
        fi
    elif [ "$4" -eq "2" ]; then
        if [ `echo "$rmse < $prefac*$minrmse" | bc -l` == "1" ]; then
	    dir=prod$proc.$counter
	    cd $dir
            calc_density
	        calc_vapor
            print_out_parms 	
	    cd ..	    
	    let "counter += 1"
	fi
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

    if [ ! -f gas_phase.$charmmout ]; then
	   ssh $sshaddress "cd $workdir/$dir_name/$dir; $charmmsub -N g$proc.$counter -p 1 gas_phase.inp"
    fi

    cd ..
    let "counter += 1"
}


function calc_density
{
    [ ! -f pureliquid_npt2.$charmmout ] && echo "Can't find pureliquid_npt2.$charmmout" && \
	   exit 1
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
    [ ! -f gas_phase.$charmmout ] && echo "Can't find gas_phase.$charmmout" && \
	   exit 1
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
        linpar=`grep NONBONDED $par -n | cut --delim=":" -f1`

        offset=`tail -n +$linpar $par | awk -v prm=$parami '{if (prm == $1) print NR}'`
        if [ "$offset" == "" ]; then
            echo "can't find atom type $parami"
            exit 1
        fi
        cat $tmppar | awk -v linpar=$linpar -v offset=$offset \
            -v eps=$2 -v Rmin=$3 '{if (NR==linpar+offset-1) 
                   printf "%7.4f %7.4f ",$3,$4}' >> ../$parms_out
        
        
    done
    printf "%11.4f %11.4f\n" $deltaH ${npt_results[1]} >> ../$parms_out
}


function vary_parms
{
    # Read default values {eps_0,Rmin_0} in original par file. 
    # Scan  eps: max( eps_0 - 0.1,0.01) <  eps_0 <  eps_0 + 0.1
    # WARNING: use negative values of eps.
    # Scan Rmin: max(Rmin_0 - 1.0,0.5 ) < Rmin_0 < Rmin_0 + 1.0
    
    # Options:
    # $1: 0 for rmse
    #     1 to submit density calculations
    #     2 to analyze results once calculations are finished.

    echo "Varying parameters for selected atom types"
    eps_def=""
    eps_max=""
    eps_min=""
    Rmin_def=""
    Rmin_max=""
    Rmin_min=""
    Rmin_i_min=""
    Rmin_i_max=""
    eps_i_min=""
    eps_i_max=""
    echo "parms ${parms[@]}"
    for parami in ${parms[@]}; do
        linpar=`grep NONBONDED $par -n | cut --delim=":" -f1`
        
        offset=`tail -n +$linpar $par | \
	    awk -v prm=$parami '{if (prm == $1) print NR}'`
        if [ "$offset" == "" ]; then
	       echo "can't find atom type $parami"
	       exit 1
        fi
        parm_tmp=(`cat $par | awk -v linpar=$linpar -v offset=$offset \
	    -v eps=$2 -v Rmin=$3 '{if (NR==linpar+offset-1) \
            printf "%7.4f %7.4f ",$3,$4}'`)
        eps_i_def=`echo $parm_tmp | tr -s ' ' | cut -f1 -d" "`
        Rmin_i_def=`echo $parm_tmp | tr -s ' ' | cut -f2 -d" "`
        eps_def=(${eps_def[@]} $eps_i_def)
        Rmin_def=(${Rmin_def[@]} $Rmin_i_def)
        eps_i_min=`echo "scale=4;$eps_i_def + $eps_var" | bc -l`
        eps_i_min=`echo "$eps_i_def $eps_i_min" | awk -v emin=$eps_bound \
	    '{if ($2 > emin) print emin; else print $2}'`
        eps_i_max=`echo "scale=4;$eps_i_def - $eps_var" | bc -l`
        Rmin_i_min=`echo "scale=4;$Rmin_i_def - $Rmin_var" | bc -l`
        Rmin_i_min=`echo "$Rmin_i_def $Rmin_i_min" | awk -v Rmin=$Rmin_bound \
	    '{if ($2 < Rmin) print Rmin; else print $2}'`
        Rmin_i_max=`echo "scale=4;$Rmin_i_def + $Rmin_var" | bc -l`
        eps_min=(${eps_min[@]} ${eps_i_min})
        eps_max=(${eps_max[@]} ${eps_i_max})
        Rmin_min=(${Rmin_min[@]} ${Rmin_i_min})
        Rmin_max=(${Rmin_max[@]} ${Rmin_i_max})
    done

    # Loop over atom type 1 - eps
    eps_i=${eps_min[0]}
    # Only run RMSE calculation. inputpar=0.
    inputpar=$1
    while [ `echo "scale=4;$eps_i - ${eps_max[0]} > 0" | bc -l` -eq "1" ]
    do
        # Loop over atom type 1 - Rmin
        Rmin_i=${Rmin_min[0]}
        while [ `echo "scale=4;$Rmin_i - ${Rmin_max[0]} < 0" | bc` -eq "1" ]
        do
            # Loop over atom type 2?
            if [ "$numparms" -eq "2" ]; then
                # Loop over atom type 2 - eps
                eps2_i=${eps_min[1]}
                while [ `echo "scale=4;$eps2_i - ${eps_max[1]} > 0" | bc -l` -eq "1" ]
                do
                    # Loop over atom type 2 - Rmin
                    Rmin2_i=${Rmin_min[1]}
                    while [ `echo "scale=4;$Rmin2_i - ${Rmin_max[1]} < 0" | bc -l` \
                        -eq "1" ]
                    do                        
                        new_par ${parms[0]} $eps_i $Rmin_i \
                            $inputpar ${parms[1]} $eps2_i $Rmin2_i
                        
                        Rmin2_i=`echo "scale=4;$Rmin2_i + $Rmin_step" | bc -l`
                        #[ `echo "${npt_results[0]} > 80." | bc -l` -eq "1" ] && input="ori"
                    done

                    echo "" >> $parms_out
                    eps2_i=`echo "scale=4;$eps2_i - $eps_step" | bc -l`
                done
            else
                new_par ${parms[0]} $eps_i $Rmin_i $inputpar
                #[ `echo "${npt_results[0]} > 80." | bc -l` -eq "1" ] && input="ori"
            fi
            Rmin_i=`echo "scale=4;$Rmin_i + $Rmin_step" | bc -l`
        done

        echo "" >> $parms_out
        eps_i=`echo "scale=4;$eps_i - $eps_step" | bc -l`
    done

    unset Rmin_min
    unset Rmin_max
    unset eps_min
    unset eps_max

    print_out_parms
}


# Try to establish connection with remote cluster
# Test connection
sshaddress=${user}@${hostname}
ssh $sshaddress "hostname" >/dev/null
[ $? -ne 0 ] && echo "Cannot connect to remote computer $hostname" && exit 1
# Create directory for calculations
existing_dirs=`ssh $sshaddress "ls $workdir"`
dir_index=0
dir_name=`printf "dir%03d\n" $index_trial`
max_index=999
while [[ $existing_dirs =~ $dir_name ]]; do
    let dir_index+=1
    dir_name=`printf "dir%03d\n" $dir_index`
    [ $dir_index -ge $max_index ] && \
        echo "Error. No free directory in remote cluster." && exit 1
done
ssh $sshaddress "mkdir -p $workdir/$dir_name"
echo "Submitting simulations to $hostname:$workdir/$dir_name"

vary_parms 0

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

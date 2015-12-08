#!/bin/bash

# Generate solvated simulation box out of one molecule with top, par and pdb
# files. Simulate in NAMD. Extract dimers of compound + water molecule, where
# the water is close to a given atom type.
#
# Tristan BEREAU 2012-2013

# Location of executables from config.ini
[ ! -f ${0%/*}/cfg_parser.sh ] && echo "Can't find cfg_parser.sh" && exit 1
source ${0%/*}/cfg_parser.sh 
cfg_parser ${0%/*}/config.ini
cfg.section.local 

for exe in $charmrun $namd; do
    exists=`command -v $exe | wc -l`
    [ $exists -eq 0 ] && \
        echo "Can't find $exe; check config.ini file" && exit 1
done


[ -z "$4" ] && echo "missing arguments: mol.top mol.pdb mol.par atom_type_sel [Ncor]" \
    && echo "  where atom_type_sel is the atom type(s) around which water
    molecules should sample." && \
        echo "  Note: include multiple atom types using double quotes" && \
        echo "  Note: the top and par files should include TIP3P " && exit 1 

top=$1
pdb=$2
par=$3
typ=$4
ncor=1
[ ! -z "$5" ] && ncor=$5
base=`basename $pdb .pdb`

typformatted=""
# Are there multiple atom types?
more=0
parmsindex=1
echo $typ | grep " " 1>/dev/null 2>&1 && more=1
if [ $more -eq 0 ]; then
    typformatted=$typ
else
    while [ $more == 1 ]; do
        typcurrent=(`echo $typ | cut -f$parmsindex -d" "`)
        if [ "$typcurrent" != "" ]; then
            typformatted=(${typformatted[@]} \\\"$typcurrent\\\")
        else
            more=0
        fi
        let parmsindex+=1
    done
fi

# Check that the frames directory doesn't already exist
mkdir frames
if [ $? -ne 0 ]; then
    echo "Error: frames directory already exists"
    exit 1
    echo "after exit"
fi

cat > vmd.tmp.tcl <<EOF
mol load pdb $pdb

autopsf -mol 0 -top $top
EOF

vmd < vmd.tmp.tcl

[ ! -f ${base}_autopsf.psf ] && echo "VMD did not complete. Exiting" && exit 1

mv ${base}_autopsf.psf ${base}.psf

cat > vmd.tmp2.tcl <<EOF
solvate ${base}.psf $pdb -o ${base}.slv -t 10
EOF

vmd < vmd.tmp2.tcl

[ ! -f ${base}.slv.psf ] && \
    echo "VMD did not complete. Can't create solvated box. Exiting" && \
    exit 1

# Check whether the solute has nonzero coordinates, otherwise exit
line1=`grep ATOM ${base}.slv.pdb | awk '{if (NR==1) print substr($0,34,21)}'`
line2=`grep ATOM ${base}.slv.pdb | awk '{if (NR==2) print substr($0,34,21)}'`
[ "$line1" == "$line2" ] && echo "Error in ${base}.slv.pdb. Check coordinates." && exit 1


rm -f vmd.tmp.tcl vmd.tmp2.tcl pureliquid_autopsf.pdb pureliquid_autopsf.log

cat > $base.slv.namd <<EOF
# NAMD Config file

# input
coordinates             $base.slv.pdb
structure               $base.slv.psf
parameters              $par
paratypecharmm          on

# output
set output              $base.slv
outputname              \$output
dcdfile                 \${output}.dcd
xstFile                 \${output}.xst
dcdfreq                 50
xstFreq                 50

binaryoutput            no
binaryrestart           no
outputEnergies          100
restartfreq             1000

fixedAtoms              off

# Basic dynamics
exclude                 scaled1-4
1-4scaling              1
COMmotion               no
dielectric              1.0

# Simulation space partitioning
switching               on
switchdist              9
cutoff                  10
pairlistdist            12

# Multiple timestepping
firsttimestep           0
timestep                1
stepspercycle           20
nonbondedFreq           2
fullElectFrequency      4

# Temperature control

set temperature         298
temperature             \$temperature;  # initial temperature

# Langevin Dynamics
langevin                on;            # do langevin dynamics
langevinDamping         1;              # damping coefficient (gamma) of 1/ps
langevinTemp            \$temperature;   # bath temperature
langevinHydrogen        no;             # don't couple langevin bath to hydrogens
seed                    12345

# Pressure control
langevinPiston          on
langevinPistonTarget    1.01325; # in bar -> 1.01325 bar = 1 atm
langevinPistonPeriod    200
langevinPistonDecay     100
langevinPistonTemp      \$temperature
useFlexibleCell         no
useGroupPressure        no
fixedAtomsForces        off

# PBC
cellBasisVector1        25.0 0.0 0.0
cellBasisVector2        0.0 25.0 0.0
cellBasisVector3        0.0 0.0 25.0
cellOrigin              0.0 0.0 0.0
wrapAll                 on
dcdUnitCell             yes

PME                     yes
PMEGridSizeX            25
PMEGridSizeY            25
PMEGridSizeZ            25


# Scripting

minimize            1000
reinitvels          \$temperature
run                 100000

EOF

if [ -f ${base}.slv.dcd ]; then
    echo "Found ${base}.slv.dcd file. Skipping simulation."
else
    echo "$charmrun +p$ncor $namd $base.slv.namd"
    $charmrun +p$ncor \
        $namd $base.slv.namd > $base.slv.log
fi

if [ "$?" -ne "0" ]; then
    grep ERROR $base.slv.log
    echo "Error in NAMD NPT run: ${base}.slv.namd"
    echo "Check ${base}.slv.log"
    exit 1
fi
rm -f $base.slv.restart.* charmrun.* $base.slv.*.BAK


cat > vmd.tmp3.tcl <<EOF
mol load psf ${base}.slv.psf dcd ${base}.slv.dcd

set mol [atomselect top "not water"]
set nmol [\$mol num]
set moll [\$mol list]
puts "nmol \$nmol"
set nf  [molinfo top get numframes]
puts "nf \$nf"

set index_m 0
set index_pdb 0

for {set i 20} {\$i < \$nf} {incr i 2} {
    \$mol frame \$i
    set nei [atomselect top "water and same residue as within 4.5 of type ${typformatted[@]}" frame \$i]
    \$nei frame \$i
    set index_w [expr {int(rand()*[\$nei num])}]
    set out [atomselect top "same residue as index [lindex [\$nei list] \
        \$index_w] [lindex \$moll \$index_m]" frame \$i]
    \$out frame \$i
    puts "[\$out list]"

    if {\$index_pdb < 10} {
        \$out writepdb frames/frame.000\$index_pdb.pdb
    } elseif {\$index_pdb < 100} {
        \$out writepdb frames/frame.00\$index_pdb.pdb
    } elseif {\$index_pdb < 1000} {
        \$out writepdb frames/frame.0\$index_pdb.pdb
    } else {
        \$out writepdb frames/frame.\$index_pdb.pdb
    }
    
    if { \$index_m == \$nmol-1 } {
        set index_m 0
    } else {
        set index_m [expr \$index_m + 1]
    }
    incr index_pdb
}
EOF

vmd < vmd.tmp3.tcl

[ `ls -l frames/*.pdb | wc -l` -lt 50 ] && \
    echo "Error in extracting conformations" && exit 1

for i in frames/*.pdb; do
    cat $i | awk '{if (substr($0,23,4) != "   1" && 
    ($1~"ATOM" || $1~"HETATM")) {gsub(substr($0,23,4),"   2")} 
    print num""$0}' > $i.tmp
    mv $i.tmp $i
done


rm -f vmd.tmp3.tcl

#!/bin/bash

# Generate pure liquid simulation box out of one molecule with top and pdb
# files, number of molecules and box size.  Generates both NAMD PSF and
# CHARMM-compatible PSF and CRD.
#
# Tristan BEREAU 2013

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


[ -z $5 ] && echo "missing arguments: mol.top mol.pdb mol.par Nmols L_size" && exit 1 

top=$1
pdb=$2
par=$3
Nat=$4
box=$5

exists=`command -v packmol | wc -l`
[ $exists -eq 0 ] && \
    echo "Can't find packmol; Please download from:" &&
    echo "http://www.ime.unicamp.br/~martinez/packmol/" && 
    exit 1

[ ! -f $DIR/cfg_parser.sh ] && echo "Can't find cfg_parser.sh" && exit 1
source $DIR/cfg_parser.sh 
cfg_parser $DIR/config.ini
cfg.section.local

cat > gen.liq.tmp.inp <<EOF
#
# mol liquid box
#

# All the atoms from diferent molecules will be separated at least 2.0
# Anstroms at the solution.

tolerance 2.8

# The file type of input and output files is PDB

filetype pdb

# The name of the output file

output pureliquid.pdb

# 1000 water molecules and 400 urea molecules will be put in a box
# defined by the minimum coordinates x, y and z = 0. 0. 0. and maximum
# coordinates 20. 20. 20. That is, they will be put in a cube of side
# 20. (the keyword "inside cube 0. 0. 0. 20.") could be used as well.

structure $pdb 
  number $Nat 
  inside box 0. 0. 0. $box $box $box 
end structure

EOF

packmol < gen.liq.tmp.inp

cat > vmd.tmp.tcl <<EOF
mol load pdb pureliquid.pdb

autopsf -mol 0 -top $top
EOF

vmd < vmd.tmp.tcl

[ ! -f pureliquid_autopsf.psf ] && echo "VMD did not complete. Exiting" && exit 1

mv pureliquid_autopsf.psf pureliquid.namd.psf

echo $box > box.size
echo "Box size stored in file: box.size"

cat > prep.inp <<EOF
* Prepare solute 
*

bomlev 0
prnlev 2

!-- read parameters and coordinates
read rtf   card name $top
read param card name $par

open unit 10 read card name pureliquid.pdb
read sequence pdb unit 10
generate LIG $Nat
rewind unit 10 
read coor pdb unit 10
close unit 10

COOR STAT
CALC BOXX = $box
CRYSTAL DEFI CUBIC @BOXX @BOXX @BOXX 90. 90. 90.
CRYSTAL BUILD nope 0
image byres xcen 0.0 ycen 0.0 zcen 0.0 sele all end

mini sd nstep 100

DYNA LEAP STRT NSTEP 1000 TIMESTEP 0.001 -
  NTRFRQ 100 -
  IPRFRQ 0 INBFRQ -1 IMGFRQ 250 -
  TCONST TCOUPL 1.0 TREFE 298.0 -
  NPRINT 100 NSAVC -1 -

DYNA LEAP STRT NSTEP 5000 TIMESTEP 0.001 -
  NTRFRQ 100 -
  IPRFRQ 0 INBFRQ -1 IMGFRQ 50 -
  IHTFRQ 0 IEQFRQ 0 TSTRUCT 298.0 FINALT 298.0 -
  FIRSTT 298.0 -
  CPT PCONst PREF 1.0 PGAMMA 20.0 PMASs 500 HOOVER -
  REFT 298.9 TMASS 4000.0 -
  NPRINT 100 NSAVC -1

OPEN UNIT 10 CARD WRITE NAME pureliquid.psf
WRITE PSF  CARD UNIT 10
OPEN UNIT 10 CARD WRITe NAME pureliquid.crd
WRITe COOR CARD UNIT 10

STOP
EOF

./charmm < prep.inp > prep.out
[ $? -ne 0 ] && exit 1



rm -f gen.liq.tmp.inp vmd.tmp.tcl pureliquid_autopsf.pdb pureliquid_autopsf.log


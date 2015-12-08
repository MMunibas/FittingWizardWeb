#!/bin/bash

# Directory of the lj-fit.pc-mtp package (subdirectory fit.lj)
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

cleanup()
{
    rm -f *.txt *di.??? *di.cout *f1.??? *f1.cout *f2.??? *f2.cout
}

[ $# -eq 1 ] && [ "$1" == "clean" ] && \
    echo "Cleaning up spurious files" && \
    cleanup && exit 1

[ -z $3 ] && \
    echo "missing arguments: file_type_MP2_calcs file.rtf file.par" && \
    echo "                   [-charmmff] [-aty file.aty] [-mtp file.lpun]" && \
    exit
mp2=$1
rtf=$2
par=$3
flag_ff=''
flag_aty=''
flag_mtp=''
for (( c=4; c<=$#; c++ )); do
    eval arg=\$$c
    d=`echo "$c + 1" | bc -l`
    eval argn=\$$d
    [ $arg == "-charmmff" ] && flag_ff="-charmmff" && \
        echo "CHARMM force field will be included"
    [ $arg == "-aty" ] && flag_aty="-aty $argn" && \
        echo "atom type pairs loaded from $argn"
    [ $arg == "-mtp" ] && flag_mtp="-mtp $argn" && \
        echo "multipole file $argn included"
done



$DIR/0.extract.MP2.energies.sh $mp2
$DIR/1.prep.charmm.inp.sh $rtf $par $flag_ff $flag_mtp
$DIR/2.run.charmm.sh || exit 1
$DIR/3.extract.MD.energies.sh
$DIR/4.combine.E.MP2.MD.sh || exit 1
$DIR/5.extract.LJdist.sh $rtf $flag_ff $flag_aty

# Cleanup
cleanup

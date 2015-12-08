#!/bin/bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

[ -z $2 ] && echo "missing arguments: file.rtf file.par [-charmmff] " \
    "[-mtp file.dma]" && exit
rtf=$1
par=$2
flag_ff=0
flag_mtp="-no_mtp"
for (( c=3; c<=$#; c++ )); do
    eval arg=\$$c
    d=`echo "$c + 1" | bc -l`
    eval argn=\$$d
    [ $arg == "-charmmff" ] && flag_ff=1
    [ $arg == "-mtp" ] && flag_mtp="-lpun_mono1 $argn -lpun_di $argn"
done

echo "Generating CHARMM input files..."

# First remove all *.inp and *.com files
for i in *.inp *.com; do
    rm -f $i
done

if [ "$flag_ff" -eq "1" ]; then
    for i in *.pdb; do
        python $DIR/charmm_diintE_inpmaker.py -pdb $i -rtf \
            $DIR/top_all27_prot_na.rtf $rtf -par \
            $DIR/par_all27_prot_na.prm $par $flag_mtp
    done        
else
    for i in *.pdb; do
        python $DIR/charmm_diintE_inpmaker.py -pdb $i -rtf $rtf -par $par $flag_mtp
    done
fi



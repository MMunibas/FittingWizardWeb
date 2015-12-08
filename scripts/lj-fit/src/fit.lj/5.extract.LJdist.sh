#!/bin/bash

echo "Extracting LJ distances..."

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"


[ -z $1 ] && echo "missing arguments: file.rtf [-charmmff] [-aty file.aty]" && exit
rtf=$1
flag_ff=0
[ ! -z $2 ] && [ "$2" == "-charmmff" ] && flag_ff=1
[ ! -z $3 ] && [ "$3" == "-charmmff" ] && flag_ff=1
[ ! -z $4 ] && [ "$4" == "-charmmff" ] && flag_ff=1
flag_aty=''
[ ! -z $2 ] && [ "$2" == "-aty" ] && flag_aty="-aty $3"
[ ! -z $3 ] && [ "$3" == "-aty" ] && flag_aty="-aty $4"

# exclude monomer pdbs
pdbs=`find . -name "*di.pdb" -print | sort`

if [ "$flag_ff" -eq "1" ]; then
    for i in $pdbs; do
        $DIR/LJ_Tab_gen.py -rtf $DIR/top_all27_prot_na.rtf \
            $rtf -pdb ${i#*/} $flag_aty
    done
else
    for i in $pdbs; do
        $DIR/LJ_Tab_gen.py -rtf $rtf -pdb ${i#*/} $flag_aty
    done
fi

header=`find . -name "*di.ljf" -print | head -1`
head -n1 $header > dimer.ljf
ljfs=`find . -name "*di.ljf" -print | sort`
for i in $ljfs; do 
    tail -n1 $i >> dimer.ljf
done


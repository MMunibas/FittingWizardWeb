#!/bin/bash
#
# Print LJ parameters that reproduce experimental liquid properties. Script
# only works with Monte-Carlo-based parametrization.
#
# Tristan Bereau (2013)


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



function check_file
{
    [ ! -f $1 ] && echo "Missing file $1" && exit 1    
}

check_file density.exp
check_file vapor.exp

[ -z $1 ] && echo "Missing argument: parms.out.txt [NUM]" && \
    echo "   NUM: acceptable interval for experimental values (default: 0.05 for 5%)" && \
    exit 1

par=$1
range=0.05
[ ! -z $2 ] && range=$2

if [ `echo "$range < 0.0" | bc -l` -eq 1 ]; then
    echo "Range needs to be positive."
    exit 1
elif [ `echo "$range > 1.0" | bc -l` -eq 1 ]; then
    echo "Range needs to be smaller than 1."
    exit 1
fi

rho=`cat density.exp`
vap=`cat vapor.exp`
rhomin=`echo "$rho * (1-$range)" | bc -l`
rhomax=`echo "$rho * (1+$range)" | bc -l`
vapmin=`echo "$vap * (1-$range)" | bc -l`
vapmax=`echo "$vap * (1+$range)" | bc -l`

echo "Looking for parameter sets with:"
printf "    %7.4f < rho  < %7.4f\n" $rhomin $rhomax
printf "    %7.4f < delH < %7.4f\n" $vapmin $vapmax
# Read $par once to get number of columns
fielddensity=`tail $par | awk '{print NF; exit}'`
fieldvapor=`echo "$fielddensity - 1" | bc -l`
cat $par | awk -v rhomin=$rhomin -v rhomax=$rhomax \
    -v vapmin=$vapmin -v vapmax=$vapmax \
    -v colnum=$fielddensity -v colvap=$fieldvapor \
    '!/#/{if ($colnum > rhomin && $colnum < rhomax && \
              $colvap > vapmin && $colvap < vapmax) print $0}'

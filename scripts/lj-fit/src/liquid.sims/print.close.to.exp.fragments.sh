#!/bin/bash
#
# Print LJ parameters that reproduce experimental liquid properties. Script
# only works with fragment-based parametrization (up to 2 atom types at once).
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
check_file parms.out.txt


[ ! -f $DIR/fit.lj/fit.LJ.water.constr.py ] && \
    echo "Error. Can't find script fit.LJ.water.constr.py in package dir." &&
    exit 1

[ -z $3 ] && echo "Missing argument: file.par E.comb.dat dimer.ljf [NUM]" && \
    echo "   NUM: acceptable interval for experimental values (default: 0.05 for 5%)" && \
    exit 1

par=$1
ene=$2
dim=$3
range=0.05
read_parameter=""
fitf=eval.par.fit.tmp.dat
rmse=0.0

[ ! -z $4 ] && range=$4

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
        -ene $ene -ljf $dim -prm $fitf > opt.tmp
    rmse=`grep " RMSE" opt.tmp | awk '{print $2}'`
    rm -f opt.tmp
}

function add_other_coefs
{
    # Find other parameters by reading dimer.ljf header
    dimhead=(`head -n 1 $dim`)
    othercoefs=(${aty[@]} "HT" "OT")
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

rho=`cat density.exp`
vap=`cat vapor.exp`
rhomin=`echo "$rho * (1-$range)" | bc -l`
rhomax=`echo "$rho * (1+$range)" | bc -l`
vapmin=`echo "$vap * (1-$range)" | bc -l`
vapmax=`echo "$vap * (1+$range)" | bc -l`

cat parms.out.txt | awk '/#/{if (NR<3)print $0}' 
aty=(`cat parms.out.txt | awk \
    '/#/{if ($3 == "Heat") print $2; else print $2,$3}' \
    | head -1`)
echo -ne "" > $fitf
for (( i=0; i<${#aty[@]}; i++ )); do
    read_par ${aty[i]}
    echo ${aty[i]} ${read_parameter[0]} ${read_parameter[1]} >> $fitf
done

colnum=""
if [ ${#aty[@]} == "1" ];then
    colnum=4
elif [ ${#aty[@]} == "2" ]; then
    colnum=6
else
    echo "Unsupported number of parameters (>2)."
    exit 1
fi
colvap=`echo "$colnum - 1" | bc -l`

echo "Looking for parameter sets with:"
printf "    %7.4f < rho  < %7.4f\n" $rhomin $rhomax
printf "    %7.4f < delH < %7.4f\n" $vapmin $vapmax
cat parms.out.txt | awk -v rhomin=$rhomin -v rhomax=$rhomax \
    -v vapmin=$vapmin -v vapmax=$vapmax \
    -v colnum=$colnum -v colvap=$colvap \
    '!/#/{if ($colnum > rhomin && $colnum < rhomax && \
              $colvap > vapmin && $colvap < vapmax) print $0}' > good.parms.tmp

bestline=""
bestrmse=1000.0
while read line; do
    line=($line)    
    echo ${aty[0]} ${line[0]} ${line[1]} > $fitf
    [ ${#aty[@]} == "2" ] && echo ${aty[1]} ${line[2]} ${line[3]} >> $fitf
    add_other_coefs
    run_fit
    if [ `echo "$rmse < $bestrmse" | bc -l` == "1" ]; then
        bestline=${line[@]}
        bestrmse=$rmse
    fi
done < good.parms.tmp

echo ${bestline[@]}
echo "RMSE: $bestrmse kcal/mol"

echo "Within 35% of best RMSE:"
besterrexp=1000.0
bestdenexp=1000.0
bestvapexp=1000.0
bestrmsexp=1000.0
bestlineexp=""
while read line; do
    line=($line)    
    echo ${aty[0]} ${line[0]} ${line[1]} > $fitf
    [ ${#aty[@]} == "2" ] && echo ${aty[1]} ${line[2]} ${line[3]} >> $fitf
    add_other_coefs
    run_fit
    if [ `echo "$rmse / $bestrmse < 1.35" | bc -l` == "1" ]; then
        [ ${#aty[@]} == "1" ] && \
	    rhoerrexp=`echo "scale=4;($rho - ${line[3]})/$rho" | bc -l` && \
	    vaperrexp=`echo "scale=4;($vap - ${line[2]})/$vap" | bc -l`
        [ ${#aty[@]} == "2" ] && \
	    rhoerrexp=`echo "scale=4;($rho - ${line[5]})/$rho" | bc -l` && \
	    vaperrexp=`echo "scale=4;($vap - ${line[4]})/$vap" | bc -l`
        errrms=`echo "scale=4;($bestrmse - $rmse)/$bestrmse" | bc -l`
	errexp=`echo "sqrt($rhoerrexp^2 + $vaperrexp^2)" | bc -l`
        if [ `echo "$errexp < $besterrexp" | bc -l` == "1" ]; then
            besterrexp=$errexp
            bestrmsexp=$rmse
            [ ${#aty[@]} == "1" ] && \
		bestdenexp=${line[3]} && \
		bestvapexp=${line[2]}
            [ ${#aty[@]} == "2" ] && \
		bestdenexp=${line[5]} && \
		bestvapexp=${line[4]}
            bestlineexp=${line[@]}
        fi
        echo ${line[@]}
        printf "RMSE: %7.4f kcal/mol; Error on RMSE: %7.4f; " $rmse $errrms 
        printf "Relative error on experimental data: %7.4f\n" $errexp
        echo ""
    fi
done < good.parms.tmp

echo "Best parameters:"
echo ${bestlineexp[@]}
printf "          RMSE: %7.4f kcal/mol\n" $bestrmsexp
printf "       Density: %7.4f g/mol\n" $bestdenexp
printf "Heat of vapor.: %7.4f kcal/mol\n" $bestvapexp

#rm -f $fitf good.parms.tmp

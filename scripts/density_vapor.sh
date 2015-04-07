#!/bin/sh


# 
# function calc_density
# {
# 
# }
# 
# function calc_vapor
# {
# 
# 
# }
# 
# calc_density
# calc_vapor

gas_name=$1
liq_name=$2

[ ! -f $liq_name ] && echo "Can't find $liq_name" exit 1
L=`grep "AVER PRESS>" $liq_name | tail -1 | awk '{print $7^(1/3.)}'`
echo "Box length [A]:      $L"

d=`echo "94.12 * 150 / (0.602 * $L^3)" | bc -l`
printf "** Density  [g/cm^3]: %7.4f\n" $d

echo "density"  $d

cons=`grep "constraints will" $gas_name | tail -1 | awk '{print $1}'`
egas=`grep 'AVER>' $gas_name | awk '$1=="AVER>"' |tail -1 | awk '{print $6}'`
egas=`echo "$egas + 0.5*0.59*(3*13 -6-$cons)" | bc -l`
eliq=`grep 'AVER>' $liq_name | awk '$1=="AVER>"' | tail -1 | awk '{print $6}'`
eliq=`echo "$eliq/150" | bc -l`
deltaH=`echo "$egas - $eliq + 0.59" | bc -l`
printf "** DeltaH [kcal/mol]: %7.4f\n" $deltaH


#!/bin/bash

[ -z $1 ] && echo "Usage: $0 output.log" && exit 1

log=$1

echo "# lambda1 lambda2 dGi       sum(dG)"
grep -B 1 succeeded $log | awk '/d/' | \
    sed 's/[()]/ /g' | awk \
    '{if (NR%2==1)printf "%f %f",$3,$5; 
     else {printf " %+f\n",$2}}' | sort | awk \
    'BEGIN{s=0}!/#/{s+=$3; 
     printf "%f %f %+f %+f\n",$1,$2,$3,s}'
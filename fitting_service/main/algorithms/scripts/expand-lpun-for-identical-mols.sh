#!/bin/bash

[ -z $3 ] && echo "Usage: $0 file.lpun Num_molecules extended.lpun" && exit 1

pun=$1
num=$2
opun=$3

cat $pun > output.tmp
awk '!/#/' $pun > rpt.tmp

# Read out number of atoms in molecules by going through file once. 
numatoms=`cat rpt.tmp | awk 'BEGIN{id=1}{if ($0~"Rank")
id+=1} END{print id-1}'`

for i in `seq 2 $num`; do
  cat rpt.tmp >> output.tmp
done


cat output.tmp | awk -v numatoms=$numatoms '
BEGIN{id=1;mult=0}{if ($0~"Rank")
{print id,$2,$3,$4,$5,$6,$7; id+=1} else {
    if ($0~"LRA:") {
        mult = int((id-2)/numatoms);
        ret = $1;
        n1=$3;
        n2=$4;
        n3=$5;
        n4=$6;
        if ($3>0) {n1+=mult*numatoms}
        if ($4>0) {n2+=mult*numatoms}
        if ($5>0) {n3+=mult*numatoms}
        if ($6>0) {n4+=mult*numatoms}
        print $1,$2,n1,n2,n3,n4
    }
    else print $0
}}' > $3

rm output.tmp rpt.tmp


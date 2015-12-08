#!/bin/bash

[ -z $1 ] && print \
    "missing argument: extension of gaussian output files (e.g., log, out)" \
    && exit
out=$1

echo "Extracting MP2 energies..."

rm -f Dimer_MP2_E.txt Frag1_MP2_E.txt Frag2_MP2_E.txt
for i in *.$out; do 
    [ `grep -H "EUMP2" $i | wc -l` -gt "2" ] && grep -H "EUMP2" $i \
        | head -1 >> Dimer_MP2_E.txt
    [ `grep -H "RMN12SX" $i | wc -l` -gt "2" ] && grep -H "RMN12SX" $i \
        | head -1 | awk '{print $1,".",$2,$3,$4,$5,$6}' >> Dimer_MP2_E.txt
    [ `grep -H "EUMP2" $i | wc -l` -gt "2" ] && grep -H "EUMP2" $i \
        | head -n2 | tail -n1 >> Frag1_MP2_E.txt
    [ `grep -H "RMN12SX" $i | wc -l` -gt "2" ] && grep -H "RMN12SX" $i \
        | head -n2 | tail -n1 | awk '{print $1,".",$2,$3,$4,$5,$6}' >> Frag1_MP2_E.txt
    [ `grep -H "EUMP2" $i | wc -l` -gt "2" ] && grep -H "EUMP2" $i \
        | head -n3 | tail -n1 >> Frag2_MP2_E.txt
    [ `grep -H "RMN12SX" $i | wc -l` -gt "2" ] && grep -H "RMN12SX" $i \
        | head -n3 | tail -n1 | awk '{print $1,".",$2,$3,$4,$5,$6}' >> Frag2_MP2_E.txt
done


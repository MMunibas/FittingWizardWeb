#!/bin/bash

echo "Collecting CHARMM energies..."

for i in *di.cout; do
    dipc=`grep -H "ENER EXTERN>" $i | sed 's/ \{1,\}/ /g' | \
        cut -f1,4 --delim=' '`
    if [ "$dipc" == "" ]; then echo "frame 10000.0"; else echo $dipc; fi >> Dimer_PC_E.txt
    dilj=`grep -H "ENER EXTERN>" $i | sed 's/ \{1,\}/ /g' | \
        cut -f1,3 --delim=' '`
    if [ "$dilj" == "" ]; then echo "frame 10000.0"; else echo $dilj; fi >> Dimer_LJ_E.txt
done

for i in *f1.cout; do
    f1pc=`grep -H "ENER EXTERN>" $i | sed 's/ \{1,\}/ /g' | \
        cut -f1,4 --delim=' '`
    if [ "$f1pc" == "" ]; then echo "frame 0.0"; else echo $f1pc; fi >> Frag1_PC_E.txt
    f1lj=`grep -H "ENER EXTERN>" $i | sed 's/ \{1,\}/ /g' | \
        cut -f1,3 --delim=' '`
    if [ "$f1lj" == "" ]; then echo "frame 0.0"; else echo $f1lj; fi >> Frag1_LJ_E.txt
done

for i in *f2.cout; do
    f2pc=`grep -H "ENER EXTERN>" $i | sed 's/ \{1,\}/ /g' | \
        cut -f1,4 --delim=' '`
    if [ "$f2pc" == "" ]; then echo "frame 0.0"; else echo $f2pc; fi >> Frag2_PC_E.txt
    f2lj=`grep -H "ENER EXTERN>" $i | sed 's/ \{1,\}/ /g' | \
        cut -f1,3 --delim=' '`
    if [ "$f2lj" == "" ]; then echo "frame 0.0"; else echo $f2lj; fi >> Frag2_LJ_E.txt
done


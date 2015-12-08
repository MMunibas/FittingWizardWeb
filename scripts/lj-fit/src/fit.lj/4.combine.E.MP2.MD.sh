#!/bin/bash

echo "Combining all energies into file E.comb.dat..."

# Check that all files are present
for i in Dimer_MP2_E.txt Dimer_PC_E.txt Dimer_LJ_E.txt \
    Frag1_MP2_E.txt Frag1_PC_E.txt Frag1_LJ_E.txt \
    Frag2_MP2_E.txt Frag2_PC_E.txt Frag2_LJ_E.txt; do
    [ ! -e $i ] && echo "missing $i" && exit
done

# Combine MP2 energies
pr -m -t -s\  Dimer_MP2_E.txt Frag1_MP2_E.txt Frag2_MP2_E.txt | \
    sed 's/D/E/g' | awk '{print $1,($7-$14-$21)*627.503}' \
    > MP2.E.tmp.txt 

# Combine PC energies
pr -m -t -s\  Dimer_PC_E.txt Frag1_PC_E.txt Frag2_PC_E.txt | \
    awk '{print $1, $2-$4-$6}' > PC.E.tmp.txt

# Combine LJ energies
pr -m -t -s\  Dimer_LJ_E.txt Frag1_LJ_E.txt Frag2_LJ_E.txt | \
    awk '{print $1, $2-$4-$6}' > LJ.E.tmp.txt

while read line; do
    file=`echo $line | cut -f1 --delim=' '`
    file=${file%.*}
    grep1=`grep $file PC.E.tmp.txt`
    if [[ $? -eq 0 ]]; then	
	grep2=`grep $file LJ.E.tmp.txt`
	if [[ $? -eq 0 ]]; then
	    echo -e $file "\t " `echo $line | cut -f2 --delim=' '` \
		"\t " `echo $grep1 | cut -f2 --delim=' '` \
		"\t " `echo $grep2 | cut -f2 --delim=' '`
	fi
    fi
done < MP2.E.tmp.txt > E.comb.dat

[ `cat E.comb.dat | wc -l` -eq 0 ] && \
    echo "Error: E.comb.dat empty. Please check individual *E.tmp.txt files." && \
    exit 1

rm MP2.E.tmp.txt PC.E.tmp.txt LJ.E.tmp.txt

exit 0
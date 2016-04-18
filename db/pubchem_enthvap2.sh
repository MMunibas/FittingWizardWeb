#!/bin/bash

out=$(mysql -u fittingWizard --password=z6nNfrAhyxb2cLA8 --batch --silent -e " use fittingWizard ; select REPLACE(name,' ','') from import; ")

for i in $out
do

  curl "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/$i/property/IUPACName,MolecularFormula,CanonicalSMILES,InChI,MolecularWeight/CSV" >> evap2pubchem.csv

done

# out2$(echo $out | tr '\n' ' ')

# curl "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/$out2/property/IUPACName,MolecularFormula,CanonicalSMILES,InChI,MolecularWeight/CSV"

# out2=$(echo $out | sed 's/ /,/g')
# # echo $out2
# 
# curl "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/$out2/property/MolecularFormula,InChI/csv" > struct.csv
# 
# curl "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/$out2/property/MolecularWeight/csv" > mass.csv



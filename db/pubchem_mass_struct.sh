#!/bin/bash

out=$(mysql -u fittingWizard --password=z6nNfrAhyxb2cLA8 --batch --silent -e 'use fittingWizard;select idPubchem from compounds order by id;')

# for i in $out
# do
#     echo "hello $i"
# done
    
out2=$(echo $out | sed 's/ /,/g')
# echo $out2

curl "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/$out2/property/MolecularFormula,InChI/csv" > struct.csv

curl "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/$out2/property/MolecularWeight/csv" > mass.csv



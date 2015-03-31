#!/bin/bash

out=$(mysql -u fittingWizard --password=z6nNfrAhyxb2cLA8 --batch --silent -e 'use fittingWizard; select idPubchem from compounds;')

# mkdir -p json

touch results.csv
echo '"pubchemID","density","HVap"' > results.csv
    
for i in $out
do
#     curl "http://pubchem.ncbi.nlm.nih.gov/rest/pug_view/data/compound/$i/JSON" > json/$i.json
    
    den=$(jq -f jqDensityCMD "json/$i.json")
    dh=$(jq -f jqHvapCMD "json/$i.json")
#     
    echo "$i,$den,$dh"
    echo "$i,$den,$dh" >> results.csv
done

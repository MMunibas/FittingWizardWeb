#!/bin/bash

export JAVA_BIN_DIR=/usr/java/latest/
export PYTHONPATH=/usr/local/lib64/python2.7/site-packages/
#export PYTHONPATH=/home/hedin/src/rdkit/
export LD_LIBRARY_PATH=/usr/local/lib/

#java -cp "build/classes:lib/*" ch.unibas.fittingwizard.Main
java -jar dist/FittingWizard.jar




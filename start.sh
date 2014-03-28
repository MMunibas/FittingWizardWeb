#!/bin/bash

export JAVA_BIN_DIR=/usr/java/latest/
export PYTHONPATH=/usr/local/lib64/python2.7/site-packages/
export LD_LIBRARY_PATH=/usr/local/lib/

java -cp "bin:lib/*:$JAVA_BIN_DIR/jre/lib/jfxrt.jar" ch.unibas.fittingwizard.Main

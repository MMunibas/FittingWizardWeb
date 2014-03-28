#!/bin/bash

cd `dirname $0`

JAVA_BIN_DIR=`which java`

java -cp "*:$JAVA_BIN_DIR/../lib" com.javafx.main.Main

cd -
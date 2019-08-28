#!/bin/bash

/sbin/lsof -i :8081 | grep java > /dev/null && exit
echo "starting fitting web script 2.0.1 for MDCM"
export JAVA_HOME=/usr/java/jdk-10.0.2/
cd /home/wfit/fitting-web-2.0.1-mdcm/bin
./fitting-web >& log


#!/bin/bash

cd /home/wfit/FittingWizardWeb-mdcm/fitting
./gradlew fitting-web:build
tar xvf fitting-web/build/distributions/fitting-web-2.0.1.tar
cp ref-bin-pc-beethoven/* fitting-web-2.0.1/bin/
rm -r ~/fitting-web-2.0.1-mdcm/
mv fitting-web-2.0.1/ ~/fitting-web-2.0.1-mdcm/

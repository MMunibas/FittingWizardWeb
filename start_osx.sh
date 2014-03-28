#!/bin/sh

java_path=`/usr/libexec/java_home`

java -cp "*:$java_path/jre/lib/*" ch.scs.unibas.fittingwizard.Main
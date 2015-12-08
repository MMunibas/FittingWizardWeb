#!/bin/bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"/..

echo "Running CHARMM energy calculations..."

[ ! -f $DIR/cfg_parser.sh ] && echo "Can't find cfg_parser.sh" && exit 1
source $DIR/cfg_parser.sh 
cfg_parser $DIR/config.ini
cfg.section.local

for j in $(for i in *.inp ; do basename $i '.inp'; done); do
    $charmm < $j'.inp' > $j'.cout' 
    tail -n 50 $j.cout | grep "ABNORMAL TERMINATION" >/dev/null 2>&1 && \
        echo "CHARMM did not complete successfully. Please check $j.cout" && \
        exit 1
done

exit 0

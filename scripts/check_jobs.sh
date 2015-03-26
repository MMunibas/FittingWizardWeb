#!/bin/bash

user=hedin
stop=0
myid=$1

while true;
do 
    out=$(ssh verdi "qstat -u $user"| grep $myid | awk '{print $1}')
    arr=($out)
    size=${#arr[@]}
    if [ "$size" -eq "$stop" ]; then
        break
    fi
    echo "$size jobs still running ; sleeping 10 seconds and then looping ..."
    sleep 10
done

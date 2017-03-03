#!/bin/bash

docker run -it --rm --env=DISPLAY --volume=$HOME/.Xauthority:/home/fw/.Xauthority --net=host fhedin/ubuntu-fw:latest


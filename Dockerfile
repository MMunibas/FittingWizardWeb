# Pull base image.
FROM ubuntu:16.04

RUN apt-get update 

# better to have everything in one command later for reducing size, but faster to build
#  with separate commands at beginning
RUN apt-get install -y python-rdkit
RUN apt-get install -y python-scipy
RUN apt-get install -y openjdk-8-jre
RUN apt-get install -y python-scipy
RUN apt-get install -y openjfx
RUN apt-get install -y xterm
RUN apt-get install -y vim
RUN apt-get install -y openssh-client
RUN apt-get install -y python-openbabel

# RUN apt-get clean
# RUN apt-get purge

# add specific user
RUN adduser --disabled-password --gecos "" fw

# Define working directory.
WORKDIR /home/fw

RUN mkdir -p data/testdata/molecules
COPY data/testdata/molecules data/testdata/molecules 

RUN mkdir dist
COPY dist dist

RUN mkdir scripts
COPY scripts scripts

RUN mkdir db
COPY db db

COPY start_docker.sh start.sh
COPY config_gui.ini config_gui.ini

COPY README.md README.md
COPY LICENSE.txt LICENSE.txt

RUN chown -R fw:fw *

WORKDIR /home/fw
ENV HOME /home/fw
USER fw

# Define default command.
# ENTRYPOINT ["bash","start.sh"]

# ENTRYPOINT ["bash"]

ENTRYPOINT ["xterm","bash"]


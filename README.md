----------------------------------------------
# FittingWizard
----------------------------------------------
A Java-based Web application with separate Python API that assists the user in performing forcefield parameter optimization by using Quantum Chemistry and Molecular Dynamics software.

https://github.com/MMunibas/FittingWizardWeb

Mike Devereux (Michael.Devereux@unibas.ch) , Florent Hédin (florent.hedin@unibas.ch) ,
Markus Meuwly (m.meuwly@unibas.ch)

----------------------------------------------
## LICENSING:
----------------------------------------------
Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
All rights reserved.
The 3-clause BSD license is applied to this software.
see LICENSE.txt

----------------------------------------------
# Installation
----------------------------------------------
The software consists of two components, a web-based UI written in Java and a calculation API written in Python. The source code for both components is packaged together, but they are installed separately (often on entirely separate machines) and communication between the two components needs to be configured accordingly.

## Python API:
The Python API runs as a service on the server or cluster where the heavy computation will be done. Its role is in job-management, specifically passing user files and input data from the web UI to the scientific Python scripts in the "algorithms" folder, then acting as an interface between these scripts and the server/cluster environment to schedule and track jobs. It detects when jobs are finished and returns control to the scripts, then once the scripts are finished it changes the calculation's status accordingly so that the web UI can fetch results.

At any given time the API can return a list of active calculations and their status, available methods and active calculations can be viewed using the URL http://server:port, where "server" is the server on which the API is running and "port" is the port it is configured to run on.

### Installing the Python API:
#### Dependencies:
The API itself requires Python with the following Python modules, which can be installed by first installing Python, then running "pip3 install -r requirements.txt" from inside the fitting_service folder:
- Python 3.6.5
- xmltodict
- aniso8601
- certifi
- chardet
- click
- Flask
- flask-restplus
- idna
- itsdangerous
- Jinja2
- jsonschema
- MarkupSafe
- pytz
- requests
- six
- urllib3
- Werkzeug

Recommended:
- Anaconda (to easily manage the Python environment)
- A separate "wfit" user without login to run the code

#### Configuring

To configure Anaconda after installing, use "conda" to add modules, for rdkit use e.g. "conda create -c rdkit -n FittingWizard rdkit". To subsequently load the environment use "source activate FittingWizard" and then add remaining modules using pip3 for this environment.

The scientific scripts require the compute nodes (where jobs submitted via the cluster's job scheduler will actually run) to have installed:
- Gaussian (with formchk, cubegen)
- CHARMM
- GDMA
- Babel

In addition the scientific scripts require the server / master node where the API will run to have:
- rdkit (easiest to install via Anaconda)
- numpy
- scipy

Once the dependencies are installed, the next step is to configure the API for the cluster / server environment. To date two queuing systems (Grid Scheduler and Slurm) have been configured with corresponding scripts written to fitting_service/main/algorithms/cluster/. You can use these scripts as templates for your own environment, focusing in particular on correctly setting the paths in the first few lines.

If you choose to copy one of the scripts to e.g. "my_cluster.py" rather than modifying an existing script, then you'll also need to set a corresponding "job_management_type" in fitting_service/main/fitting_service/settings.py. Here you should also configure the relevant commands for submitting and querying job status via your cluster's job scheduler, as well as defining the folder to write job output files and the port to be used by the API to accept communication from the web UI (your server's firewall needs to be configured accordingly).

You should then add your new job scheduler script (if you chose to write one) to fitting_service/main/fitting_service/job.py by importing the file and adding the "job_management_type" that you introduced to settings.py to the JobsService "\__init__(self)" section.

Next you should check the job templates in fitting_service/main/algorithms/cluster/. Again you can either modify an existing template or copy a file and create your own. After doing so you should check the ljfit.py, mtp-part1.py and mtp-part2.py to point those scripts to the correct job scheduler script in the corresponding "import" sections.

With configuration complete, you should now be able to start the API by launching fitting.py from fitting_service/main. It is recommended to use a cron job to automatically start / restart the API with contents such as:

~~~~
#!/bin/bash

ps -u wfit | grep python3 > /dev/null && exit  
cd /home/wfit/anaconda3/bin/  
source activate my-rdkit-env  
cd /home/wfit/FittingWizardWeb/fitting_service/main  
currdate=`date -Iseconds`  
[ -e log ] && mv log "log-$currdate"  
python3 fitting.py >& log  
~~~~

If installation was successful then you should now be able to reach the API in a web browser using "http://server:port", where "server" is the machine hosting the API and "port" is the port number you defined in the file "settings.py"

## Web interface
The web UI can run on a completely separate host to the Python API, so long as it can reach the API at the port that was specified for the API when it was configured.

Unlike the API, the UI is Java-based and when launched it starts a web server that hosts the pages used to request files and information or display results. In the background the UI also stores files and information about active sessions so that a user's session can be continued if their web browser session is interrupted.

### Installing the web UI:
#### Dependencies
- Oracle Java 10.0.1 (not openJDK from package manager!)

It is recommended that you also create a new local user as owner of the UI directory and processes

#### Compiling
The web UI must first be compiled / packaged before it can be deployed. To do this:
- change to the "fitting" folder
- run ./gradlew fitting-web:build
- check code has been built to fitting-web/build/distributions (should contain a recently created .tar and .zip file)
- extract one of these archives and move it to the desired install location
- modify fitting-web/bin/config.ini as necessary (in particular the port number that the web UI should use, and the server and port that should be used to contact the API, as entered during configuration of the API)
- launch the UI from the bin/ folder using:
  export JAVA_HOME=<your_java_install_path>
  ./fitting-web >& log

Again, it is recommended that you create a cron job to monitor and automatically restart the UI in case of a crash. Along the lines of:

~~~~
#!/bin/bash  

export JAVA_HOME=/usr/lib/jvm/jdk-10.0.1/  
cd /usr/local/fitting-web-2.0.1/bin  
ps -u wfit | grep java > /dev/null || echo "starting fitting web script 2.0.1"  
ps -u wfit | grep java > /dev/null || ./fitting-web >& log  
~~~~

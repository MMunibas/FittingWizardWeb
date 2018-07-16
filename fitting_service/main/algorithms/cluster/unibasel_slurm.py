import sys

script_path = "/data/wfit/FittingWizardWeb/fitting_service/main/algorithms/scripts/"

charmm_executable = "/data/wfit/charmm/charmm-intel2018"
mpi_executable = "/usr/mpi/intel/openmpi-1.10.4-hfi/bin/mpirun"
mpi_flags = "--bind-to none"
scratch_dir_name = "scratch"
ld_path = "/usr/mpi/intel/openmpi-1.10.4-hfi/lib64:/opt/intel/l_comp_lib_2018.3.222_comp.for_redist/compilers_and_libraries_2018.3.222/linux/compiler/lib/intel64_lin/"
env_path = "/usr/mpi/intel/openmpi-1.10.4-hfi/bin:/opt/cluster/programs/charmm/c42b2/exec/gnu_M/intel/charmm:/opt/TurboVNC/bin:/opt/cluster/admin:/usr/local/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/opt/areca/sbin/:/opt/c3-4:/home/devereux/.local/bin:/home/devereux/bin"
number_of_cpu_cores = 8

gau_login_script = "/opt/cluster/programs/g09_d.01/g09/bsd/g09.login.bash"
gau_formchk = "/opt/cluster/programs/g09_d.01/g09/formchk"
gdma = "/data/wfit/bin/gdma"
cubegen = "/opt/cluster/programs/g09_d.01/g09/cubegen"
babel = "/usr/bin/babel"

fieldcomp = script_path+"/fieldcomp"

def generate_gau_setup_script(input_file, output_file, working_directory, number_of_cpu_cores, job_name,
                              gau_login_script, scratch_dir_name, gau_formchk, chk_file, fchk_file, gdma, gdma_inp_name,
                              gdma_out_name, grid_spec, cubegen, cube_file, fieldcomp, gdma_pun_file, vdw_file_name,
                              babel, xyz_file_name, sdf_file_name):
    return """#!/bin/bash

######################################################

#SBATCH --job-name={job_name}
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task={cpu_cores}
#SBATCH -p short
#SBATCH -o {data_folder}/slurm-$JOBID.out

######################################################
# lib for Gaussian
######################################################
source {login_script}

######################################################
# Creation of the working directory
######################################################

cd {workdir}
mkdir -p /{scratch_dir_name}/$USER.$SLURM_JOB_ID
export GAUSS_SCRDIR=/{scratch_dir_name}/$USER.$SLURM_JOB_ID

######################################################
#  Run
######################################################

echo "Starting Gaussian"
$GAUSSIAN_EXE {input_file_name} {output_file_name}

######################################################
#  Create Formatted Checkpoint File
######################################################

echo "Starting formchk"
{formchk} {chk_file} {fchk_file}

######################################################
#  Run GDMA Calculation
######################################################
echo "Starting GDMA"
{gdma} < {gdma_inp_name} > {gdma_out_name}

######################################################
#  Run Cubegen for ESP grid
######################################################
echo "Starting CubeGen"
echo -e {grid_spec} | {cubegen} 0 potential {fchk_file} {cube_file} -1

######################################################
#  Run FieldComp ESP fitting code
######################################################
echo "Starting fieldcomp"
{fieldcomp} -cube {cube_file} -vdw {vdw_file} -pun {pun_file} > fieldcomp.log

######################################################
#  Convert xyz file to sdf for connectivity
######################################################
echo "Starting Babel"
{babel} -ixyz {xyz_file} -osdf > {sdf_file}

""".format(input_file_name=input_file, output_file_name=output_file, data_folder=working_directory,
           cpu_cores=number_of_cpu_cores, job_name=job_name, login_script=gau_login_script,
           scratch_dir_name=scratch_dir_name, formchk=gau_formchk, chk_file=chk_file, fchk_file=fchk_file,
           workdir=working_directory, gdma=gdma, gdma_inp_name=gdma_inp_name, gdma_out_name=gdma_out_name,
           grid_spec=grid_spec, cubegen=cubegen, cube_file=cube_file, fieldcomp=fieldcomp, pun_file=gdma_pun_file,
           vdw_file=vdw_file_name, babel=babel, xyz_file=xyz_file_name, sdf_file=sdf_file_name)


def generate_charmm_setup_script(input_file, output_file, working_directory, charmm_executable, number_of_cpu_cores,
                                 job_name, ld_path, env_path, mpi_executable, mpi_flags, scratch_dir_name,
                                 input_dir_name):

    import os
    return """#!/bin/bash

######################################################

#SBATCH --job-name={job_name}
#SBATCH --nodes=1
#SBATCH --ntasks={cpu_cores}
#SBATCH --cpus-per-task=1
#SBATCH -p short
#SBATCH -o {data_folder}/slurm-$JOBID.out

######################################################
# lib for CHARMM 
######################################################
export LD_LIBRARY_PATH={ld_path}:$LD_LIBRARY_PATH
export PATH={env_path}:$PATH

######################################################
# Creation of the working directory
######################################################

mkdir -p /{scratch_dir_name}/$USER.$SLURM_JOB_ID

cp {input_file_name} /{scratch_dir_name}/$USER.$SLURM_JOB_ID/.

cd /{scratch_dir_name}/$USER.$SLURM_JOB_ID

# make data folder
mkdir -p {data_folder}

######################################################
#  Run
######################################################

export CHMDIR={input_dir}

my_charmm_para="{charmm_executable}"
{mpi_executable} {mpi_flags} -np {cpu_cores} $my_charmm_para < {input_file_name_nopath} > {output_file_name}

######################################################
# Copy of the output data to the data directory

cp /{scratch_dir_name}/$USER.$SLURM_JOB_ID/* {data_folder}

# removing the working directory
rm -fr /{scratch_dir_name}/$USER.$SLURM_JOB_ID
""".format(input_file_name=input_file, output_file_name=output_file, data_folder=working_directory,
           charmm_executable=charmm_executable, cpu_cores=number_of_cpu_cores, job_name=job_name, ld_path=ld_path,
           env_path=env_path, mpi_executable=mpi_executable, mpi_flags=mpi_flags, scratch_dir_name=scratch_dir_name,
           input_dir=input_dir_name,input_file_name_nopath=os.path.basename(input_file))


if __name__ == "__main__":
    type = sys.argv[1]
    if type == "gau":
        print(generate_gau_setup_script("input file", "output file", "workdir", "numcores", "name", "script_path",
                                        "scratchdir"))
    elif type == "charmm":
        print(generate_charmm_setup_script("input file", "output file", "workdir", "charmm", "numcores", "name",
                                           "ldpath", "env_path", "mpi", "scratchdir"))




def generate_charmm_twostep_setup_script(input_file1, output_file1, input_file2, output_file2,
                                 working_directory, charmm_executable,
                                 number_of_cpu_cores, job_name, ld_path, env_path, mpi_executable,
                                 mpi_flags, scratch_dir_name, input_dir_name):

    import os
    return """#!/bin/bash
######################################################

#SBATCH --job-name={job_name}
#SBATCH --nodes=1
#SBATCH --ntasks={cpu_cores}
#SBATCH --cpus-per-task=1
#SBATCH -p short
#SBATCH -o {data_folder}/slurm-$JOBID.out

######################################################
# lib for CHARMM 
######################################################
export LD_LIBRARY_PATH={ld_path}:$LD_LIBRARY_PATH
export PATH={env_path}:$PATH

######################################################
# Creation of the working directory
######################################################

mkdir -p /{scratch_dir_name}/$USER.$SLURM_JOB_ID

cp {input_file_name1} {input_file_name2} /{scratch_dir_name}/$USER.$SLURM_JOB_ID/.

cd /{scratch_dir_name}/$USER.$SLURM_JOB_ID

# make data folder
mkdir -p {data_folder}

######################################################
#  Run
######################################################

export CHMDIR={input_dir}

my_charmm_para="{charmm_executable}"
{mpi_executable} {mpi_flags} -np {cpu_cores} $my_charmm_para < {input_file_name1_nopath} > {output_file_name1}

{mpi_executable} {mpi_flags} -np {cpu_cores} $my_charmm_para < {input_file_name2_nopath} > {output_file_name2}

######################################################
# Copy of the output data to the data directory

cp /{scratch_dir_name}/$USER.$SLURM_JOB_ID/* {data_folder}

# removing the working directory
rm -fr /{scratch_dir_name}/$USER.$SLURM_JOB_ID
""".format(input_file_name1=input_file1, input_file_name2=input_file2,
           output_file_name1=output_file1, output_file_name2=output_file2,
           data_folder=working_directory, charmm_executable=charmm_executable,
           cpu_cores=number_of_cpu_cores, job_name=job_name, ld_path=ld_path,
           env_path=env_path, mpi_executable=mpi_executable, mpi_flags=mpi_flags,
           scratch_dir_name=scratch_dir_name, input_dir=input_dir_name,
           input_file_name1_nopath=os.path.basename(input_file1),
           input_file_name2_nopath=os.path.basename(input_file2))


if __name__ == "__main__":
    type = sys.argv[1]
    if type == "gau":
        print(generate_gau_setup_script("input file", "output file", "workdir", "numcores", "name", "script_path",
                                        "scratchdir"))
    elif type == "charmm":
        print(generate_charmm_setup_script("input file", "output file", "workdir", "charmm", "numcores", "name",
                                           "ldpath", "env_path", "mpi", "scratchdir"))

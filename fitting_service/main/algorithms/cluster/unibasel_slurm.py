import sys

# These are cluster-specific, to be defined by sys admin:

script_path = "/home/wfit/FittingWizardWeb-mdcm/fitting_service/main/algorithms/scripts/"

charmm_executable = "/usr/local/bin/charmm"
mpi_executable = "/opt/intel/openmpi-3.1.1/bin/mpirun"
mpi_flags = "--bind-to none"
scratch_dir_name = "scratch"
ld_path = "/opt/intel/mkl/include:/opt/intel/mkl/lib/intel64:/opt/intel/openmpi-3.1.1/lib:/opt/intel/openmpi-3.1.1/include:/opt/intel/compilers_and_libraries_2018.3.222/linux/compiler/lib/intel64_lin"
env_path = "/opt/intel/openmpi-3.1.1/bin:/opt/intel/mkl/bin:/opt/cluster/programs/charmm/c43b1/build/cmake:/opt/c3-4"
number_of_cpu_cores = 8

gau_login_script = "/opt/cluster/programs/g09/g09_d.01/g09/bsd/g09.login.bash"
gau_formchk = "/opt/cluster/programs/g09/g09_d.01/g09/formchk"
gdma = "/home/wfit/bin/gdma-2.2.04/bin/gdma"
cubegen = "/opt/cluster/programs/g09/g09_d.01/g09/cubegen"
babel = "/bin/babel"

cubefit_exe = script_path+"/pcubefit.x" # code to fit MDCM charge models using differential evolution 

# These are bundled with the source, can leave as they are:

fieldcomp = script_path+"/fieldcomp"

def generate_gau_setup_script(input_file, output_file, working_directory, number_of_cpu_cores, job_name,
                              gau_login_script, scratch_dir_name, gau_formchk, chk_file, fchk_file, 
                              grid_spec, cubegen, pot_cube_file, dens_cube_file):
    return """#!/bin/bash

######################################################

#SBATCH --job-name={job_name}
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task={cpu_cores}
#SBATCH -p ib
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
#  Run Cubegen for ESP grid
######################################################
echo "Starting CubeGen"
echo -e {grid_spec} | {cubegen} 0 potential {fchk_file} {pot_cube_file} -1

######################################################
#  Run Cubegen for Density grid
######################################################
echo "Starting CubeGen"
echo -e {grid_spec} | {cubegen} 0 density {fchk_file} {dens_cube_file} -1  

""".format(input_file_name=input_file, output_file_name=output_file, data_folder=working_directory,
           cpu_cores=number_of_cpu_cores, job_name=job_name, login_script=gau_login_script,
           scratch_dir_name=scratch_dir_name, formchk=gau_formchk, chk_file=chk_file, fchk_file=fchk_file,
           workdir=working_directory,grid_spec=grid_spec, cubegen=cubegen, pot_cube_file=pot_cube_file,
           dens_cube_file=dens_cube_file)

def generate_atom_chg_fit_script(multipole_file, pot_cube_file, dens_cube_file, chgs_per_atom, job_name,
            cpu_cores, working_directory, cubefit_exe):
    import os
    return """#!/bin/bash

######################################################

#SBATCH --job-name={job_name}
#SBATCH --nodes=1
#SBATCH --ntasks={cpu_cores}
#SBATCH --cpus-per-task=1
#SBATCH -p ib
#SBATCH -o {workdir}/slurm-$JOBID.out

######################################################
cd {workdir}
mkdir slices
{cubefit} -greedy {mtpl_file} -esp {pot_cube} -dens {dens_cube} -nacmin {nchgs} -nacmax {nchgs} -ntry 5 -onlymultipoles -v >> {job_name}_charge_to_multipole_fit_out.txt 

""".format(mtpl_file=multipole_file, pot_cube=pot_cube_file, dens_cube=dens_cube_file, nchgs=chgs_per_atom,
           job_name=job_name, cpu_cores=cpu_cores, workdir=working_directory, cubefit=cubefit_exe)

def generate_mol_chg_fit_script(multipole_file, pot_cube_file, dens_cube_file, job_name,
            cpu_cores, working_directory, cubefit_exe, part1_dir, num_of_charges):
    import os
    return """#!/bin/bash

######################################################

#SBATCH --job-name={job_name}
#SBATCH --nodes=1
#SBATCH --ntasks={cpu_cores}
#SBATCH --cpus-per-task=1
#SBATCH -p ib
#SBATCH -o {workdir}/slurm-$JOBID.out

######################################################
cd {workdir}
cp {part1_dir}/multipole*_*charges.xyz .
mkdir slices
{cubefit} -greedy {mtpl_file} -esp {pot_cube} -dens {dens_cube} -ncmin {nchgs} -ncmax {nchgs} -nacmax 3 -ntry 5 -v >> {job_name}_molecular_charge_fit.out 

""".format(mtpl_file=multipole_file, pot_cube=pot_cube_file, dens_cube=dens_cube_file,
           job_name=job_name, cpu_cores=cpu_cores, workdir=working_directory, cubefit=cubefit_exe,
           nchgs=num_of_charges, part1_dir=part1_dir)

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

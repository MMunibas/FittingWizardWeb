import sys

script_path = "/home/wfit/FittingWizardWeb/fitting_service/main/algorithms/scripts/"

charmm_executable = "/data/wfit/storage/soft/charmm"
mpi_executable = "/opt/intel/openmpi/bin/mpirun"
mpi_flags = "--bind-to none"
scratch_dir_name = "scratch"
ld_path = "/export/cluster/programs/composer_xe_2013_sp1.2.144/lib/intel64/:/opt/intel/l_fcompxe_2013.4.183_redist/compiler/lib/intel64/:/opt/intel/openmpi/lib64/"
env_path = "/opt/intel/l_fcompxe_2013.4.183_redist/compiler/lib/intel64/:/opt/intel/openmpi/bin/"
number_of_cpu_cores = 8

gau_login_script = "/opt/cluster/programs/g09/g09_d.01/g09/bsd/g09.login.bash"
gau_formchk = "/opt/cluster/programs/g09/g09_d.01/g09/formchk"
gdma = "/data/wfit/storage/soft/gdma"
cubegen = "/opt/cluster/programs/g09/g09_d.01/g09/cubegen"
babel = "/usr/bin/babel"

fieldcomp = script_path+"fieldcomp"

def generate_gau_setup_script(input_file, output_file, working_directory, number_of_cpu_cores, job_name,
                              gau_login_script, scratch_dir_name, gau_formchk, chk_file, fchk_file, gdma, gdma_inp_name,
                              gdma_out_name, grid_spec, cubegen, cube_file, fieldcomp, gdma_pun_file, vdw_file_name,
                              babel, xyz_file_name, sdf_file_name):
    return """

######################################################

#$ -S /bin/sh
#$ -o {data_folder}
#$ -e {data_folder} 
#$ -N {job_name}
#$ -j y
#$ -pe smp {cpu_cores}
#$ -m n

######################################################
# lib for Gaussian
######################################################
source {login_script}

######################################################
# Creation of the working directory
######################################################

cd {workdir}
mkdir -p /{scratch_dir_name}/$USER.$JOB_ID
export GAUSS_SCRDIR=/{scratch_dir_name}/$USER.$JOB_ID

######################################################
#  Run
######################################################

$GAUSSIAN_EXE {input_file_name} {output_file_name}

######################################################
#  Create Formatted Checkpoint File
######################################################

{formchk} {chk_file} {fchk_file}

######################################################
#  Run GDMA Calculation
######################################################
{gdma} < {gdma_inp_name} > {gdma_out_name}

######################################################
#  Run Cubegen for ESP grid
######################################################
echo -e {grid_spec} | {cubegen} 0 potential {fchk_file} {cube_file} -1

######################################################
#  Run FieldComp ESP fitting code
######################################################
{fieldcomp} -cube {cube_file} -vdw {vdw_file} -pun {pun_file} > fieldcomp.log

######################################################
#  Convert xyz file to sdf for connectivity
######################################################
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
    return """

######################################################

#$ -S /bin/sh
#$ -o {data_folder}
#$ -e {data_folder} 
#$ -N {job_name}
#$ -j y
#$ -pe smp {cpu_cores}
#$ -m n

######################################################
# lib for CHARMM 
######################################################
export LD_LIBRARY_PATH={ld_path}:$LD_LIBRARY_PATH
export PATH={env_path}:$PATH

######################################################
# Creation of the working directory
######################################################

mkdir -p /{scratch_dir_name}/$USER.$JOB_ID

cp {input_file_name} /{scratch_dir_name}/$USER.$JOB_ID/.

cd /{scratch_dir_name}/$USER.$JOB_ID

# make data folder
mkdir -p {data_folder}

######################################################
#  Run
######################################################

export CHMDIR={input_dir}

my_charmm_para="{charmm_executable}"
{mpi_executable} {mpi_flags} -np {cpu_cores} $my_charmm_para < {input_file_name} > {output_file_name}

######################################################
# Copy of the output data to the data directory

cp /{scratch_dir_name}/$USER.$JOB_ID/* {data_folder}

# removing the working directory
rm -fr /{scratch_dir_name}/$USER.$JOB_ID
""".format(input_file_name=input_file, output_file_name=output_file, data_folder=working_directory,
           charmm_executable=charmm_executable, cpu_cores=number_of_cpu_cores, job_name=job_name, ld_path=ld_path,
           env_path=env_path, mpi_executable=mpi_executable, mpi_flags=mpi_flags, scratch_dir_name=scratch_dir_name,
           input_dir=input_dir_name)


if __name__ == "__main__":
    type = sys.argv[1]
    if type == "gau":
        print(generate_gau_setup_script("input file", "output file", "workdir", "numcores", "name", "script_path",
                                        "scratchdir"))
    elif type == "charmm":
        print(generate_charmm_setup_script("input file", "output file", "workdir", "charmm", "numcores", "name",
                                           "ldpath", "env_path", "mpi", "scratchdir"))

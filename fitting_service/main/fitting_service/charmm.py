def generate_charmm_setup_script(input_file, output_file, working_directory, charmm_executable, number_of_cpu_cores, job_name, ld_path, env_path,mpi_executable, mpi_flags, scratch_dir_name, input_dir_name):
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
""".format(input_file_name=input_file, output_file_name=output_file, data_folder=working_directory, charmm_executable=charmm_executable, cpu_cores=number_of_cpu_cores, job_name=job_name, ld_path=ld_path, env_path=env_path, mpi_executable=mpi_executable, mpi_flags=mpi_flags, scratch_dir_name=scratch_dir_name, input_dir=input_dir_name)


if __name__ == "__main__":
	print(generate_charmm_setup_script("input file", "output file", "workdir", "charmm", "numcores", "name", "ldpath", "env_path", "mpi", "scratchdir"))



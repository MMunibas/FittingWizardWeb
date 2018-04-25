def generate_gau_setup_script(input_file, output_file, working_directory, number_of_cpu_cores, job_name, gau_login_script, scratch_dir_name):
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

mkdir -p /{scratch_dir_name}/$USER.$JOB_ID
export GAUSS_SCRDIR=/{scratch_dir_name}/$USER.$JOB_ID

######################################################
#  Run
######################################################

$GAUSSIAN_EXE {input_file_name} {output_file_name}

""".format(input_file_name=input_file, output_file_name=output_file, data_folder=working_directory, cpu_cores=number_of_cpu_cores, job_name=job_name, login_script=gau_login_script, scratch_dir_name=scratch_dir_name)


if __name__ == "__main__":
	print(generate_gau_setup_script("input file", "output file", "workdir", "numcores", "name", "script_path", "scratchdir"))



def generate_gau_setup_script(input_file, output_file, working_directory, number_of_cpu_cores, job_name, gau_login_script, scratch_dir_name, gau_formchk, chk_file, fchk_file, gdma, gdma_inp_name, gdma_out_name, grid_spec, cubegen, cube_file, fieldcomp, gdma_pun_file, vdw_file_name, babel, xyz_file_name, sdf_file_name):
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

""".format(input_file_name=input_file, output_file_name=output_file, data_folder=working_directory, cpu_cores=number_of_cpu_cores, job_name=job_name, login_script=gau_login_script, scratch_dir_name=scratch_dir_name, formchk=gau_formchk, chk_file=chk_file, fchk_file=fchk_file, workdir=working_directory, gdma=gdma, gdma_inp_name=gdma_inp_name, gdma_out_name=gdma_out_name, grid_spec=grid_spec, cubegen=cubegen, cube_file=cube_file, fieldcomp=fieldcomp, pun_file=gdma_pun_file, vdw_file=vdw_file_name, babel=babel, xyz_file=xyz_file_name, sdf_file=sdf_file_name)


if __name__ == "__main__":
	print(generate_gau_setup_script("input file", "output file", "workdir", "numcores", "name", "script_path", "scratchdir"))



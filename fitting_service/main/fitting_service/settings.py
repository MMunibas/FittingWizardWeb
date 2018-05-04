#JOB_MANAGEMENT_TYPE = "SingleNodeJobManagement"
#QSUB_PATH = "C:\\Users\\eknecht\\Desktop\\grid_engine_mock\\qsub"
#QSTAT_PATH = "C:\\Users\\eknecht\\Desktop\\grid_engine_mock\\qstat"
#QDEL_PATH = "C:\\Users\\eknecht\\Desktop\\grid_engine_mock\\qdel"

script_path = "/home/wfit/FittingWizardWeb/fitting_service/main/algorithms/scripts/"

JOB_MANAGEMENT_TYPE = "GridEngineJobManagement"
QSUB_PATH = "/opt/sge/bin/linux-x64/qsub"
QSTAT_PATH = "/opt/sge/bin/linux-x64/qstat"
QDEL_PATH = "/opt/sge/bin/linux-x64/qdel"

FITTING_SERVICE_URI = 'http://localhost'
FITTING_SERVICE_HOST = '0.0.0.0'
FITTING_SERVICE_PORT = 5000

STORAGE_ROOT = "../data"

JOBS_FILE_NAME = ".jobs"
STATUS_FILE_NAME = ".status"
CANCEL_FILE_NAME = ".cancel"

CALCULATION_METADATA_FILE_NAME = "calculation_meta.json"
RUN_METADATA_FILE_NAME = "run_meta.json"


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

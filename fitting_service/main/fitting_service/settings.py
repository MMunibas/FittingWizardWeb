#JOB_MANAGEMENT_TYPE = "SingleNodeJobManagement"
#QSUB_PATH = "C:\\Users\\eknecht\\Desktop\\grid_engine_mock\\qsub"
#QSTAT_PATH = "C:\\Users\\eknecht\\Desktop\\grid_engine_mock\\qstat"
#QDEL_PATH = "C:\\Users\\eknecht\\Desktop\\grid_engine_mock\\qdel"


JOB_MANAGEMENT_TYPE = "SlurmJobManagement"
QSUB_PATH = "/bin/sbatch"
QSTAT_PATH = "/bin/sacct"
QDEL_PATH = "/bin/scancel"

FITTING_SERVICE_URI = 'http://localhost'
FITTING_SERVICE_HOST = '0.0.0.0'
FITTING_SERVICE_PORT = 5001

STORAGE_ROOT = "../data"







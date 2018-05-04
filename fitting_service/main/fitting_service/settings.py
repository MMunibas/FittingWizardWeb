#JOB_MANAGEMENT_TYPE = "SingleNodeJobManagement"
#QSUB_PATH = "C:\\Users\\eknecht\\Desktop\\grid_engine_mock\\qsub"
#QSTAT_PATH = "C:\\Users\\eknecht\\Desktop\\grid_engine_mock\\qstat"
#QDEL_PATH = "C:\\Users\\eknecht\\Desktop\\grid_engine_mock\\qdel"


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




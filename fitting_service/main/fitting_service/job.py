import threading
import time
from toolkit import RepeatingTimer, Singleton, synchronized
from .file_acces import IdGenerator, Storage, JobStatus
from .settings import JOB_MANAGEMENT_TYPE, QSTAT_PATH, QSUB_PATH, QDEL_PATH
from .job_managers import SingleNodeJobManagement, GridEngineJobManagement


class JobsService(metaclass=Singleton):
    def __init__(self):
        if JOB_MANAGEMENT_TYPE == "SingleNodeJobManagement":
            self.job_management_impl = SingleNodeJobManagement()

        elif JOB_MANAGEMENT_TYPE == "GridEngineJobManagement":
            self.job_management_impl = GridEngineJobManagement(QSTAT_PATH, QSUB_PATH, QDEL_PATH)

    def start_status_updater(self):
        JobStatusUpdater(self.job_management_impl)

    def start_cluster_simulation(self):
        if JOB_MANAGEMENT_TYPE == "SingleNodeJobManagement":
            self.simulated_cluster = self.job_management_impl.start_cluster_simulator(7)

    def stop_cluster_simulation(self):
        if JOB_MANAGEMENT_TYPE == "SingleNodeJobManagement":
            self.simulated_cluster.shutdown()

    def list_jobs_for_calculation(self, calculation_id):
        return Storage().get_jobs_file(calculation_id).list()

    def schedule_new_job(self, calculation_id, command):
        job_name = "WFIT{}".format(IdGenerator.base64_id(5))
        job_id = self.job_management_impl.schedule_new_job(job_name, command)
        Storage().get_jobs_file(calculation_id).add(job_id)
        JobStatusUpdater().watch(calculation_id, job_id)
        return job_id

    def job_status(self, job_id):
        return self.job_management_impl.job_status(job_id)

    def cancel_job(self, job_id):
        return self.job_management_impl.cancel_job(job_id)

    def wait_for_all_jobs(self):
        running_jobs = self.job_management_impl.list_running_jobs()
        for job in running_jobs:
            if self.job_status(job) != JobStatus.NOT_FOUND:
                self.wait_for_finished(job)

    def wait_for_finished(self, *jobs):
        for job in jobs:
            JobStatusUpdater().wait_for_finished(job)


@synchronized
class JobStatusUpdater(metaclass=Singleton):
    POLLING_FREQUENCY = 5

    def __init__(self, job_management=None):
        if job_management is None:
            raise Exception("job_management has to be initialized with a valid implementation")
        self.job_management = job_management
        self.job_id_event_dict = {}
        self.job_calculation_mapping = {}

        self.polling_thread = RepeatingTimer(self.POLLING_FREQUENCY, self._poll)
        self.polling_thread.start()

    def watch(self, calculation_id, job_id):
        event = threading.Event()
        self.job_id_event_dict[job_id] = event
        self.job_calculation_mapping[job_id] = calculation_id
        return event

    def terminate(self):
        self.polling_thread.stop()

    def wait_for_finished(self, job_id):
        print("waiting for job id: ", job_id)
        if isinstance(job_id, list):
            job_id = job_id[0]
        if job_id not in self.job_id_event_dict:
            return
        self.job_id_event_dict[job_id].wait()
        Storage().get_jobs_file(self.job_calculation_mapping[job_id]).remove(job_id)
        del self.job_id_event_dict[job_id]
        del self.job_calculation_mapping[job_id]

    def _poll(self):
        #print("polling...")
        running_jobs = self.job_management.list_running_jobs()
        #print("    running {}".format(running_jobs))
        #print("    calculations: {}".format(Storage().list_all_calculations()))
        for calc in Storage().list_all_calculations():
            queued_jobs = Storage().get_jobs_file(calc).list()
            #print("    queued  {}".format(queued_jobs))
            for job in queued_jobs:
                if job not in running_jobs:
                    if job in self.job_id_event_dict:
                        self.job_id_event_dict[job].set()
                        time.sleep(1)

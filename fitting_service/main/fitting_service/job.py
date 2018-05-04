import threading
import time
from logging import getLogger

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

    def schedule_new_job(self, calculation_id: str, command: str):
        job_name = "WFIT_{}_{}".format(calculation_id, IdGenerator.base64_id(5))
        job_id = self.job_management_impl.schedule_new_job(job_name, command)
        Storage().get_jobs_file(calculation_id).add(job_id)
        JobStatusUpdater().watch(job_id)
        return job_id

    def job_status(self, job_id: str):
        return self.job_management_impl.job_status(job_id)

    def cancel_job(self, job_id: str):
        return self.job_management_impl.cancel_job(job_id)

    def wait_for_finished(self, calculation_id: str, job_id_array: list):
        for job_id in job_id_array:
            JobStatusUpdater().wait_for_finished(job_id)
            Storage().get_jobs_file(calculation_id).remove(job_id)


class JobStatusUpdater(metaclass=Singleton):
    POLLING_FREQUENCY = 5

    def __init__(self, job_management=None):
        self.lock = threading.Lock()
        self._logger = getLogger(self.__class__.__name__)
        if job_management is None:
            raise Exception("job_management has to be initialized with a valid implementation")
        self.job_management = job_management
        self.job_id_event_dict = {}

        self.polling_thread = RepeatingTimer(self.POLLING_FREQUENCY, self._update_event_trigger)
        self.polling_thread.start()

    def _access_events(self, func):
        with self.lock:
            return func(self.job_id_event_dict)

    def watch(self, job_id: str):
        event = threading.Event()

        def add(d):
            d[job_id] = event

        self._access_events(add)

    def terminate(self):
        self.polling_thread.stop()

    def wait_for_finished(self, job_id: str):
        job_event_items = self._access_events(lambda d: d.copy())

        if job_id not in job_event_items:
            print("Wait finished. No job with id {} found.".format(job_id))
            return

        print("Waiting for job id: ", job_id)
        job_event_items[job_id].wait()

        def delete(d):
            del d[job_id]
        self._access_events(delete)
        print("Wait finished for job id: ", job_id)

    def _update_event_trigger(self):
        try:
            running_jobs = self.job_management.list_running_job_ids()
            job_event_items = self._access_events(lambda d: d.copy())
            for job_id, event_trigger in job_event_items.items():
                if job_id not in running_jobs:
                    event_trigger.set()
        except Exception:
            self._logger.exception("_update_event_trigger failed")

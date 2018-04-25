import threading
from logging import getLogger
from os import system
from .i_job_management import IJobManagement
from fitting_service.file_acces import Storage, JobStatus
from toolkit import RepeatingTimer, TransparentQueue

single_node_job_manager = None

class SingleNodeJobManagement(IJobManagement):

    def __init__(self):
        super().__init__()
        self.queue = TransparentQueue()  # queue.Queue is thread safe
        self.running_jobs = []
        global single_node_job_manager
        single_node_job_manager = self

    def start_cluster_simulator(self, num_workers=1):
        return ClusterSimulation(num_workers)

    def cancel_job(self, job_id):
        calcs = Storage().list_all_calculations()
        for calc in calcs:
            if job_id in Storage().get_jobs_file(calc).list():
                Storage().get_cancel_file(calc).is_set = True

    def schedule_new_job(self, job_name, command):
        job = SimulatedJob(job_name, command)
        self.queue.put(job)
        return job.job_id

    def fetch_job(self):
        if len(self.queue.list()) > 0:
            job = self.queue.get()
            self.running_jobs.append(job.job_id)
            return job

    def job_status(self, job_id):
        if job_id in self.running_jobs:
            return JobStatus.RUNNING
        if job_id in [job.job_id for job in self.queue.list()]:
            return JobStatus.WAITING
        return JobStatus.NOT_FOUND

    def list_running_job_ids(self):
        return self.running_jobs + [job.job_id for job in self.queue.list()]


class SimulatedJob:
    def __init__(self, job_id, command):
        self._logger = getLogger(self.__class__.__name__)
        self.job_id = job_id
        self.command = command

    def run(self):
        self._logger.info("{} {} -- started".format(self.job_id, self.command))
        system(self.command)
        self._logger.info("{} {} -- done".format(self.job_id, self.command))


class ClusterSimulation:
    workers = []

    def __init__(self, num_workers=1):
        for _ in range(num_workers):
            worker = JobConsumer()
            worker.start()
            self.workers.append(worker)

    def shutdown(self):
        for worker in self.workers:
            worker.timer.stop()
            worker.join()


class JobConsumer(threading.Thread):
    SCHEDULING_TIMER_DELAY_IN_S = 3.

    def __init__(self):
        threading.Thread.__init__(self)
        self.timer = RepeatingTimer(self.SCHEDULING_TIMER_DELAY_IN_S, self.fetch_job)
        self.timer.start()

    def fetch_job(self):
        global single_node_job_manager
        job = single_node_job_manager.fetch_job()
        if job:
            job.run()
            single_node_job_manager.running_jobs.remove(job.job_id)


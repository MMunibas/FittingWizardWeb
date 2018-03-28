from abc import ABCMeta, abstractmethod
from logging import getLogger
import os
import threading
import queue
import time
from toolkit import RepeatingTimer, Singleton, synchronized
from .file_acces import IdGenerator, Storage, JobStatus


class JobsService(metaclass=Singleton):
    def __init__(self):
        self.job_management_impl = SingleNodeJobManagement()
        JobStatusUpdater(self.job_management_impl)

        if isinstance(self.job_management_impl, SingleNodeJobManagement):
            self.cluster = ClusterSimulation()

    def list_jobs_for_calculation(self, calculation_id):
        return Storage().get_jobs_file(calculation_id).list()

    def schedule_new_job(self, calculation_id, command):
        job_id = "{}".format(IdGenerator.base64_id(5))
        self.job_management_impl.schedule_new_job(job_id, command)
        Storage().get_jobs_file(calculation_id).add(job_id)
        JobStatusUpdater().watch(calculation_id, job_id)
        return job_id

    def job_status(self, job_id):
        return self.job_management_impl.job_status(job_id)

    def cancel_cob(self, job_id):
        return self.job_management_impl.cancel_job(job_id)

    def wait_for_all_jobs(self):
        running_jobs = self.job_management_impl.list_running_jobs()
        for job in running_jobs:
            if self.job_status(job) != JobStatus.NOT_FOUND:
                self.wait_for_finished(job)

    def wait_for_finished(self, *jobs):
        for job in jobs:
            JobStatusUpdater().wait_for_finished(job)


class IJobManagement(metaclass=ABCMeta):
    @abstractmethod
    def list_running_jobs(self):
        pass

    @abstractmethod
    def schedule_new_job(self, job_id, command):
        pass

    @abstractmethod
    def job_status(self, job_id):
        pass

    @abstractmethod
    def cancel_job(self, job_id):
        pass


class TransparentQueue(queue.Queue):
    def list(self):
        with self.mutex:
            return list(self.queue)


class SingleNodeJobManagement(IJobManagement):

    def __init__(self):
        super().__init__()
        self.queue = TransparentQueue()  # queue.Queue is thread safe
        self.running_jobs = []

    def cancel_job(self, job_id):
        calc = Storage().get_calculation_by_job(job_id)
        Storage().get_cancel_file(calc).is_set = True

    def schedule_new_job(self, job_id, command):
        job = SimulatedJob(job_id, command)
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

    def list_running_jobs(self):
        return self.running_jobs + [job.job_id for job in self.queue.list()]


class SimulatedJob:
    def __init__(self, job_id, command):
        self._logger = getLogger(self.__class__.__name__)
        self.job_id = job_id
        self.command = command

    def run(self):
        self._logger.info("{} {} -- started".format(self.job_id, self.command))
        os.system(self.command)
        self._logger.info("{} {} -- done".format(self.job_id, self.command))


class JobConsumer(threading.Thread):
    SCHEDULING_TIMER_DELAY_IN_S = 3.

    def __init__(self):
        threading.Thread.__init__(self)
        self.timer = RepeatingTimer(self.SCHEDULING_TIMER_DELAY_IN_S, self.fetch_job)
        self.timer.start()

    def fetch_job(self):
        job = JobsService().job_management_impl.fetch_job()
        if job:
            job.run()
            JobsService().job_management_impl.running_jobs.remove(job.job_id)


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

    def wait_for_finished(self, job_id):
        if job_id not in self.job_id_event_dict:
            return
        self.job_id_event_dict[job_id].wait()
        Storage().get_jobs_file(self.job_calculation_mapping[job_id]).remove(job_id)
        del self.job_id_event_dict[job_id]
        del self.job_calculation_mapping[job_id]

    def _poll(self):
        running_jobs = self.job_management.list_running_jobs()
        for calc in Storage().list_all_calculations():
            for job in Storage().get_jobs_file(calc).list():
                if job not in running_jobs:
                    if job in self.job_id_event_dict:
                        self.job_id_event_dict[job].set()
                        time.sleep(0)


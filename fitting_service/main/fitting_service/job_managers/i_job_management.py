from abc import ABCMeta, abstractmethod


class IJobManagement(metaclass=ABCMeta):
    @abstractmethod
    def list_running_job_ids(self):
        pass

    @abstractmethod
    def schedule_new_job(self, job_name, command):
        pass

    @abstractmethod
    def job_status(self, job_id):
        pass

    @abstractmethod
    def cancel_job(self, job_id):
        pass

# Implement new job scheduler

# Implementation
Implement this interface:
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

## Registration

Inside the constructor of the JobsService defined in the main/fitting_service/jobs module, replace "self.job_management_impl = SingleNodeJobManagement()"
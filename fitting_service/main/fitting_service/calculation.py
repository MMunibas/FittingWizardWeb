from logging import getLogger
from threading import Thread

from algorithms import IContext
from toolkit import Singleton, synchronized, CalculationCanceledException, RepeatingTimer
from .algorithms.scanner import Scanner
from .file_acces import StorageService, Status
from .job import JobsService

SERVICE_VERSION = "1.0.1"


class CalculationService(metaclass=Singleton):
    def __init__(self):
        jobs_service = JobsService()
        jobs_service.start_job_synchronization()

    @property
    def info(self):
        return {
            "version": SERVICE_VERSION,
            "server_status": self.sanity_check
        }

    @property
    def sanity_check(self):
        # get from CalculationScheduler
        return "totally insane"

    def get_calculation_directory(self, calculation_id):
        if not StorageService().contains_calculation(calculation_id):
            raise CalculationNotFoundException

        return StorageService().get_calculation_directory(calculation_id)

    def get_calculation_status(self, calculation_id):
        if not StorageService().contains_calculation(calculation_id):
            raise CalculationNotFoundException

        return self.get_calculation_directory(calculation_id).get_status()

    def cancel_calculation(self, calculation_id):
        if not StorageService().contains_calculation(calculation_id):
            raise CalculationNotFoundException

        calculation_management = CalculationManagement()
        if calculation_management.is_running(calculation_id):
            calculation_management.request_cancel(calculation_id)

    def delete_calculation(self, calculation_id):
        if not StorageService().contains_calculation(calculation_id):
            raise CalculationNotFoundException
        if CalculationManagement().is_running(calculation_id):
            raise CalculationRunningException

        return self.get_calculation_directory(calculation_id).delete()

    def create_new_calculation(self, parameter):
        calc_id = StorageService().create_new_calculation(parameter)
        return calc_id

    def list_all_calculations(self):
        return StorageService().list_all_calculations()

    def save_input_file(self, calculation_id, filename, data):
        if not StorageService().contains_calculation(calculation_id):
            raise CalculationNotFoundException

        self.get_calculation_directory(calculation_id).save_input_file(filename, data)

    def run_calculation(self, calculation_id, run_details, blocking=False):
        if not StorageService().contains_calculation(calculation_id):
            raise CalculationNotFoundException

        return CalculationManagement().start(calculation_id, run_details, blocking)

    def list_algorithms(self):
        return list(Scanner().find_algorithms().keys())

    def is_calculation_running(self, calculation_id):
        if not StorageService().contains_calculation(calculation_id):
            return False

        return self.get_calculation_directory(calculation_id).is_running()

    def calculation_exists(self, calculation_id):
        return StorageService().contains_calculation(calculation_id)

    def list_output_files(self, calculation_id):
        calc_dir = self.get_calculation_directory(calculation_id)
        return calc_dir.list_last_run_output_files()

    def list_input_files(self, calculation_id):
        return self.get_calculation_directory(calculation_id).list_input_files()

    def get_output_file_absolute_path(self, calculation_id, relative_path):
        calc_dir = self.get_calculation_directory(calculation_id)
        return calc_dir.get_last_run_outputfile_path(relative_path)

    def delete_input_file(self, calculation_id, relative_path):
        path = self.get_input_file_absolute_path(calculation_id, relative_path)
        return StorageService().delete(path)

    def delete_output_file(self, calculation_id, relative_path):
        path = self.get_output_file_absolute_path(calculation_id, relative_path)
        return StorageService().delete(path)

    def get_input_file_absolute_path(self, calculation_id, relative_path):
        dir = self.get_calculation_directory(calculation_id)
        return dir.get_input_file_path(relative_path)

    def set_calculation_parameter(self, calculation_id, parameters):
        calc = self.get_calculation_directory(calculation_id)
        calc.set_calculation_parameter(parameters)

    def is_algorithm_supported(self, algo_name):
        return Scanner().has_algorithm(algo_name)

    def read_last_run_result(self, calculation_id):
        return StorageService().get_calculation_directory(calculation_id).read_last_run_result()

    def check_if_run_exists(self, calculation_id):
        calc_dir = StorageService().get_calculation_directory(calculation_id)
        return calc_dir.has_existing_run()


@synchronized
class CalculationManagement(metaclass=Singleton):
    running_calculations = {}

    def __init__(self):
        self.calculation_cleanup_timer = RepeatingTimer(5, self.cleanup)

    def is_running(self, calculation_id):
        return calculation_id in self.running_calculations and self.running_calculations[calculation_id].is_alive()

    def start(self, calculation_id, run_details, blocking=False):
        calc_dir = StorageService().get_calculation_directory(calculation_id)

        run_id = calc_dir.prepare_run(run_details)

        context = CalculationContext(calc_dir, run_id)

        algorithm = Scanner().find_algorithm(context.algorithm)
        algorithm.validate_input(context)

        run_thread = CalculationRun(algorithm, context)
        run_thread.start()
        self.running_calculations[calculation_id] = run_thread
        if blocking:
            run_thread.join()
        return run_id

    def request_cancel(self, calculation_id):
        if calculation_id in self.running_calculations:
            self.running_calculations[calculation_id].cancel()
            del self.running_calculations[calculation_id]

    def cleanup(self):
        indices_to_remove = [k for k, v in self.running_calculations.items() if not v.is_alive()]
        for i in reversed(indices_to_remove):
            del self.running_calculations[i]


class CalculationRun(Thread):
    def __init__(self, algorithm, context):
        self._logger = getLogger(self.__class__.__name__)
        Thread.__init__(self)
        self.algorithm = algorithm
        self.context = context

    def run(self):
        self.context.set_running_status("")
        try:
            self.algorithm(self.context)
            self.context.set_finished()
        except CalculationCanceledException:
            self._logger.info("cancel called")
            self.context.set_canceled()
        except Exception as e:
            self._logger.exception("failure in calculation run")
            self.context.set_failed(e)
        finally:
            self.context.cancel_all_jobs()

    def cancel(self):
        self.context.request_cancel()


class CalculationRunningException(Exception):
    pass


class CalculationNotFoundException(Exception):
    pass


class CalculationContext(IContext):
    def __init__(self, calc_dir, run_id):
        """
        CalculationContext
        """
        self._calculation_id = calc_dir.get_id()
        self._run_id = run_id
        self._calc_dir = calc_dir
        self._logger = getLogger("calc[{}]".format(self._calculation_id))
        self._cancel_requested = False

        self._run_dir = calc_dir.subdir(run_id)

    @property
    def log(self):
        return self._logger

    @property
    def input_dir(self):
        return self._calc_dir.subdir("input")

    @property
    def run_out_dir(self):
        return self._run_dir.subdir("output")

    @property
    def run_tmp_dir(self):
        return self._run_dir.subdir("tmp")

    @property
    def calc_out_dir(self):
        return self._calc_dir.subdir("output")

    @property
    def parameters(self):
        return self._calc_dir.get_merged_parameters(self._run_id)

    @property
    def algorithm(self):
        return self._calc_dir.get_algorithm_type(self._run_id)

    def update_status(self, status, message):
        self._calc_dir.update_status(status, message)

    def set_running_status(self, message):
        self.update_status(Status.RUNNING, message)

    def set_canceled(self, message=""):
        self.update_status(Status.CANCELED, message)

    def set_finished(self, message=""):
        self.update_status(Status.FINISHED, message)

    def set_failed(self, exception: Exception):
        self.update_status(Status.FAILED, str(exception))

    def terminate_if_canceled(self):
        if self._calc_dir.is_canceled():
            raise CalculationCanceledException

    def request_cancel(self):
        self._calc_dir.set_canceled()
        self.cancel_all_jobs()

    def schedule_job(self, command):
        return JobsService().schedule_new_job(self._calculation_id, command)

    def job_status(self, job_id):
        return JobsService().job_status(job_id)

    def wait_for_all_jobs(self):
        job_ids = ', '.join(self.job_ids)
        self.set_running_status('Waiting for jobs: ' + job_ids)
        self.wait_for_finished_jobs(*self.job_ids)

    def write_results(self, json_object):
        self._calc_dir.write_run_results(self._run_id, json_object)

    def wait_for_finished_jobs(self, *job_ids):
        JobsService().wait_for_finished(self._calculation_id, list(job_ids))
        self.terminate_if_canceled()

    def cancel_all_jobs(self):
        for job_id in self.job_ids:
            JobsService().cancel_job(job_id)

    @property
    def job_ids(self):
        return self._calc_dir.list_job_ids()

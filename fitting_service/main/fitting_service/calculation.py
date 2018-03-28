import os
import json
from threading import Thread
from logging import getLogger
from werkzeug.utils import secure_filename
from algorithms import AlgorithmRegistry
from toolkit import Singleton, synchronized, CalculationCanceledException, RepeatingTimer
from .file_acces import Storage, Filename, Directory, Status
from .job import JobsService

VERSION = 0.1


class CalculationService(metaclass=Singleton):
    def __init__(self):
        JobsService()

    @property
    def info(self):
        return {
            "version": VERSION,
            "server_status": self.sanity_check
        }

    @property
    def sanity_check(self):
        # get from CalculationScheduler
        return "totally insane"

    def get_by_id(self, calculation_id):
        if Storage().contains_calculation(calculation_id):
            return CalculationDirectory(calculation_id)
        raise CalculationNotFoundException

    def get_calculation_status(self, calculation_id):
        if not Storage().contains_calculation(calculation_id):
            raise CalculationNotFoundException
        return self.get_by_id(calculation_id).status.to_dict()

    def cancel_calculation(self, calculation_id):
        if not Storage().contains_calculation(calculation_id):
            raise CalculationNotFoundException
        calculation_management = CalculationManagement()
        if calculation_management.is_running(calculation_id):
            return calculation_management.request_cancel(calculation_id)
        else:
            raise CalculationNotRunningException

    def delete_calculation(self, calculation_id):
        if not Storage().contains_calculation(calculation_id):
            raise CalculationNotFoundException
        if CalculationManagement().is_running(calculation_id):
            raise CalculationRunningException
        return self.get_by_id(calculation_id).delete()

    def create_new_calculation(self, parameters):
        return CalculationDirectory(parameters).get_id()

    def list_all_calculations(self):
        return list({
                        "id": calculation,
                        "status": self.get_calculation_status(calculation)
                    } for calculation in Storage().root.list_files())

    def download_file(self, calculation_id, request):
        if not Storage().contains_calculation(calculation_id):
            raise CalculationNotFoundException

        if 'file' not in request.files:
            raise InvalidInputException

        file = request.files['file']
        if file.filename == '':
            raise InvalidInputException

        if file:
            filename = secure_filename(file.filename)
            self.get_by_id(calculation_id).save_input_file(filename, file)
            return {}

    def run_calculation(self, calculation_id, parameters):
        if not Storage().contains_calculation(calculation_id):
            return None
        return CalculationManagement().start(calculation_id, parameters)

    def list_algorithms(self):
        return AlgorithmRegistry().list_algorithms()

    def is_running(self, calculation_id):
        if not Storage().contains_calculation(calculation_id):
            return False
        return self.get_by_id(calculation_id).status.is_running

    def calculation_exists(self, calculation_id):
        return Storage().contains_calculation(calculation_id)


@synchronized
class CalculationManagement(metaclass=Singleton):
    running_calculations = {}

    def __init__(self):
        self.calculation_cleanup_timer = RepeatingTimer(5, self.cleanup)

    def is_running(self, calculation_id):
        return calculation_id in self.running_calculations and self.running_calculations[calculation_id].is_alive()

    def start(self, calculation_id, parameters):

        calculation = CalculationDirectory(calculation_id)
        if calculation.status.is_running:
            return None
        run = calculation.prepare_run(parameters)

        status = Storage().get_status_file(calculation.calculation_id)
        if not status.is_ready:
            raise Exception("Calculation no ready")
        context = CalculationContext(calculation, run)
        if "algorithm" not in context.run_parameters \
                or context.run_parameters["algorithm"] not in AlgorithmRegistry().algorithms:
            raise Exception("Algorithm not found")
        algo = AlgorithmRegistry().algorithms[context.run_parameters["algorithm"]]
        if algo.can_validate_input:
            input_valid = algo.input_validator(context.input_dir)
            if not input_valid:
                raise Exception("Input not valid")
        run_thread = CalculationRun(algo, context)
        run_thread.start()
        self.running_calculations[calculation_id] = run_thread
        return run

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
        Thread.__init__(self)
        self.algorithm = algorithm
        self.context = context

    def run(self):
        self.context.update_status(Status.RUNNING, "")
        try:
            self.algorithm.execute(self.context)
            self.context.set_finished()
        except CalculationCanceledException:
            self.context.set_canceled()
        except Exception as e:
            self.context.handle_exception("Something went wrong during execution of {}".format(self.algorithm.__name__), e)
        finally:
            self.context.cancel_all_jobs()

    def cancel(self):
        self.context.request_cancel()


class CalculationDirectory(Directory):
    CALCULATION_METADATA_FILE_NAME = "calculation_meta.json"
    RUN_METADATA_FILE_NAME = "run_meta.json"

    def __init__(self, parameter):
        self.calculation_id = None
        self._dir = None
        self._logger = getLogger(self.__class__.__name__)

        if not isinstance(parameter, str):
            self._create(parameter)
            Directory.__init__(self, Storage().get_calculation_directory_path(self.calculation_id))
        else:
            Directory.__init__(self, Storage().get_calculation_directory_path(parameter))
            self._load(parameter)

    def _load(self, calculation_id):
        self.calculation_id = calculation_id
        if Storage().contains_calculation(calculation_id):
            calculation_directory = Storage().get_calculation_directory(calculation_id)
            if calculation_directory.contains(CalculationDirectory.CALCULATION_METADATA_FILE_NAME):
                self.dir = calculation_directory
        return None

    def _create(self, parameters):
        self.calculation_id = Filename.new_calculation_filename()
        self.dir = Storage().get_calculation_directory(self.calculation_id)

        calculation_metadata = {
            "parameters": parameters
        }
        with self.dir.open_file(self.CALCULATION_METADATA_FILE_NAME, "w") as f:
            json.dump(calculation_metadata, f)
        self.dir.subdir("input")
        Storage().get_status_file(self.calculation_id)

    def get_id(self):
        return self.calculation_id

    def save_input_file(self, filename, data):
        input_dir = self.dir.subdir("input")
        data.save(os.path.join(input_dir.full_path, filename))

    def prepare_run(self, parameters):
        if self.status.is_running:
            return None
        del Storage().get_cancel_file(self.calculation_id).is_set
        Storage().get_jobs_file(self.calculation_id).clear()
        run_id = Filename.new_run_filename()
        run_dir = self.subdir(run_id)
        run_dir.subdir("output")

        with run_dir.open_file(self.RUN_METADATA_FILE_NAME, "w") as f:
            json.dump(parameters, f)

        self.status.set_last_run(run_id)

        return run_id

    @property
    def status(self):
        try:
            return Storage().get_status_file(self.calculation_id)
        except:
            self._logger.error("Unable to get calculation status")
            raise


class CalculationRunningException(Exception):
    pass


class CalculationNotRunningException(Exception):
    pass


class CalculationNotFoundException(Exception):
    pass


class InvalidInputException(Exception):
    pass


class CalculationContext(object):
    def __init__(self, calculation, run_id):
        """
        CalculationContext
        """
        self._calculation_id = calculation.calculation_id
        self._run_id = run_id
        self._data_dir = calculation
        self._logger = getLogger("calc[{}]".format(self._calculation_id))
        self._cancel_requested = False

    @property
    def log(self):
        return self._logger

    @property
    def base_path(self):
        return self._data_dir

    @property
    def work_dir(self):
        return self.base_path.subdir(self.status.last_run())

    @property
    def input_dir(self):
        return self.base_path.subdir("input")

    @property
    def output_dir(self):
        return self.work_dir.subdir("output")

    @property
    def calculation_parameters(self):
        with self.base_path.open_file(CalculationDirectory.CALCULATION_METADATA_FILE_NAME, "r") as calc_meta:
            return json.load(calc_meta)

    @property
    def run_parameters(self):
        with self.work_dir.open_file(CalculationDirectory.RUN_METADATA_FILE_NAME, "r") as run_meta:
            return json.load(run_meta)

    @property
    def status(self):
        return Storage().get_status_file(self._calculation_id)

    def sleep(self, delay_in_s):
        import time
        time.sleep(delay_in_s)

    def update_status(self, status, message):
        return self.status.update_status(status, message)

    def set_canceled(self, message=""):
        self.status.update_status(Status.CANCELED, message)

    def set_finished(self, message=""):
        self.status.update_status(Status.FINISHED, message)

    def set_failed(self, exception):
        self.status.update_status(Status.FAILED, exception)

    def handle_exception(self, message, exception):
        self.log.error(message)
        self.log.error(exception)

    def _end_run(self):
        CalculationManagement().cleanup()
        # remove from calculation management
        # write file
        pass

    def terminate_if_canceled(self):
        if Storage().get_cancel_file(self._calculation_id).is_set:
            raise CalculationCanceledException

    def request_cancel(self):
        Storage().get_cancel_file(self._calculation_id).is_set = True

    def cancel_all_jobs(self):
        for calc in Storage().list_all_calculations():
            Storage().get_cancel_file(calc).is_set = True

    def schedule_job(self, command):
        return JobsService().schedule_new_job(self._calculation_id, command)

    def job_status(self, job_id):
        return JobsService().job_status(job_id)

    def wait_for_finished_jobs(self, *jobs):
        JobsService().wait_for_finished(*jobs)

    def wait_for_all_jobs(self):
        JobsService().wait_for_all_jobs()

    @property
    def jobs(self):
        return Storage().get_jobs_file(self._calculation_id)

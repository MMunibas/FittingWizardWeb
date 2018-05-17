import os
import json
from threading import Thread
from logging import getLogger
from werkzeug.utils import secure_filename
from algorithms import IContext
from .algorithms.scanner import Scanner, InvalidInputException
from toolkit import Singleton, synchronized, CalculationCanceledException, RepeatingTimer
from .file_acces import Storage, Filename, Directory, Status, CalculationStatus
from .job import JobsService
from .settings import CALCULATION_METADATA_FILE_NAME, RUN_METADATA_FILE_NAME


VERSION = 0.2


class CalculationService(metaclass=Singleton):
    def __init__(self):
        jobs_service = JobsService()
        jobs_service.start_job_synchronization()

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
            calculation_management.request_cancel(calculation_id)

    def delete_calculation(self, calculation_id):
        if not Storage().contains_calculation(calculation_id):
            raise CalculationNotFoundException
        if CalculationManagement().is_running(calculation_id):
            raise CalculationRunningException
        return self.get_by_id(calculation_id).delete()

    def create_new_calculation(self, parameters):
        calc_dir = CalculationDirectory(parameters)

        return {"calculation": calc_dir.get_id()}

    def list_all_calculations(self):
        return {"calculations": list({
                        "id": calculation,
                        "status": self.get_calculation_status(calculation)
                    } for calculation in Storage().root.list_subdirs())}

    def upload_file(self, calculation_id, request):
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

    def run_calculation(self, calculation_id, parameters, blocking=False):
        if not Storage().contains_calculation(calculation_id):
            raise CalculationNotFoundException
        return CalculationManagement().start(calculation_id, parameters, blocking)

    def list_algorithms(self):
        return {"algorithms": list(Scanner().find_algorithms().keys())}

    def is_running(self, calculation_id):
        if not Storage().contains_calculation(calculation_id):
            return False
        return self.get_by_id(calculation_id).status.is_running

    def calculation_exists(self, calculation_id):
        return Storage().contains_calculation(calculation_id)

    def _get_output_dir_of_last_run(self, calculation_id):
        calc = self.get_by_id(calculation_id)
        last_run = calc.status.last_run()
        if last_run:
            return Storage().get_output_dir(calculation_id, last_run)

    def list_output_files(self, calculation_id):
        return {"files": self._get_output_dir_of_last_run(calculation_id).list_files_recursively()}

    def list_input_files(self, calculation_id):
        return {"files": self.get_by_id(calculation_id).subdir("input").list_files_recursively()}

    def get_output_file_absolute_path(self, calculation_id, relative_path):
        return os.path.join(self._get_output_dir_of_last_run(calculation_id).full_path, relative_path)

    def delete_input_file(self, calculation_id, relative_path):
        path = self.get_input_file_absolute_path(calculation_id, relative_path)
        return Storage().delete(path)

    def delete_output_file(self, calculation_id, relative_path):
        path = self.get_output_file_absolute_path(calculation_id, relative_path)
        return Storage().delete(path)

    def get_input_file_absolute_path(self, calculation_id, relative_path):
        return os.path.join(self.get_by_id(calculation_id).subdir("input").full_path, relative_path)

    def set_calculation_parameters(self, calculation_id, parameters):
        calc = self.get_by_id(calculation_id)

        with calc.open_file(CALCULATION_METADATA_FILE_NAME, "r") as meta_file:
            meta = json.load(meta_file)
        if isinstance(parameters["parameters"], str):
            newparams = json.loads(parameters["parameters"])
        else:
            newparams = parameters["parameters"]

        meta["parameters"] = newparams

        with calc.open_file(CALCULATION_METADATA_FILE_NAME, "w") as meta_file:
            json.dump(meta, meta_file)

    def is_algorithm_supported(self, algo_name):
        return Scanner().has_algorithm(algo_name)

    def read_last_run_result(self, calculation_id):
        return CalculationDirectory(calculation_id).read_last_run_result()


@synchronized
class CalculationManagement(metaclass=Singleton):
    running_calculations = {}

    def __init__(self):
        self.calculation_cleanup_timer = RepeatingTimer(5, self.cleanup)

    def is_running(self, calculation_id):
        return calculation_id in self.running_calculations and self.running_calculations[calculation_id].is_alive()

    def start(self, calculation_id, parameters, blocking=False):
        calc_dir = CalculationDirectory(calculation_id)

        run_id = calc_dir.prepare_run(parameters)

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


class CalculationDirectory(Directory):

    RESULTS_FILE_NAME = 'run_results.json'

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
            if calculation_directory.contains(CALCULATION_METADATA_FILE_NAME):
                self._dir = calculation_directory

    def _create(self, parameters):
        self.calculation_id = Filename.new_calculation_filename()
        self._dir = Storage().get_calculation_directory(self.calculation_id)
        if isinstance(parameters["parameters"], str):
            calculation_metadata = {
                "parameters": json.loads(parameters["parameters"])
            }
        else:
            calculation_metadata = parameters
        with self._dir.open_file(CALCULATION_METADATA_FILE_NAME, "w") as f:
            json.dump(calculation_metadata, f)
        self._dir.subdir("input")
        Storage().get_status_file(self.calculation_id)

    def get_id(self):
        return self.calculation_id

    def save_input_file(self, filename, data):
        input_dir = self._dir.subdir("input")
        data.save(os.path.join(input_dir.full_path, filename))

    def prepare_run(self, parameters):
        if self.status.is_running:
            raise CalculationRunningException

        del Storage().get_cancel_file(self.calculation_id).is_set
        Storage().get_jobs_file(self.calculation_id).clear()
        run_id = Filename.new_run_filename()
        run_dir = self.subdir(run_id)
        run_dir.subdir("output")
        if isinstance(parameters, str):
            parameters = json.loads(parameters)
        if isinstance(parameters["parameters"], str):
            parameters["parameters"] = json.loads(parameters["parameters"])
        with run_dir.open_file(RUN_METADATA_FILE_NAME, "w") as f:
            json.dump(parameters, f)

        self.status.set_last_run(run_id)

        return run_id

    @property
    def status(self):
        try:
            status = CalculationStatus(self)

            with self.open_file(CALCULATION_METADATA_FILE_NAME, "r") as f:
                calculation_prams = json.load(f)
            if status.last_run():
                with self.subdir(status.last_run()).open_file(RUN_METADATA_FILE_NAME, "r") as f:
                    run_params = json.load(f)
            else:
                run_params = None

            status.set_calculation_parameters(calculation_prams)
            status.set_run_parameters(run_params)
            status.set_input_files(self.subdir("input").list_files_recursively())
            return status
        except:
            self._logger.error("Unable to get calculation status")
            raise

    def read_last_run_result(self):
        last_run = self.status.last_run()
        if last_run is None:
            return None
        return self.read_run_result(last_run)

    def read_run_result(self, run_id):
        run_dir = self.subdir(run_id)
        run_out_dir = run_dir.subdir('output')
        if not run_out_dir.contains(self.RESULTS_FILE_NAME):
            return None

        with run_out_dir.open_file(self.RESULTS_FILE_NAME, 'r') as json_file:
            return json.load(json_file)

    def write_run_results(self, run_id, json_object):
        run_output = self.subdir(run_id).subdir('output')
        with run_output.open_file(self.RESULTS_FILE_NAME, 'w') as json_file:
            json.dump(json_object, json_file)


class CalculationRunningException(Exception):
    pass


class CalculationNotFoundException(Exception):
    pass


class CalculationContext(IContext):
    def __init__(self, calc_dir, run_id):
        """
        CalculationContext
        """
        self._calculation_id = calc_dir.calculation_id
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
        params = {}

        with self._calc_dir.open_file(CALCULATION_METADATA_FILE_NAME, "r") as calc_meta:
            for k, v in json.load(calc_meta)["parameters"].items():
                params[k] = v

        with self._run_dir.open_file(RUN_METADATA_FILE_NAME, "r") as run_meta:
            for k, v in json.load(run_meta)["parameters"].items():
                params[k] = v

        return params

    @property
    def algorithm(self):
        with self._run_dir.open_file(RUN_METADATA_FILE_NAME, "r") as run_meta:
            return json.load(run_meta)["algorithm"]

    @property
    def status(self):
        return Storage().get_status_file(self._calculation_id)

    def update_status(self, status, message):
        self.status.update_status(status, message)

    def set_running_status(self, message):
        self.update_status(Status.RUNNING, message)

    def set_canceled(self, message=""):
        self.update_status(Status.CANCELED, message)

    def set_finished(self, message=""):
        self.update_status(Status.FINISHED, message)

    def set_failed(self, exception: Exception):
        self.update_status(Status.FAILED, str(exception))

    def terminate_if_canceled(self):
        if Storage().get_cancel_file(self._calculation_id).is_set:
            raise CalculationCanceledException

    def request_cancel(self):
        Storage().get_cancel_file(self._calculation_id).is_set = True
        self.cancel_all_jobs()

    def schedule_job(self, command):
        return JobsService().schedule_new_job(self._calculation_id, command)

    def job_status(self, job_id):
        return JobsService().job_status(job_id)

    def wait_for_all_jobs(self):
        self.wait_for_finished_jobs(*self.job_ids)

    def write_results(self, json_object):
        CalculationDirectory(self._calculation_id).write_run_results(self._run_id, json_object)

    def wait_for_finished_jobs(self, *job_ids):
        JobsService().wait_for_finished(self._calculation_id, list(job_ids))
        self.terminate_if_canceled()

    def cancel_all_jobs(self):
        for job_id in self.job_ids:
            JobsService().cancel_job(job_id)

    @property
    def job_ids(self):
        return Storage().get_jobs_file(self._calculation_id).list()

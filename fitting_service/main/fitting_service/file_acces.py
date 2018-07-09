import json
import os
import random
from base64 import urlsafe_b64encode
from contextlib import contextmanager
from datetime import datetime
from hashlib import sha256
from logging import getLogger

from algorithms.toolkit import IDirectory
from toolkit import *
from .settings import STORAGE_ROOT


class IdGenerator(object):
    @staticmethod
    def base64_id(length=5):
        b64 = str(urlsafe_b64encode(sha256(str(random.random()).encode("utf-8")).digest()))
        return b64[2:length + 2]


class Timestamp(object):
    @staticmethod
    def now():
        now = datetime.now()
        return Timestamp.format(now)

    @staticmethod
    def format(timestamp):
        date = {
            "year": str(timestamp.year).zfill(4),
            "month": str(timestamp.month).zfill(2),
            "day": str(timestamp.day).zfill(2),
            "hour": str(timestamp.hour).zfill(2),
            "min": str(timestamp.minute).zfill(2),
            "sec": str(timestamp.second).zfill(2),
            "micro": str(timestamp.microsecond).zfill(6)
        }

        return "{year}-{month}-{day}_{hour}-{min}-{sec}-{micro}".format(**date)

    @staticmethod
    def parse(string):
        try:
            year = int(string[:4])
            month = int(string[5:7])
            day = int(string[8:10])

            hour = int(string[11:13])
            _min = int(string[14:16])
            sec = int(string[17:19])
            micro = int(string[20:26])
            return datetime(year, month, day, hour, _min, sec, micro)

        except ValueError:
            # string index out of range
            return None
        except IndexError:
            # invalid timestamp value
            return None


class Filename(object):
    @staticmethod
    def new_calculation_filename():
        return "{}_{}".format(Timestamp.now(), IdGenerator.base64_id(5))

    @staticmethod
    def new_run_filename():
        return "{}_{}".format(Timestamp.now(), IdGenerator.base64_id(5))


@synchronized
class StorageService(metaclass=Singleton):
    def __init__(self):
        self._storage_root = Directory(STORAGE_ROOT)

    def set_root(self, path):
        if not os.path.exists(path):
            os.mkdir(path)
        self._storage_root = Directory(path)

    def _get_calculation_directory_path(self, calculation_id):
        return os.path.join(self._root.full_path, calculation_id)

    def get_calculation_directory(self, calculation_id):
        if not self.contains_calculation(calculation_id):
            raise Exception("calculation {} not found".format(calculation_id))

        calc_dir = self._get_calculation_directory_path(calculation_id)
        return CalculationDirectory(calc_dir)

    def contains_calculation(self, calculation_id):
        return self._root.has_subdir(calculation_id)

    def list_all_calculations(self):
        sub_dirs = self._root.list_subdirs()
        return list(self.get_calculation_directory(dir) for dir in sub_dirs)

    @property
    def _root(self):
        return self._storage_root

    def initialize(self):
        self.set_root(STORAGE_ROOT)
        pass

    def delete(self, path):
        if not os.path.exists(path):
            return False
        if os.path.isfile(path):
            os.remove(path)
            return True
        elif os.path.isdir(path):
            os.rmdir(path)
            return True
        return False

    def create_new_calculation(self, parameter):
        new_id = Filename.new_calculation_filename()
        if self.contains_calculation(new_id):
            raise Exception("calculation {} already exists".format(new_id))

        calc_dir_path = self._get_calculation_directory_path(new_id)
        calc_dir = CalculationDirectory(calc_dir_path)
        calc_dir.init_directory()
        calc_dir.set_calculation_parameter(parameter)
        return calc_dir.get_id()


class Directory(IDirectory):
    def __init__(self, path=""):
        self._path = path
        if os.path.isfile(os.path.abspath(path)):
            raise Exception("can not create directory from file path")

    @property
    def full_path(self):
        return os.path.abspath(self._path)

    @property
    def name(self):
        return self.full_path.split("\\")[-1]

    def subdir(self, name):
        fullpath = os.path.join(self._path, name)
        if not os.path.exists(fullpath):
            os.makedirs(fullpath, 0o777)
        return Directory(fullpath)

    def list_files(self):
        return [item for item in os.listdir(self.full_path) if os.path.isfile(os.path.join(self.full_path, item))]

    def list_subdirs(self):
        return [item for item in os.listdir(self.full_path) if not os.path.isfile(os.path.join(self.full_path, item))]

    def list_files_recursively(self):
        files = []
        for path, _, file_names in os.walk(self.full_path):
            for file_name in file_names:
                files.append(os.path.join(path, file_name).replace(self.full_path, "").replace("\\", "/").lstrip("/"))
        return files

    def delete(self):
        for path, directory_names, file_names in os.walk(self.full_path, topdown=False):
            for directory_name in directory_names:
                os.rmdir(os.path.join(path, directory_name))
            for file_name in file_names:
                os.remove(os.path.join(path, file_name))
        os.rmdir(self.full_path)
        return True

    def delete_file(self, pattern):
        path_to_delete = os.path.join(self.full_path, pattern)
        os.remove(path_to_delete)

    def delete_empty_subdir(self, pattern):
        path_to_delete = os.path.join(self.full_path, pattern)
        os.rmdir(path_to_delete)

    def has_subdir(self, name):
        return os.path.exists(os.path.join(self._path, name))

    def contains(self, pattern):
        return os.path.exists(os.path.join(self.full_path, pattern))

    @contextmanager
    def open_file(self, name, mode):
        with open(self.get_path(name), mode) as handle:
            yield handle

    def get_path(self, relative_path):
        return os.path.join(self.full_path, relative_path)


@synchronized
class CalculationDirectory(Directory):
    CALCULATION_METADATA_FILE_NAME = "calculation_meta.json"
    RUN_METADATA_FILE_NAME = "run_meta.json"
    RESULTS_FILE_NAME = 'run_results.json'

    def __init__(self, calc_dir_path):
        Directory.__init__(self, calc_dir_path)
        self._calculation_id = self.name
        self._logger = getLogger(self.__class__.__name__)

    def init_directory(self):
        self._get_input_dir()
        self.get_status_file()
        self.set_calculation_parameter({})

    def get_id(self):
        return self._calculation_id

    def _get_input_dir(self):
        return self.subdir("input")

    def _get_last_run_output(self):
        last_run_dir = self.subdir(self.get_last_run())
        return last_run_dir.subdir("output")

    def list_input_files(self):
        return self._get_input_dir().list_files_recursively()

    def list_last_run_output_files(self):
        return self._get_last_run_output().list_files_recursively()

    def get_last_run_outputfile_path(self, relative_path):
        return os.path.join(self._get_last_run_output().full_path, relative_path)

    def get_status_file(self):
        return _CalculationStatus(self)

    def has_job_running(self, job_id):
        return job_id in self._get_jobs_file().list_job_ids()

    def _get_jobs_file(self):
        return _JobFile(self)

    def is_canceled(self):
        self._get_cancel_file().is_canceled()

    def set_canceled(self):
        self._get_cancel_file().set_canceled()

    def _get_cancel_file(self):
        return _CancelFile(self)

    def get_output_dir(self, run_id):
        return self.subdir(run_id).subdir("output")

    def update_status(self, status, message):
        self._status.update_status(status, message)

    def save_input_file(self, filename, data):
        input_dir = self.subdir("input")
        data.save(os.path.join(input_dir.full_path, filename))

    def prepare_run(self, run_details):
        if self._status.is_running:
            raise Exception("calculation is already running")

        self._get_cancel_file().delete()
        self._get_jobs_file().clear()
        run_id = Filename.new_run_filename()
        run_dir = self.get_run_dir(run_id)
        run_dir.subdir("output")

        self._write_run_details(run_id, run_details)
        self._status.set_last_run(run_id)

        return run_id

    def list_job_ids(self):
        return self._get_jobs_file().list_job_ids()

    def add_job_id(self, job_id):
        self._get_jobs_file().add(job_id)

    def remove_job_id(self, job_id):
        self._get_jobs_file().remove(job_id)

    def get_status(self):
        return self._status.to_dict()

    def get_last_run(self):
        return self._status.last_run()

    def is_running(self):
        return self._status.is_running

    @property
    def _status(self):
        status = _CalculationStatus(self)

        if status.last_run():
            with self.subdir(status.last_run()).open_file(self.RUN_METADATA_FILE_NAME, "r") as f:
                run_params = json.load(f)
        else:
            run_params = None

        status.set_run_parameters(run_params)
        status.set_input_files(self.subdir("input").list_files_recursively())

        if os.path.exists(self.get_path(self.CALCULATION_METADATA_FILE_NAME)):
            with self.open_file(self.CALCULATION_METADATA_FILE_NAME, "r") as f:
                calculation_prams = json.load(f)["parameters"]
        else:
            calculation_prams = '{}'
        status.set_calculation_parameters(calculation_prams)
        return status

    def read_last_run_result(self):
        last_run = self._status.last_run()
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

    def set_calculation_parameter(self, parameters):
        meta = {}
        if os.path.exists(self.get_path(self.CALCULATION_METADATA_FILE_NAME)):
            with self.open_file(self.CALCULATION_METADATA_FILE_NAME, "r") as meta_file:
                meta = json.load(meta_file)

        meta["parameters"] = parameters

        with self.open_file(self.CALCULATION_METADATA_FILE_NAME, "w") as meta_file:
            json.dump(meta, meta_file)

    def get_calculation_parameters(self):
        params = {}

        with self.open_file(self.CALCULATION_METADATA_FILE_NAME, "r") as calc_meta:
            for k, v in json.load(calc_meta)["parameters"].items():
                params[k] = v

        return params

    def _write_run_details(self, run_id, run_parameters):
        run_dir = self.get_run_dir(run_id)

        with run_dir.open_file(self.RUN_METADATA_FILE_NAME, "w") as f:
            json.dump(run_parameters, f)

    def get_run_parameters(self, run_id):
        run_dir = self.get_run_dir(run_id)

        params = {}
        with run_dir.open_file(self.RUN_METADATA_FILE_NAME, "r") as run_meta:
            json_content = json.load(run_meta)
            from_file = json.loads(json_content["parameters"])
            for k, v in from_file.items():
                params[k] = v

        return params

    def get_merged_parameters(self, run_id):
        calc_params = self.get_calculation_parameters()
        run_params = self.get_run_parameters(run_id)

        for k, v in run_params.items():
            calc_params[k] = v

        return calc_params

    def get_algorithm_type(self, run_id):
        run_dir = self.get_run_dir(run_id)
        with run_dir.open_file(self.RUN_METADATA_FILE_NAME, "r") as run_meta:
            return json.load(run_meta)["algorithm"]

    def get_run_dir(self, run_id):
        return self.subdir(run_id)

    def has_existing_run(self):
        return self.read_last_run_result() is not None

    def get_input_file_path(self, relative_path):
        return os.path.join(self._get_input_dir().full_path, relative_path)


class Status:
    CREATED = "Created"
    RUNNING = "Running"
    FINISHED = "Finished"
    CANCELED = "Canceled"
    FAILED = "Failed"


class _CalculationStatus(object):
    STATUS_FILE_NAME = ".status"

    LAST_RUN = "last_run"
    STATUS = "status"
    MESSAGE = "message"
    INPUT_FILES = "input_files"
    CALCULATION_PARAMETERS = "calculation_parameters"
    RUN_PARAMETERS = "run_parameters"

    DEFAULT = {
        LAST_RUN: None,
        STATUS: Status.CREATED,
        MESSAGE: ""
    }

    def __init__(self, calculation_directory):
        self.calculation_directory = calculation_directory

        if not calculation_directory.contains(self.STATUS_FILE_NAME):
            self._save(self.DEFAULT)

    def _load(self):
        with self.calculation_directory.open_file(self.STATUS_FILE_NAME, 'r') as status_file:
            return json.load(status_file)

    def _save(self, status):
        with self.calculation_directory.open_file(self.STATUS_FILE_NAME, 'w') as status_file:
            json.dump(status, status_file)

    def __str__(self):
        return str(self._load())

    def to_dict(self):
        return self._load()

    def set_last_run(self, run_id):
        def update(dic):
            dic[self.LAST_RUN] = run_id

        self._update_file(update)

    def last_run(self):
        content = self._load()
        return content[self.LAST_RUN]

    def update_status(self, status, message: str):
        def update(d):
            d[self.STATUS] = status
            d[self.MESSAGE] = message

        self._update_file(update)

    def _update_file(self, update_callback):
        status = self._load()
        update_callback(status)
        self._save(status)

    @property
    def is_running(self):
        return self._load()[self.STATUS] == Status.RUNNING

    @property
    def is_ready(self):
        return not self.is_running

    def set_calculation_parameters(self, calculation_prams):
        def update(d):
            json_parms = {"parameters": json.dumps(calculation_prams)}
            d[self.CALCULATION_PARAMETERS] = json_parms

        self._update_file(update)

    def set_run_parameters(self, run_params):
        def update(d):
            json_parms = None
            if run_params is not None:
                json_parms = {"parameters": json.dumps(run_params["parameters"])}
            d[self.RUN_PARAMETERS] = json_parms

        self._update_file(update)

    def set_input_files(self, input_files):
        def update(d):
            d[self.INPUT_FILES] = input_files

        self._update_file(update)


class JobStatus:
    WAITING = "waiting"
    RUNNING = "running"
    NOT_FOUND = "not found"


class _JobFile:
    JOBS_FILE_NAME = ".jobs"

    def clear(self):
        def update(l):
            l.clear()

        self._update_file(update)

    def list_job_ids(self):
        return self._load()

    def add(self, job_id):
        def add_job(jobs):
            jobs.append(job_id)

        self._update_file(add_job)

    def remove(self, job_id):
        def remove_job(jobs):
            if job_id in jobs:
                jobs.remove(job_id)

        self._update_file(remove_job)

    def __init__(self, calculation_directory):
        self.calculation_directory = calculation_directory
        if not self.calculation_directory.contains(self.JOBS_FILE_NAME):
            self._save(self._load())

    def _update_file(self, func):
        jobs = self._load()
        func(jobs)
        self._save(jobs)

    def _load(self):
        if self.calculation_directory.contains(self.JOBS_FILE_NAME):
            with self.calculation_directory.open_file(self.JOBS_FILE_NAME, "r") as jobs_file:
                return json.load(jobs_file)
        else:
            return []

    def _save(self, jobs):
        with self.calculation_directory.open_file(self.JOBS_FILE_NAME, "w") as jobs_file:
            json.dump(jobs, jobs_file)

    def __str__(self):
        return str(self._load())


class _CancelFile:
    CANCEL_FILE_NAME = ".cancel"

    def __init__(self, calculation_directory):
        self.calculation_directory = calculation_directory

    def _get(self):
        return self.calculation_directory.contains(self.CANCEL_FILE_NAME)

    def _set(self, value):
        if value:
            with self.calculation_directory.open_file(self.CANCEL_FILE_NAME, "w") as cancel_file:
                cancel_file.write("")
        else:
            self._del()

    def _del(self):
        if self.calculation_directory.contains(self.CANCEL_FILE_NAME):
            self.calculation_directory.delete_file(self.CANCEL_FILE_NAME)

    def is_canceled(self):
        return self._get()

    def set_canceled(self):
        self._set(True)

    def delete(self):
        self._del()

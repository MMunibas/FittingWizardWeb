import os
import random
import json
from datetime import datetime
from hashlib import sha256
from base64 import urlsafe_b64encode
from contextlib import contextmanager
from algorithms.toolkit import IDirectory
from toolkit import *
from .settings import STORAGE_ROOT

jobs_lock = threading.Lock()
cancel_lock = threading.Lock()


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


class Storage(metaclass=Singleton):
    def __init__(self):
        self._storage_root = Directory(STORAGE_ROOT)

    def set_root(self, path):
        if not os.path.exists(path):
            os.mkdir(path)
        self._storage_root = Directory(path)

    def get_calculation_directory(self, calculation_id):
        return self.root.subdir(calculation_id)

    def get_calculation_directory_path(self, calculation_id):
        return os.path.join(self.root.full_path, calculation_id)

    def contains_calculation(self, calculation_id):
        return self.root.has_subdir(calculation_id)

    def list_all_calculations(self):
        return self.root.list_subdirs()

    def get_status_file(self, calculation_id):
        return CalculationStatus(self.get_calculation_directory(calculation_id))

    def get_jobs_file(self, calculation_id):
        return JobFile(self.get_calculation_directory(calculation_id))

    def get_cancel_file(self, calculation_id):
        return CancelFile(self.get_calculation_directory(calculation_id))

    def get_output_dir(self, calculation, run):
        return self.get_calculation_directory(calculation).subdir(run).subdir("output")

    @property
    def root(self):
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
        with open(os.path.join(self._path, name), mode) as handle:
            yield handle


class Status:
    CREATED = "Created"
    RUNNING = "Running"
    FINISHED = "Finished"
    CANCELED = "Canceled"
    FAILED = "Failed"


class CalculationStatus(object):
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
        self._status_file_lock = threading.Lock()
        self.calculation_directory = calculation_directory

        if not calculation_directory.contains(self.STATUS_FILE_NAME):
            self._save(self.DEFAULT)

    def _load(self):
        with self._status_file_lock:
            with self.calculation_directory.open_file(self.STATUS_FILE_NAME, 'r') as status_file:
                return json.load(status_file)

    def _save(self, status):
        with self._status_file_lock:
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
        try:
            return self._load()[self.LAST_RUN]
        except:
            return None

    def update_status(self, status, message: str):
        def update(d):
            d[self.STATUS] = status
            d[self.MESSAGE] = message

        self._update_file(update)

    def _update_file(self, update_callback):
        try:
            status = self._load()
            update_callback(status)
            self._save(status)
        except Exception as ex:
            print(ex)

    @property
    def is_running(self):
        return self._load()[self.STATUS] == Status.RUNNING

    @property
    def is_ready(self):
        return not self.is_running

    def set_calculation_parameters(self, calculation_prams):
        def update(d):
            calculation_prams["parameters"] = json.dumps(calculation_prams["parameters"])
            d[self.CALCULATION_PARAMETERS] = calculation_prams
        self._update_file(update)

    def set_run_parameters(self, run_params):
        def update(d):
            if run_params is not None:
                run_params["parameters"] = json.dumps(run_params["parameters"])
                pass
            d[self.RUN_PARAMETERS] = run_params

        self._update_file(update)

    def set_input_files(self, input_files):
        def update(d):
            d[self.INPUT_FILES] = input_files
        self._update_file(update)


class JobStatus:
    WAITING = "waiting"
    RUNNING = "running"
    NOT_FOUND = "not found"


class JobFile:
    JOBS_FILE_NAME = ".jobs"

    def clear(self):
        def update(l):
            l.clear()
        self._update_file(update)

    def list(self):
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

    @synchronize_with(jobs_lock)
    def _load(self):
        if self.calculation_directory.contains(self.JOBS_FILE_NAME):
            with self.calculation_directory.open_file(self.JOBS_FILE_NAME, "r") as jobs_file:
                return json.load(jobs_file)
        else:
            return []

    @synchronize_with(jobs_lock)
    def _save(self, jobs):
        with self.calculation_directory.open_file(self.JOBS_FILE_NAME, "w") as jobs_file:
            json.dump(jobs, jobs_file)

    def __str__(self):
        return str(self._load())


class CancelFile:
    CANCEL_FILE_NAME = ".cancel"

    def __init__(self, calculation_directory):
        self.calculation_directory = calculation_directory

    @synchronize_with(cancel_lock)
    def _get(self):
        return self.calculation_directory.contains(self.CANCEL_FILE_NAME)

    @synchronize_with(cancel_lock)
    def _set(self, value):
        if value:
            with self.calculation_directory.open_file(self.CANCEL_FILE_NAME, "w") as cancel_file:
                cancel_file.write("")
        else:
            self._del()

    @synchronize_with(cancel_lock)
    def _del(self):
        if self.calculation_directory.contains(self.CANCEL_FILE_NAME):
            self.calculation_directory.delete_file(self.CANCEL_FILE_NAME)

    @property
    def is_set(self):
        return self._get()

    @is_set.setter
    def is_set(self, value):
        self._set(value)

    @is_set.deleter
    def is_set(self):
        self._del()

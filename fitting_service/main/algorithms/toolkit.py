from abc import ABCMeta, abstractmethod
from contextlib import contextmanager


class IContext(metaclass=ABCMeta):
    @property
    @abstractmethod
    def log(self):
        """ log message:
        ctx.log.<level>("message")
         where <level> is one of those values: [debug, info, warning, error] """
        pass

    @property
    @abstractmethod
    def input_dir(self):
        """ get input directory: See IDirectory for available methods. """
        pass

    @property
    @abstractmethod
    def output_dir(self):
        """ get output directory: See IDirectory for available methods. """
        pass

    @property
    @abstractmethod
    def parameters(self):
        """ get input parameters """
        pass

    @abstractmethod
    def update_status(self, status, message):
        """ update calculation status """
        pass

    @abstractmethod
    def terminate_if_canceled(self):
        """ terminate execution if cancel was requested """
        pass

    @abstractmethod
    def schedule_job(self, command):
        """ schedule a new job """
        pass

    @abstractmethod
    def job_status(self, job_id):
        """ get the current status of a job """
        pass

    @abstractmethod
    def wait_for_finished_jobs(self, *jobs):
        """ wait for the listed jobs to finish """
        pass

    @abstractmethod
    def wait_for_all_jobs(self):
        """ wait for all running jobs to finish """
        pass

    @property
    @abstractmethod
    def jobs(self):
        """ list all running job ids """
        pass


class IDirectory(metaclass=ABCMeta):
    @property
    @abstractmethod
    def full_path(self):
        """ get the absolute path of the directory. """
        pass

    @property
    @abstractmethod
    def name(self):
        """ get the name of the directory. """
        pass

    @abstractmethod
    def subdir(self, name):
        """ get a subdirectory. The directory will be created if it does not exist. """
        pass

    @abstractmethod
    def list_files(self):
        """ list files inside the directory """
        pass

    @abstractmethod
    def list_subdirs(self):
        """ list subdirectories """
        pass

    @abstractmethod
    def list_files_recursively(self):
        """ list all files recursively """
        pass

    @abstractmethod
    def delete(self):
        """ delete the directory recursively. all content will be deleted! """
        pass

    @abstractmethod
    def delete_file(self, pattern):
        """ delete a file inside the directory """
        pass

    @abstractmethod
    def delete_empty_subdir(self, pattern):
        """ delete a empty subdirectory """
        pass

    @abstractmethod
    def has_subdir(self, name):
        """ check if a subdirectory exists """
        pass

    @abstractmethod
    def contains(self, pattern):
        """ check if the directory contains a file or subdirwith given pattern """
        pass

    @contextmanager
    @abstractmethod
    def open_file(self, name, mode):
        """
        open a file handle.
        name: the name of the file to open
        mode: file mode, use "r" for read, "w" for write. full list of options: https://stackoverflow.com/a/23566951
        """
        pass


def register(func):
    """ this decorator is used to register a algorithm. use '@register' to decorate a function. """
    import inspect
    if len(inspect.signature(func).parameters) != 1:
        raise Exception("Algorithms have to have exactly one parameter: {}".format(func.__name__))
    func.__is_algorithm__ = True
    return func


def input_validator(algo):
    """ this decorator is used to register a input validator for one or multiple algorithms.
    use '@input_validator(<algorithm name>)' to decorate a function. """
    import inspect

    def func(fun):
        if len(inspect.signature(func).parameters) != 1:
            raise Exception("Input validators have to have exactly one parameter: {}".format(fun.__name__))
        if not hasattr(fun, "__input_validator_for__"):
            fun.__input_validator_for__ = []
        fun.__input_validator_for__ += [algo]
        return fun
    return func

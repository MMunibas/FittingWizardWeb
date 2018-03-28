from toolkit import *


class AlgorithmRegistry(metaclass=Singleton):
    def __init__(self):
        self.algorithms = {}

    def list_algorithms(self):
        return list(self.algorithms.keys())

    def get_version_info(self):
        return ["".format(name, func.__version__)
                if hasattr(func, "__version__")
                else name
                for name, func in self.algorithms.items()]


class Algorithm(object):
    can_validate_input = False
    can_validate_output = False

    def __init__(self, execute, input_validator=None, output_validator=None):
        self.execute = execute
        self.__name__ = execute.__name__
        if input_validator is not None:
            self.input_validator = input_validator
        if output_validator is not None:
            self.output_validator = output_validator

    def __get__(self, obj, obj_type=None):
        if obj is None:
            return self
        if self.execute is None:
            raise Exception("execute func not set")
        return self.execute

    def input_validator(self, func):
        self.can_validate_input = True
        self.input_validator = func

    def output_validator(self, func):
        self.can_validate_output = True
        self.output_validator = func


def register_algorithm(func):
    func = Algorithm(func)
    AlgorithmRegistry().algorithms[func.__name__] = func
    return func

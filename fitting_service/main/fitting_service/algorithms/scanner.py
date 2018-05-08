import logging
import pkgutil
import algorithms

ALGORITHM_PACKAGE = algorithms

LOGGER = logging.getLogger(__name__)

class Scanner:
    def _analyze(self, package):
        submodules = []
        for importer, modname, is_package in pkgutil.iter_modules(package.__path__, package.__name__ + "."):
            submodules.append([importer, modname, is_package, __import__(modname, fromlist="dummy")])
        return submodules

    def _scan(self, package, meta_tag="__is_algorithm__"):
        algos = {}
        packages_to_analyze = [package]
        while len(packages_to_analyze) > 0:
            package = packages_to_analyze[0]
            del packages_to_analyze[0]
            sm = self._analyze(package)

            for importer, modname, is_package, the_module in sm:
                if is_package:
                    packages_to_analyze.append(the_module)
                else:
                    for element in dir(the_module):
                        if meta_tag in dir(getattr(the_module, element)):
                            algo = (getattr(the_module, element))
                            algos[algo.__name__] = algo
        return algos

    @staticmethod
    def find_algorithms():
        algos = Scanner()._scan(ALGORITHM_PACKAGE)
        algos = {k: Algorithm(v) for k, v in algos.items()}
        validators = Scanner()._scan(algorithms, "__input_validator_for__")

        for validator in validators:
            for algo in validators[validator].__input_validator_for__:
                algos[algo].validators += [validators[validator]]
        return algos

    @staticmethod
    def has_algorithm(name):
        algos = Scanner.find_algorithms()
        return name in algos

    @staticmethod
    def find_algorithm(name):
        algos = Scanner.find_algorithms()
        if name not in algos:
            raise Exception("Algorithm '{}' not found".format(name))
        return algos[name]

    @staticmethod
    def load_algorithms():
        LOGGER.info('load_algorithms')
        algos = Scanner.find_algorithms()
        for name in algos:
            LOGGER.info('found: ' + name)
        LOGGER.info('load_algorithms DONE')


class InvalidInputException(Exception):
    def __init__(self, validator_name):
        self.validator_name = validator_name
        super().__init__("Input validation failed: {}".format(validator_name))


class Algorithm:
    def __init__(self, func):
        self.name = func.__name__
        self.func = func
        self.validators = []

    def __call__(self, ctx):
        return self.func(ctx)

    def validate_input(self, ctx):
        for validator in self.validators:
            if not validator(ctx):
                raise InvalidInputException(validator.__name__)


if __name__ == "__main__":
    algos = Scanner.find_algorithms()
    print("Algorithms:")

    for algo in algos:
        print("    {}".format(algo))
        if algos[algo].validators:
            for validator in algos[algo].validators:
                print("        {}".format(validator.__name__))

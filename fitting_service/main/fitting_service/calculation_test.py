import os
import json
import logging
from .calculation import CalculationService
from .file_acces import StorageService
from .job import JobsService, JobStatusUpdater


class CalculationTest(object):
    def __init__(self):
        self.run_parameter = {}
        self.algorithm_type = None
        self.calc_id = None
        self.calc_dir = None
        self.run_id = None
        self.run_dir = None
        self.logger = logging.getLogger(self.__class__.__name__)
        StorageService().initialize()
        StorageService().set_root("../data")

    def execute(self):
        self.calc_dir.update_status("created", "")

        run_details = {
            "algorithm": self.algorithm_type,
            "parameters": self.run_parameter
        }
        CalculationService().run_calculation(self.calc_id,
                                             run_details,
                                             blocking=True)

    def use_calculation_data(self, calc_id):
        self.calc_id = calc_id
        self.calc_dir = CalculationService().get_calculation_directory(self.calc_id)

        loaded_params = self.calc_dir.get_calculation_parameters()
        for k, v in loaded_params:
            self.run_parameter[k] = v

    def set_calculation_params(self, params):
        details = {"parameters": params}
        self.calc_id = CalculationService().create_new_calculation(details)
        print(self.calc_id)
        self.calc_dir = CalculationService().get_calculation_directory(self.calc_id)
        for k, v in params.items():
            self.run_parameter[k] = v

    def set_run_params(self, params):
        for k, v in params.items():
            self.run_parameter[k] = v

    def set_algorithm(self, algorithm):
        self.algorithm_type = algorithm

    def add_input_file(self, absolute_path):
        self.logger.info("copying file from {} to {}".format(absolute_path,
                                                             os.path.join(self.calc_dir.subdir("input").full_path,
                                                                          os.path.split(absolute_path)[-1])))
        with open(absolute_path, "rb") as remote_file:
            with self.calc_dir.subdir("input").open_file(os.path.split(absolute_path)[-1], "wb") as local_file:
                local_file.write(remote_file.read())

    def use_last_run_params(self):
        self.run_id = self.calc_dir._status.last_run()
        if not self.run_id:
            raise Exception("no last run found")

        last_run_params = self.calc_dir.get_run_parameters(self.run_id)

        for k, v in last_run_params:
            self.run_parameter[k] = v

    def read_last_run_results(self):
        if self.calc_id is None:
            raise Exception('calc_id not set')

        return CalculationService().read_last_run_result(self.calc_id)

    @staticmethod
    def shutdown():
        JobStatusUpdater().terminate()
        JobsService().stop_cluster_simulation()

import os
import json
import logging
from .calculation import CalculationService
from .file_acces import Storage
from .job import JobsService, JobStatusUpdater
from .settings import CALCULATION_METADATA_FILE_NAME, RUN_METADATA_FILE_NAME


class CalculationTest(object):
    def __init__(self):
        self.parameters = {}
        self.algorithm = None
        self.calc_id = None
        self.calc_dir = None
        self.run_id = None
        self.run_dir = None
        self.logger = logging.getLogger(self.__class__.__name__)
        Storage().initialize()
        Storage().set_root("../data")

    def execute(self):
        self.calc_dir.status.update_status("created", "")
        CalculationService().run_calculation(self.calc_id,
                                             {
                                                 "algorithm": self.algorithm,
                                                 "parameters": self.parameters
                                             },
                                             blocking=True)

    def use_calculation_data(self, calc_id):
        self.calc_id = calc_id
        self.calc_dir = CalculationService().get_by_id(self.calc_id)
        with self.calc_dir.open_file(CALCULATION_METADATA_FILE_NAME, "r") as calc_meta:
            for k, v in json.load(calc_meta)["parameters"].items():
                self.parameters[k] = v

    def set_calculation_params(self, params):
        self.calc_id = CalculationService().create_new_calculation({"parameters": params})["calculation"]
        print(self.calc_id)
        self.calc_dir = CalculationService().get_by_id(self.calc_id)
        for k,v in params.items():
            self.parameters[k] = v

    def set_run_params(self, params):
        for k, v in params.items():
            self.parameters[k] = v

    def set_algorithm(self, algorithm):
        self.algorithm = algorithm

    def add_input_file(self, absolute_path):
        self.logger.info("copying file from {} to {}".format(absolute_path, os.path.join(self.calc_dir.subdir("input").full_path, os.path.split(absolute_path)[-1])))
        with open(absolute_path, "rb") as remote_file:
            with self.calc_dir.subdir("input").open_file(os.path.split(absolute_path)[-1], "wb") as local_file:
                local_file.write(remote_file.read())

    def use_last_run_params(self):
        self.run_id = self.calc_dir.status.last_run()
        if not self.run_id:
            raise Exception("no last run found")

        self.run_dir = self.calc_dir.subdir(self.run_id)

        with self.run_dir.open_file(RUN_METADATA_FILE_NAME, "r") as run_meta:
            meta = json.load(run_meta)
            self.algorithm = meta["algorithm"]
            for k, v in meta["parameters"].items():
                self.parameters[k] = v

    def read_last_run_results(self):
        if self.calc_id is None:
            raise Exception('calc_id not set')

        return CalculationService().read_last_run_result(self.calc_id)

    @staticmethod
    def shutdown():
        JobStatusUpdater().terminate()
        JobsService().stop_cluster_simulation()

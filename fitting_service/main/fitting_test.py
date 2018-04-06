from fitting_service.calculation_test import CalculationTest


def run_new_dummy_algorithm():
    test_setup = CalculationTest()

    test_setup.set_algorithm("dummy_algorithm")
    test_setup.logger.info("run_new_dummy_algorithm")
    test_setup.set_calculation_params({"calc_param1": "value1", "calc_param2": "value2"})
    test_setup.add_input_file("../data/somefile.json")

    test_setup.set_run_params({"run_param1": "value1", "run_param2": "value2"})

    test_setup.execute()
    test_setup.logger.info("run_new_dummy_algorithm - done")


def run_existing_dummy_algorithm():
    test_setup = CalculationTest()
    test_setup.logger.info("run_existing_dummy_algorithm")

    test_setup.use_calculation_data("2018-04-06_16-34-23-682637_fzeys")
    test_setup.use_last_run_params()
    
    test_setup.set_run_params({"run_param1": "value42"})

    test_setup.execute()
    test_setup.logger.info("run_existing_dummy_algorithm - done")


if __name__ == '__main__':
    run_new_dummy_algorithm()
    run_existing_dummy_algorithm()
    CalculationTest.shutdown()

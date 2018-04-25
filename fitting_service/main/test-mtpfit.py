#!/home/wfit/bin/python-3.6.5-env/bin/python3

from fitting_service.calculation_test import CalculationTest


def run_new_dummy_algorithm():

    job_path = "/home/wfit/FittingWizardWeb/fitting_service/data/mike-test-mtp/"

    test_setup = CalculationTest()
    test_setup.set_algorithm("mtpfit")
    test_setup.logger.info("run mtpfit algorithm")

    test_setup.set_calculation_params({"xyz": "nma.xyz", "charge": "0", "multiplicity": "1",
           "cmd" : "MP2/aug-cc-PVDZ nosymm", "ncore" : "8"})

    test_setup.add_input_file(job_path + "nma.xyz")

    test_setup.logger.info("created new calculation with id "+test_setup.calc_id)

    test_setup.execute()

    test_setup.logger.info("run_new_dummy_algorithm - done")
    return(test_setup.calc_id)

def run_existing_dummy_algorithm(calc):
    test_setup = CalculationTest()
    test_setup.logger.info("run existing algorithm "+calc)

    test_setup.use_calculation_data(calc)
    test_setup.use_last_run_params()
    
    test_setup.execute()
    test_setup.logger.info("run_existing_dummy_algorithm - done")


if __name__ == '__main__':
    calc = run_new_dummy_algorithm()
#    run_existing_dummy_algorithm(calc)
CalculationTest.shutdown()


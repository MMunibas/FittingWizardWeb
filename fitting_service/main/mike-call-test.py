#!/home/wfit/bin/python-3.6.5-env/bin/python3

from fitting_service.calculation_test import CalculationTest


def run_new_dummy_algorithm():

    job_path = "/home/wfit/FittingWizardWeb/fitting_service/data/mike-test/"

    test_setup = CalculationTest()
    test_setup.set_algorithm("mike_test")
    test_setup.logger.info("run mike_test algorithm")

    test_setup.set_calculation_params({"top": "tot.top", "slu": "mol.pdb", "slv": "solv.pdb",
           "par" : "param.par", "lpun" : "mol.lpun", "pureliq" : "pureliquid.pdb", "lmb0" : 0.0, "lmb1" : 1.0,
           "T" : 298.3})

    test_setup.add_input_file(job_path + "param.par")
    test_setup.add_input_file(job_path + "liq.pdb")
    test_setup.add_input_file(job_path + "mol.lpun")
    test_setup.add_input_file(job_path + "mol.pdb")
    test_setup.add_input_file(job_path + "tot.top")
    test_setup.add_input_file(job_path + "solv.pdb")
    test_setup.add_input_file(job_path + "pureliquid.pdb")

    test_setup.set_run_params({"dlmb": 0.1, "epsfac": 1.0, "sigfac": 1.0})
    test_setup.logger.info("created new calculation with id "+test_setup.calc_id)

    test_setup.execute()

    test_setup.logger.info("run_new_dummy_algorithm - done")
    return(test_setup.calc_id)

def run_existing_dummy_algorithm(calc):
    test_setup = CalculationTest()
    test_setup.logger.info("run existing algorithm "+calc)

    test_setup.use_calculation_data(calc)
    test_setup.use_last_run_params()
    
    test_setup.set_run_params({"dlmb": 0.05, "epsfac": 1.05, "sigfac": 0.95})

    test_setup.execute()
    test_setup.logger.info("run_existing_dummy_algorithm - done")


if __name__ == '__main__':
    calc = run_new_dummy_algorithm()
#    run_existing_dummy_algorithm(calc)
CalculationTest.shutdown()


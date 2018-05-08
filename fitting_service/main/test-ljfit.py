#!/home/wfit/bin/python-3.6.5-env/bin/python3

from fitting_service.calculation_test import CalculationTest


def run_new_ljfit():

    job_path = "/home/wfit/FittingWizardWeb/fitting_service/data/mike-test-lj/"

    ljfit_setup = CalculationTest()
    ljfit_setup.set_algorithm("ljfit")
    ljfit_setup.logger.info("run ljfit algorithm")

    ljfit_setup.set_calculation_params({"lj_filename_charmm_topology": "tot.top", "lj_filename_solute_pdb": "mol.pdb", "lj_filename_solvent_pdb": "solv.pdb",
           "lj_filename_charmm_parameter" : "param.par", "lj_filename_mtpl_lpun" : "mol.lpun", "lj_filename_pureliquid_pdb" : "pureliquid.pdb", "lj_ti_lambda_0" : 0.0, "lj_ti_lambda_1" : 1.0,
           "lj_charmm_simulation_temperature" : 298.3})

    ljfit_setup.add_input_file(job_path + "param.par")
    ljfit_setup.add_input_file(job_path + "mol.lpun")
    ljfit_setup.add_input_file(job_path + "mol.pdb")
    ljfit_setup.add_input_file(job_path + "tot.top")
    ljfit_setup.add_input_file(job_path + "solv.pdb")
    ljfit_setup.add_input_file(job_path + "pureliquid.pdb")

    ljfit_setup.set_run_params({"lj_ti_lambda_window_size_electrostatic": 0.1, "lj_ti_lambda_window_size_vdw": 0.1, "lj_scaling_factor_eps": 1.0, "lj_scaling_factor_sig": 1.0})
    ljfit_setup.logger.info("created new calculation with id "+ljfit_setup.calc_id)

    ljfit_setup.execute()

    ljfit_setup.logger.info("run_new_dummy_algorithm - done")
    return(ljfit_setup.calc_id)

def run_existing_ljfit(calc):
    ljfit_setup = CalculationTest()
    ljfit_setup.logger.info("run existing algorithm "+calc)

    ljfit_setup.use_calculation_data(calc)
    ljfit_setup.use_last_run_params()
    
    ljfit_setup.set_run_params({"dlmb": 0.05, "epsfac": 1.05, "sigfac": 0.95})

    ljfit_setup.execute()
    ljfit_setup.logger.info("run_existing_dummy_algorithm - done")


if __name__ == '__main__':
    calc = run_new_ljfit()
#    run_existing_ljfit(calc)
CalculationTest.shutdown()


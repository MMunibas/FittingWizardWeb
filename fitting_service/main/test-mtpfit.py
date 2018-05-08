#!/home/wfit/bin/python-3.6.5-env/bin/python3 

from fitting_service.calculation_test import CalculationTest


def run_mtp_fit_part1():

    job_path = "/home/wfit/FittingWizardWeb/fitting_service/data/mike-test-mtp/"

    mtp_setup1 = CalculationTest()
    mtp_setup1.set_algorithm("mtpfit_part1")
    mtp_setup1.logger.info("run mtpfit algorithm")

    mtp_setup1.set_calculation_params({"mtp_gen_filename_xyz": "nma.xyz", "mtp_gen_molecule_charge": "0"})

    mtp_setup1.add_input_file(job_path + "nma.xyz")
    mtp_setup1.set_run_params({"mtp_gen_molecule_multiplicity": "1",
              "mtp_gen_gaussian_input_commandline" : "MP2/aug-cc-PVDZ nosymm", "mtp_gen_gaussian_num_cores" : "8"})
    mtp_setup1.logger.info("created new calculation with id "+mtp_setup1.calc_id)

    mtp_setup1.execute()
    mtp_setup1.logger.info("run_mtp_fit_part1 - done")
    return(mtp_setup1)

def run_mtp_fit_part2(mtp_setup2):

# /usr/local/fitting-web-1.2.0/scripts/fit.mtp.py -rank 0 -l generated_charges.txt -o fit_results.txt -pen 0.1 -hyd ../../molecule/p-clphoh/p-clphoh_mtpfittab.txt

    job_path = "/home/wfit/FittingWizardWeb/fitting_service/data/mike-test-mtp/output/"

    mtp_setup2.set_algorithm("mtpfit_part2")
    mtp_setup2.logger.info("Launch stage 2 of fit")

    mtp_setup2.set_run_params({"mtp_fitting_threshold": 0.1, "mtp_fitting_rank": 1, "mtp_fitting_flag_ignore_H": True, "mtp_fitting_charge_filename": "generated_charges.txt",
           "mtp_fitting_table_filename": job_path+"nma.pot_mtpfittab.txt", "mtp_fit_number": 0})
    mtp_setup2.add_input_file(job_path + "../generated_charges.txt")
    mtp_setup2.logger.info("created new calculation with id "+mtp_setup2.calc_id)

    mtp_setup2.execute()
    mtp_setup2.logger.info("run_mtp_fit_part2 - done")

def run_existing_dummy_algorithm(calc):
    mtp_setup = CalculationTest()
    mtp_setup.logger.info("run fit with new criteria using existing data and files for calculation id "+calc)



if __name__ == '__main__':
    calc = run_mtp_fit_part1()
    run_mtp_fit_part2(calc)

CalculationTest.shutdown()


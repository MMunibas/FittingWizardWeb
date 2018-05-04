#!/home/wfit/bin/python-3.6.5-env/bin/python3 

from fitting_service.calculation_test import CalculationTest


def run_mtp_fit_part1():

    job_path = "/home/wfit/FittingWizardWeb/fitting_service/data/mike-test-mtp/"

    mtp_setup1 = CalculationTest()
    mtp_setup1.set_algorithm("mtpfit_part1")
    mtp_setup1.logger.info("run mtpfit algorithm")

    mtp_setup1.set_calculation_params({"filename_xyz": "nma.xyz", "molecule_charge": "0", "molecule_multiplicity": "1",
           "gaussian_input_commandline" : "MP2/aug-cc-PVDZ nosymm", "gaussian_num_cores" : "8"})

    mtp_setup1.add_input_file(job_path + "nma.xyz")

    mtp_setup1.logger.info("created new calculation with id "+mtp_setup1.calc_id)

    mtp_setup1.execute()

    mtp_setup1.logger.info("run_mtp_fit_part1 - done")
    return(mtp_setup1.calc_id)

def run_mtp_fit_part2():

# /usr/local/fitting-web-1.2.0/scripts/fit.mtp.py -rank 0 -l generated_charges.txt -o fit_results.txt -pen 0.1 -hyd ../../molecule/p-clphoh/p-clphoh_mtpfittab.txt

    job_path = "/home/wfit/FittingWizardWeb/fitting_service/data/mike-test-mtp/output/"

    mtp_setup2 = CalculationTest()
    mtp_setup2.set_algorithm("mtpfit_part2")
    mtp_setup2.logger.info("Launch stage 2 of fit")

    mtp_setup2.set_calculation_params({"tabfile": job_path+"nma.pot_mtpfittab.txt"})
    mtp_setup2.set_run_params({"threshold": 0.1, "rank": 1, "ignore_H": True, "chgfile": "generated_charges.txt",
           "outfile": "fit.out"})
    mtp_setup2.add_input_file(job_path + "generated_charges.txt")

    mtp_setup2.logger.info("created new calculation with id "+mtp_setup2.calc_id)

    mtp_setup2.execute()

    mtp_setup2.logger.info("run_mtp_fit_part2 - done")

def run_existing_dummy_algorithm(calc):
    mtp_setup = CalculationTest()
    mtp_setup.logger.info("run fit with new criteria using existing data and files for calculation id "+calc)



if __name__ == '__main__':
    calc_id = run_mtp_fit_part1()
#    run_mtp_fit_part2()

CalculationTest.shutdown()


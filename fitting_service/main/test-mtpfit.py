from fitting_service.calculation_test import CalculationTest


def run_mtp_fit_part1():

    job_path = "/data/wfit/FittingWizardWeb/fitting_service/testdata/mtp-co/"

    mtp_setup1 = CalculationTest()
    mtp_setup1.set_algorithm("mtpfit_part1")
    mtp_setup1.logger.info("run mtpfit algorithm")

    xyzfile="co.xyz"
    mtp_setup1.set_calculation_params({"mtp_gen_filename_xyz": xyzfile, "mtp_gen_molecule_charge": "0"})

    mtp_setup1.add_input_file(job_path + xyzfile)
    mtp_setup1.set_run_params({"mtp_gen_molecule_multiplicity": "1","mtp_gen_charge_filename":"atomic_charges.json"
             ,"mtp_gen_gaussian_input_commandline" : "MP2/aug-cc-PVDZ nosymm", "mtp_gen_gaussian_num_cores" : "8"})
    mtp_setup1.logger.info("created new calculation with id "+mtp_setup1.calc_id)

    mtp_setup1.execute()
    mtp_setup1.logger.info("run_mtp_fit_part1 - done")
    return(mtp_setup1)

def run_mtp_fit_part2(mtp_setup2, rank, ignoreH):

    mtp_setup2.set_algorithm("mtpfit_part2")
    mtp_setup2.logger.info("Launch stage 2 of fit")

    all_results = mtp_setup2.read_last_run_results()
    charges = all_results["mtp_fit_results"]

    mtp_setup2.set_run_params({"mtp_fitting_threshold": 0.1, "mtp_fitting_rank": rank, "mtp_fitting_flag_ignore_H": ignoreH, "mtp_fitting_initial_charges": charges,
           "mtp_fitting_table_filename": "mtpfittab.txt", "mtp_fit_number": 0})
#    mtp_setup2.add_input_file(mtp_setup2.input_dir.subdir("../output/").full_path + "/atomic_charges.json")
    mtp_setup2.logger.info("created new fit for calculation id "+mtp_setup2.calc_id)

    mtp_setup2.execute()
    mtp_setup2.logger.info("run_mtp_fit_part2 - done")

def run_existing_dummy_algorithm(calc):
    mtp_setup = CalculationTest()
    mtp_setup.logger.info("run fit with new criteria using existing data and files for calculation id "+calc)



if __name__ == '__main__':
    calc = run_mtp_fit_part1()
    run_mtp_fit_part2(calc, 2, False)
#    run_mtp_fit_part2(calc, 1, True)

CalculationTest.shutdown()


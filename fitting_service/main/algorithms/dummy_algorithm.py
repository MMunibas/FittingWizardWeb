from .toolkit import *


@register
def dummy_algorithm(context):
    context.log.info("starting dummy algo")
    context.update_status("Running", "Step 0 | Preparing")

    """ Parameter input """
    # ctx.parameters is a python dictionary containing all parameters the calculation and run are initialized with.
    # if the same key exists in both dictionaries, the calculation parameter will be overwritten by the run parameters
    context.log.debug("input parameters:")
    for parameter, value in context.parameters.items():
        context.log.debug("input parameter {} is set to {}".format(parameter, value))
    try:
        # this will fail if no parameter named 'y' was defined in calculationParameters or runParameters.
        # If you checked the parameters using input validators this approach is safe to use.
        y = float(context.parameters["y"])
        context.log.info("y = {}".format(y))
        context.log.info("y² = {}".format(y**2))
    except:
        pass
    # another safe way to access parameters, is to define a default value which will be used if the parameter was not defined.
    default_x_value = 13.
    x = float(context.parameters.get("x", default_x_value))
    context.log.info("x = {}".format(x))
    context.log.info("x² = {}".format(x**2))

    default_input_file_name = "somefile.json"
    input_file_name = str(context.parameters.get("input_file_name", default_input_file_name))

    """ File IO """
    """ List files: """
    context.log.info("Input files:\n\t{}".format("\n\t".join(context.input_dir.list_files_recursively())))
    """ Read files: """
    # files uploaded using the api will automatically land inside the input directory of the calculation.
    # just like the default python 'open', context.input_dir.open_file can use the 'with' keyword to automatically
    # close the file handle when exiting the scope.
    with context.input_dir.open_file(input_file_name, "r") as input_data:
        input_value = "\n".join(input_data.readlines())
        context.log.info("input: {}".format(input_value))

    """ Write files: """
    # writing output files is
    with context.output_dir.open_file("somedata.log", "w") as somedata:
        somedata.write("success")
    """ Job scheduling """
    totalSteps = 4
    for i in range(totalSteps):
        context.update_status("Running", "Step 1 | Part {i} of {n}".format(i=i, n=totalSteps))
        context.log.info("------------------------------")
        # use this command where it's safe to cancel the calculation.
        context.terminate_if_canceled()

        # this is used to test error handling, not used in real algorithms
        if context.output_dir.contains(".exception"):
            raise Exception("Simulated crash due to '.exception' file in output directory")

        # scheduling a job is as easy as context.schedule_job(command).
        # if the actual command lives inside a shell script use
        # context.schedule_job(context.input_dir.full_path+"/relative/path/to/script.sh")
        running_job_id = context.schedule_job("sleep 4")
        waiting_job_id = context.schedule_job("sleep 3")

        _get_and_log_job_status(context, "asdf", "not found")
        # to wait for individual jobs to finish, use
        # context.wait_for_finished_jobs("job_id_1", "job_id_2",...,"job_id_n")
        # this will wait for any number of jobs listed as parameters
        context.wait_for_finished_jobs("asdf")
        _get_and_log_job_status(context, "asdf", "not found")

        _get_and_log_job_status(context, running_job_id, "waiting")
        _get_and_log_job_status(context, waiting_job_id, "waiting")

        # to wait for all jobs to finish, use
        context.wait_for_all_jobs()

        _get_and_log_job_status(context, running_job_id, "not found")
        _get_and_log_job_status(context, waiting_job_id, "not found")
        context.log.info("==============================")

    # schedule as many jobs an needed, store ids in a list, wait for all jobs in list
    jobs_to_schedule = 10
    context.log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    job_ids = []
    for i in range(jobs_to_schedule):
        context.update_status("Running", "Step 2 | Part {i} of {n}".format(i=i, n=jobs_to_schedule))
        job_id = context.schedule_job("sleep 1")
        job_ids.append(job_id)
    context.wait_for_finished_jobs(*job_ids)
    context.log.info("==============================")
    # or
    # schedule as many jobs an needed, wait for all jobs
    for i in range(jobs_to_schedule):
        context.update_status("Running", "Step 3 | Part {i} of {n}".format(i=i, n=jobs_to_schedule))
        context.schedule_job("sleep 1")
    context.wait_for_all_jobs()

    context.log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    context.update_status("Running", "Step 4 | Finished")
    context.log.info("dummy algo finished")


def _get_and_log_job_status(ctx, job_id, expected_status):
        job_status = ctx.job_status(job_id)
        ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))

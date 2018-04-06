from .toolkit import *


@register
def dummy_algorithm(ctx):
    ctx.log.info("starting dummy algo")
    try:
        with ctx.input_dir.open_file("somefile.json", "r") as input_data:
            input_data = "\n".join(input_data.readlines())
            ctx.log.info("input: {}".format(input_data))
    except:
        pass

    for _ in range(4):
        ctx.log.info("------------------------------")
        ctx.terminate_if_canceled()

        if ctx.output_dir.contains(".exception"):
            raise Exception("Simulated crash due to '.exception' file in output directory")

        running_job_id = ctx.schedule_job("sleep 4")
        waiting_job_id = ctx.schedule_job("sleep 3")

        _get_and_log_job_status(ctx, "asdf", "not found")
        ctx.wait_for_finished_jobs("asdf")
        _get_and_log_job_status(ctx, "asdf", "not found")

        _get_and_log_job_status(ctx, running_job_id, "waiting")
        _get_and_log_job_status(ctx, waiting_job_id, "waiting")

        ctx.wait_for_all_jobs()

        _get_and_log_job_status(ctx, running_job_id, "not found")
        _get_and_log_job_status(ctx, waiting_job_id, "not found")
        ctx.log.info("==============================")

    with ctx.output_dir.open_file("somedata.log", "w") as somedata:
        somedata.write("success")
    ctx.log.info("dummy algo finished")


def _get_and_log_job_status(ctx, job_id, expected_status):
        job_status = ctx.job_status(job_id)
        ctx.log.info("expected: {} \t actual: {}".format(expected_status, job_status))

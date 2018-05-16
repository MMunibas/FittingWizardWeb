from os import path

from algorithms.toolkit import *
from distutils.dir_util import copy_tree
import time
import json


@register
def ljfit(ctx):
    ctx.set_running_status('Simulating LJ Fit')

    testdata = path.join(ctx.calc_out_dir.full_path, '../../../testdata/ljfit_output/')
    if not path.exists(testdata) or not path.isdir(testdata):
        raise Exception('testdata/ljfit_output missing')

    with ctx.run_out_dir.open_file("parameters_dump.json", "w") as input_param_dump_file:
        json.dump(ctx.parameters, input_param_dump_file)
    copy_tree(testdata, ctx.run_out_dir.full_path)
    ctx.set_running_status('Simulating LJ Fit: Copying')

    interruptable_wait(ctx, 30)


@register
def mtpfit_part1(ctx):
    ctx.set_running_status('Simulating MTP generating files')
    testdata = path.join(ctx.calc_out_dir.full_path, '../../../testdata/mtp_fit/part1')
    copy_tree(testdata, ctx.run_out_dir.full_path)
    ctx.set_running_status('Simulating MTP generating files: Copying')

    interruptable_wait(ctx, 30)


@register
def mtpfit_part2(ctx):
    ctx.set_running_status('Simulating MTP fit')
    testdata = path.join(ctx.calc_out_dir.full_path, '../../../testdata/mtp_fit/part2')
    copy_tree(testdata, ctx.run_out_dir.full_path)
    ctx.set_running_status('Simulating MTP fit: Copying')

    interruptable_wait(ctx, 30)


def interruptable_wait(ctx, seconds):
    iterations = abs(seconds / 5)
    ctx.set_running_status("waiting total {} seconds with {} iterations".format(seconds, iterations))

    for i in range(0, iterations):
        time.sleep(5)
        ctx.terminate_if_canceled()
        ctx.set_running_status("finished iteration {}".format(i))

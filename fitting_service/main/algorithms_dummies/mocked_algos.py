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
    time.sleep(15)


@register
def mtpfit_part1(ctx):
    ctx.set_running_status('Simulating MTP generating files')
    testdata = path.join(ctx.calc_out_dir.full_path, '../../../testdata/mtp_fit/part1')
    copy_tree(testdata, ctx.run_out_dir.full_path)
    ctx.set_running_status('Simulating MTP generating files: Copying')
    time.sleep(15)

@register
def mtpfit_part2(ctx):
    ctx.set_running_status('Simulating MTP fit')
    testdata = path.join(ctx.calc_out_dir.full_path, '../../../testdata/mtp_fit/part2')
    copy_tree(testdata, ctx.run_out_dir.full_path)
    ctx.set_running_status('Simulating MTP fit: Copying')
    time.sleep(15)
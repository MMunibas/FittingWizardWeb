from .toolkit import *
from distutils.dir_util import copy_tree
import time
import json

DEMO_DATA_DIR = "D:\\_projects\\unibas\\FittingWizardWeb\\fitting\\data\\debugging-mode\\lj_fit\\runs\\eps0.90_sig1.00_1524231346"

@register

def dummy_ljfit(ctx):
    with ctx.output_dir.open_file("parameters_dump.json", "w") as input_param_dump_file:
        json.dump(ctx.parameters, input_param_dump_file)
    copy_tree(DEMO_DATA_DIR, ctx.output_dir.full_path)
    time.sleep(5)

# Implement Algorithms
## Calculation
## Run
## Job
## Decorator
To register a algorithm decorate a function with the @register_algorithm decorator and import the containing module inside the __init__.py file of the algorithms package.
The decorated function should take one parameter that contains the calculation context.

example:
@register_algorithm
def somealgo(context):
    pass
    
If the algorithm input files should be validated a input validator function can be registered using a decorator (usage same as python property decorator).
example:
 
@somealgo.input_validator
def input_validator(context):
    pass
   
## Context
For a algorithm to communicate to the outside world the calculation context should be used.
### Properties
#### ctx.log

#### ctx.base_path

#### ctx.work_dir

#### ctx.input_dir

#### ctx.output_dir

#### ctx.calculation_parameters

#### ctx.run_parameters

#### ctx.status

#### ctx.jobs(self)

### Methods

#### ctx.sleep(self, delay_in_s)

#### ctx.update_status(status, message)

#### ctx.set_canceled(message="")

#### ctx.set_finished(message="")

#### ctx.set_failed(exception)

#### ctx.handle_exception(message, exception)

#### ctx.terminate_if_canceled()

#### ctx.request_cancel()

#### ctx.cancel_all_jobs()

#### ctx.schedule_job(command)

#### ctx.job_status(job_id)

#### ctx.wait_for_finished_jobs(*jobs)


# Implement Algorithms
## Context
For a algorithm to communicate to the outside world the calculation context should be used.

see interface documentation in [Interface Definition](../main/algorithms/toolkit.py#IContext)

## Decorators
To register a algorithm decorate a function with the ```@register``` decorator.
The decorated function should take one parameter that contains the calculation context.

example:

```python
@register
def somealgo(context):
    pass
```

If needed, you can define input validators.

example:
 
```python
@input_validator("somealgo")
def input_validator1(context):
    return context.input_dir.contains("somefile.csv")
    
@input_validator("somealgo")
@input_validator("anotheralgo")
def input_validator2(context):
    return context.input_dir.has_subdir("more_data")
```

## Testing

To test the implementation of a algorithm locally use CalculationTest.

```CalculationTest.execute()``` starts the calculation. 
```CalculationTest.shutdown()``` is used to clean up the environment after running the calculation (stops polling threads...)

[Example](../main/fitting_test.py)

### New calculation

To create a new calculation, use:
- ```CalculationTest.set_algorithm("algo_name")``` to set the algorithm you want to run
- ```CalculationTest.set_calculation_params({"parameter":"dict"})``` to set the calculation parameters
- ```CalculationTest.set_run_params({"parameter":"dict"})``` to set the run parameters (will override calculation parameters if key is duplicated)
- ```CalculationTest.add_input_file("absolute/or/relative/path/to.file")```to add input files (will create copy of original)

### Existing calculation

To create a existing calculation, use:
- ```CalculationTest.use_calculation_data("calculation_id")``` to set the calculation parameters
- ```CalculationTest.use_last_run_params()``` to set the run parameters (will override calculation parameters if key is duplicated)
- All ```CalculationTest.set_...``` and ```CalculationTest.add_...``` described above to override previous values

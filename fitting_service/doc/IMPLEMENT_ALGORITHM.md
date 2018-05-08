# Implement Algorithms
## Context
For a algorithm to be as maintainable and portable as possible it should only use the context parameter.
The context offers a variety of functions to read/write files, schedule and wait for jobs, print log messages and many more.

Consult the [interface definition](../main/algorithms/toolkit.py#IContext) for a full list of features and their documentation. 
Sample usage: [dummy algorithm](../main/algorithms_dummies/dummy_algorithm.py)

## Decorators
### Register algorithms
Decorators are a very powerful feature in python.
they can be used to annotate functions and classes.

To register a algorithm decorate a function with the ```@register``` decorator.
The decorated function should take one parameter that contains the calculation context.

examples:

```python
@register
def somealgo(context):
    pass
```
[dummy algorithm](../main/algorithms_dummies/dummy_algorithm.py)


### Input validation
If needed, you can define input validators.
input validators get the same context as the algorithm and are used to check if all required files are present, if they have the correct format, if all required parameters are setup correctly... 

examples:
 
```python
@input_validator("somealgo")
def input_validator1(context):
    """ checks if there is a input file named 'somefile.json' """
    return context.input_dir.contains("somefile.json")
    
@input_validator("somealgo")
@input_validator("anotheralgo")
def input_validator2(context):
    """ checks if 'somefile.json' is a well formed json file """
    import json
    try:
        with context.input_dir.open_file("somefile.json", "r") as json_file:
            json.load(json_file)
            return True
    except:
        return False
    
@input_validator("anotheralgo")
def input_validator3(context):
    """ checks if there is a parameter called 'y' """
    return "y" in context.parameters
```
[dummy input validators](../main/algorithms_dummies/dummy_input_validators.py)


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
- ```CalculationTest.add_input_file("absolute/or/relative/path/to.file")```to add input files (will create copy of original to keep all data for the calculation in one place)

### Existing calculation

To create a existing calculation, use:
- ```CalculationTest.use_calculation_data("calculation_id")``` to set the calculation parameters
- ```CalculationTest.use_last_run_params()``` to set the run parameters (will override calculation parameters if key is duplicated)
- All ```CalculationTest.set_...``` and ```CalculationTest.add_...``` described above to override previous values

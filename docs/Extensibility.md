# Extensibility #

The following sections describe how the services of the fitting system may be extended.

## Calculation Service ##

The most common case for extensibility of the calculation service is to add new types of algorithms. 

The following steps are required to add a new algorithm:
* Create a new python script file in the `algorithms` package, e.g. `mtp-part1.py`
* Define a function with one parameter named `ctx` and decorate with `@register`. The name of the function will be used as the algorithm type, e.g. `mtpfit_part1`
* Write calculation logic within the function using the utility functions provided by the `ctx`. The `ctx` is an object which 
* Restart the service in order to register the new type and use the swagger UI or fitting web to perform tests.

## Fitting Web ##

The fitting web implements two major workflows:
- `MTP Fit using Gaussian`
- `LJ Fit using CHARMM`

Both workflows follow similar concepts which should be followed for new workflows, so we will only point here to the most important implementations to copy and adapt accordingly.

- `WelcomePage`: The first selection after the login, which allows the user to select the workflow.
- `
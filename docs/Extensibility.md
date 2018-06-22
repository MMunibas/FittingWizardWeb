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

Both workflows follow similar concepts which should be followed for new workflows, so we will only point here to the most important things to copy and adapt accordingly.

- `WelcomePage` contains the first selection after the login, which allows the user to select the workflow.
- `session package` contains all pages for session creation (upload of files, definition of default parameters, creation of directories). This is implemented in a wizard style and depends on the workflow. Once a session was created the user is always directed to the overview of the workflow.
  - `ljfit` uploads input files, defines default parameters and saves everything in the `LjFitSessionDir` of the user.
  - `mtpfit` uploads XYZ files and runs the `mtpfit_part1` calculation, which concludes the session creation process. Files are saved in the `MtpFitDir` and `MoleculesDir` of the user.
- `fitting package`
  - `ljfit` allows the user to run a single or multiple `ljfit` calculations which are summarized on an overview page.
  - `mtpfit` allows the user to run a `mtpfit_part2` calculation, which adds an entry to the overview page.
- `commands package`
  - `ljfit` 
    - `RunLjFitsCommand` prepares a `StartDefinition` and a `LjFitRunDir` where all output files will be downloaded to for each run pair parameter.
  - `mtpfit`
    - `RunMtpGenerateFilesCommand` prepares the molecule directory into which the generated files are downloaded. The calculation will not be deleted because fits will run in the same calculation.
    - `RunMtpFitCommand` runs a fit in an existing calculation created when the MTP fit session was created.
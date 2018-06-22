# Fitting System #

The fitting system contains two services:

- `calculation service`: The calculation service provides a standardized API to execute calculations. A calculation provides a context to upload input files and execute different types of algorithms on the uploaded data. Calculations are independent of each other and in parallel. Within a calculation multiple runs with different parameters and algorithm types may be executed sequentially. Depending on the type of algorithm, a run may take hours or days.

- `fitting web`: The fitting web service provides a user interface for convenience on top of the calculation service. The main responsibility of the service is the high level orchestration of tasks the user wants to achieve with one or more types of calculations. Furthermore the service maintains the user's data in a directory structure.

The web service is written in java, however, the calculation service provides its API via an OpenAPI definition. This enables alternative clients to perform calculations and easily integrate the algorithms into their workflow.

The calculation service is a REST frontend which has a cluster with many nodes as its backend to distribute heavy calculations to.

The most important concepts are explained in the sections bellow.

# Calculation Service #

The service contains the following modules.
- `main`: Contains main function and further entry points for testing purposes.
- `toolkit`: Contains infrastructure classes and functions.
- `fitting_service`: Contains all framework classes and functions and does the actual orchestration of the service.
- `algorithms`: Contains all algorithm types and their corresponding utility classes.
- `algorithm_dummies`: Contains algorithm types for testing purposes which basically simulate work by copying data from the `testdata` directory and waiting. The run the service with dummies pass a `--mock` parameter to the main entry point.

The following sections describe important packages within the fitting_service module.

## api ##

The `api` contains all REST resources and performs input validation. The API documentation is provided by the standard swagger UI reachable via the service URI.

## calculation ##

The `calculation` package implements the management of the actual calculations. Each calculation runs in its own thread. The algorithm implementations are mainly doing orchestration work for the jobs running on the cluster environment, so no heavy calculations are expected on the node running the calculation service.

## job ##

The `job` package implements the interaction for job dispatching to the cluster environment. At the moment the main cluster environment is `GridEngine` and jobs are managed via `qsub`, `qdel` and `qstat` commands. We also have a rudimentary `SingleNodeManagement` implementation in order to run the dummy algorithms on the same machine without a cluster environment.

## file access ##

The `file_access` package implements the interaction with the file system. The data directory has the following components.

- `Storage` creates/deletes new calculation directories
- `CalculationDirectory` writes/reads results, prepares new runs
- `CalculationStatus` writes/reads the status of the calculation
- `CancelFile` create/delete cancel file
- `JobFile` remembers running jobs in the file

# Fitting Web #

The fitting web service contains the following components.

## UI - Web ##

Following packages are contained in the UI layer

- `calculation` contains the debugging views which allow to directly talk to the `calculation service`
- `misc` contains additional pages
- `mtpfit` contains all parts for the MTP fit workflow
- `ljfit` contains all parts for the LJ fit workflow
- `welcome` contains login and welcome screen

## UI - Commands ##

Commands are used for the interaction with the application layer. Each operation between UI and application should be reflected in a command. A command performs the operation and triggers the navigation.

- `RunMtpGenerateFilesCommand` prepares parameters and spawns one calculation of type `mtpfit_part1`
- `RunMtpFitCommand` prepares parameters and spawns one calculation of type `mtpfit_part2`
- `RunLjFitsCommand` prepares parameters and spawns a calculations of type `ljfit` for each passed run parameter pair.

## UI - Misc ##

- `WelcomePage` shows an overview of all available workflows
- `NewSessionPage` shows login page
- `HeaderPage` defines the basis layout of all pages
- `ProgressPage` shows the progress of a `CalculationGroup` for the logged in user. The user is navigated to the page if a group exists.

## Application - Directories ##

The `directories` package contains all components necessary to maintain the directory structure for all users.

- `UserDirectory` creates/deletes directories of users
- `MtpFitDir` maintains data for the MTP fit workflow of a user
- `MoleculesDir` maintains data of the generated molecule for a MTP fit
- `FitOutputDir` maintains data of all executed MTP fits 
- `LjFitSessionDir` maintains data for the LJ Fit workflow of a user
- `LjFitRunDir` maintains data for all LJ fit runs

## Application - Calculation ##

The `calculation` package contains all components for the interaction with the `calculation service`.

- `CalculationApi` implements the interaction with the calculation service API. Code was generated with [Swagger Editor](https://editor.swagger.io/). 
- `CalculationService` implements the application specific API on top of the generated API
- `CalculationManagementClient` implements the asynchronous interaction with the `calculation service`
- `CalculationManager` implements the actor which maintains all ongoing calculation groups
- `CalculationGroup` implements the actor which groups multiple running calculations
- `CalculationRun` implements the actor which maintains one ongoing calculation run

### Spawn new calculation ###

In order run one or multiple calculations a `StartDefinition` has to be created and spawned via the `CalculationManagementClient`. Examples may be found in `RunLjFitsCommand`, `RunMtpGenerateFilesCommand` and `RunMtpFitCommand`. In order to run multiple calculations at once (as done with the LJ fit), multiple `StartDefinition`s may be passed. The `ProgressPage` will show all calculations spawned together via a `StartDefinition`.


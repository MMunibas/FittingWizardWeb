# Fitting System #

The fitting system contains two services:

- `calculation service`: The calculation service provides a standardized API to execute calculations. A calculation provides a context to upload input files and execute different types of algorithms on the uploaded data. Calculations are independent of each other and in parallel. Within a calculation multiple runs with different parameters and algorithm types may be executed sequentially. Depending on the type of algorithm, a run may take hours or days.

- `fitting web`: The fitting web service provides a user interface for convenience on top of the fitting service. The main responsibility of the service is the high level orchestration of tasks the user wants to achieve with one or more types of calculations. Furthermore the service maintains the user's data in a directory structure.

The web service is written in java, however, the calculation service provides its API via an OpenAPI definition. This enables alternative clients to perform calculations and easily integrate the algorithms into their workflow.

The calculation service is a REST frontend which has a cluster with many nodes as its backend to distribute heavy calculations to.

The most important concepts are explained in the sections bellow.

## Calculation Service ##

The service contains the following modules.
- `main`: Contains main function and further entry points for testing purposes.
- `toolkit`: Contains infrastructure classes and functions.
- `fitting_service`: Contains all framework classes and functions and does the actual orchestration of the service.
- `algorithms`: Contains all algorithm types and their corresponding utility classes.
- `algorithm_dummies`: Contains algorithm types for testing purposes which basically simulate work by copying data from the `testdata` directory and waiting. The run the service with dummies pass a `--mock` parameter to the main entry point.

The following packages or classes are important.

### api ###

The `api` contains all REST resources and performs input validation. The API documentation is provided by the standard swagger UI reachable via the service URI.

### calculation ###

The `calculation` package implements the management of the actual calculations. Each calculation runs in its own thread. The algorithm implementations are mainly doing orchestration work for the jobs running on the cluster environment, so no heavy calculations are expected on the node running the calculation service.

### job ###

The `job` package implements the interaction for job dispatching to the cluster environment. At the moment the main cluster environment is `GridEngine` and jobs are managed via `qsub`, `qdel` and `qstat` commands. We also have a rudimentary `SingleNodeManagement` implementation in order to run the dummy algorithms on the same machine.

### file access ###

## Fitting Web ##

The web service contains the following components.

### Calculation ###

### Directories ###

### Commands ###
# Extending the Fitting Wizard
Release: 2.0.1  
File Author: Mike Devereux

The following sections describe how the Fitting Wizard may be extended by scientific developers (developers working on the scientific algorithms or making minor modifications to the web UI to add new properties, rather than developers making larger changes to the UI or changes to the API).

- For information on how to write new scientific algorithms for different fitting techniques or to address new types of fitting problem, see [Calculation Service](#calculation-service)

- For information on how a new property can be added to the web UI and integrated with the Python scripts, see [Fitting Web](#fitting-web)

- For a worked example where a hypothetical new property is added to the UI, new input and files are passed to the scripts and results and files are passed back to the UI to be displayed, see the [Worked Example](#worked-example)


## Calculation Service

Here we examine extensibility of the calculation service for a new fitting problem, or modification of the existing algorithms. To date the Fitting Wizard allows Lennard-Jones (LJ) fitting using CHARMM and multipole (MTP) fitting using Gaussian, but as the code is written in a modular fashion new fitting algorithms can easily be added or existing algorithms modified without affecting the API or web UI.

The basic strategy is to implement a new Python workflow with a master script in fitting_service/main/algorithms. The master script can be registered with the API running on the calculation server or cluster, so that fitting can take place even without the UI by sending Python commands to the API via the Flask interface. Corresponding changes to the web UI to interactively upload new reference data or display new results can then be made separately, as detailed in [Fitting Web](#fitting-web).

The following steps are required to add a new algorithm:
* Create a new python script file in the `algorithms` package, e.g. `test.py`
* Define a function in the file with one parameter named `ctx` and label with `@register`. The register tag indicates to the API that the new algorithm should be included when the API starts (`@register` is imported from ".toolkit"). The name of the function will be used as the algorithm type, so to create a function 'test' that writes a simple message to the API log:

~~~~
  from .toolkit import *  
  @register  
  def test(ctx):  
    ctx.log.info("hello world!")
~~~~
* Note that the API must be restarted for the new script to be found and incorporated, or for any changes to the script to take effect.
* The `ctx` in the script is an object which represents the script's "context", or instance, so methods such as "log.info()" associated with the context variable are taken from the parent process (in this case the API)
* For development purposes, another Python script can be written to interact directly with the API without needing to use or modify the web UI. This script should import the "CalculationTest" class, a minimal example would be:

~~~~
from fitting_service.calculation_test import CalculationTest
def run_test():
  mytest = CalculationTest()
  mytest.set_algorithm("test")
  mytest.set_calculation_params({"par1":"dummy1"})
  mytest.set_run_params({"par2":"dummy2"})
  mytest.execute()

if __name__ == '__main__':
  run_test()

CalculationTest.shutdown()
~~~~
* In this example the "__main__" routine is called if the script is run directly from the command line, so running the script from the command-line invokes run_test(), then "shutdown()" to kill the process (otherwise the script never terminates).
* The run_test routine here uses the context to create an instance of CalculationTest, then sets the algorithm to the "test" algorithm that we just created above, passes some global calculation parameters (e.g. the temperature for all simulations that won't be changed for each run), then some run parameters (e.g. a convergence tolerance that we define separately for each fit), then finally calls "execute" to run the test routine that we defined above.

This basic framework can be extended using a combination of standard Python and routines provided by the context to build up and test your new algorithm.

### Context methods
- To access parameters from inside your algorithm that were passed to the API use:

~~~~
    try:
       par1 = ctx.parameters["par1"]
    except ValueError:
       pass
~~~~
- To check all parameters passed to API, use:

~~~~
    for parameter, value in ctx.parameters.items():
       ctx.log.debug("input parameter {} is set to {}".format(parameter, value))
~~~~
- To access uploaded files, use the filename variables that are also passed as parameters
- To write messages to the API log, use:

~~~~
ctx.log.info("my message")
~~~~
- To write messages to the "running calculation" progress bar window in the web UI, use:

~~~~
ctx.set_running_status('my message')
~~~~
- To get the directory containing uploaded input files, use:

~~~~
ctx.input_dir.full_path
~~~~
- To get the directory containing output for the current run, use:

~~~~
ctx.run_out_dir.full_path
~~~~
- To create a new subdirectory in either the input or output directory use:

~~~~
ctx.input_dir.subdir("mydir").full_path
ctx.run_out_dir.subdir("mydir").full_path
~~~~
- To submit a job to the cluster queue, use:

~~~~
job_id = ctx.schedule_job("job_path/job_script")
~~~~
- To wait for all submitted jobs to finish before continuing, use:

~~~~
ctx.wait_for_all_jobs()
~~~~
- To instruct a script to exit at this point if the user clicks "cancel" in the UI (otherwise the job will just continue), use:

~~~~
ctx.terminate_if_canceled()
~~~~

### Programming tips for scripts
- Use the context methods wherever possible to avoid causing unexpected crashes that are difficult to debug
- Make use of the API log to debug your code, as additional error messages and traces may appear here rather than in the terminal where you launch your test script
- Do not use the "exit" command in your scripts to handle a problem, as this can cause instability in the API (your script runs as a subprocess of the UI, so exiting tries to kill the API parent process as well!) Instead use "try" and "except" e.g.:

~~~~
    try:
       tabfile = ctx.parameters["mtp_fitting_table_filename"]
    except ValueError:
       pass
~~~~
- Don't forget to use `ctx.terminate_if_canceled()`, in particular while submitting many jobs to the queue, add this command after each submission. That way if the user cancels the jobs before submission is finished then the remainder of the jobs won't be submitted and block the cluster unnecessarily (canceling the run won't kill the jobs)

Once the algorithm is complete and has been tested, simple changes can be made to the web UI as needed and used to call and interact with it, as detailed in the next section.

## Fitting Web

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

### Worked Example

The hypothetical "Property X" requires:

- An additional file to be uploaded and passed to the "ljfit.py" script
- An additional piece of reference data to be entered and passed to the script
- A new value to be read from the .json file generated by the script
- The new value to be displayed in the UI results table

The following steps could be used to achieve this:
* Add a new input field to the HTML section of fitting/fitting-web/src/main/java/ch/unibas/fitting/web/ljfit/session/CreateNewSessionPage.html as follows:
~~~~
	<tr>
	   <td>TEST file:</td>
	   <td>
	        <input wicket:id="testUploadFile" type="file" class="btn btn-default" accept=".test" id="testUploadFile"/>
	        <span class="file-custom"></span>
	   </td>
	</tr>
  ~~~~
* Add another input field for the reference value of property X
* Modify the corresponding Java code in CreateNewSessionPage.java to add the new fields:

~~~~
        private final FileUploadField testUploadFile;
        ...
        form.add(testUploadFile = createFileUploadField("testUploadFile"));
        ...
        File testFile = uploadFile(destination, testUploadFile.getFileUpload());
        return new UploadedFileNames(
              testFile,
        ...

        private final IModel<Double> experimentalPropertyX = Model.of();
        ...
        NumberTextField<Double> expectedPropertyXField = new NumberTextField<>("experimentalPropertyX", experimentalPropertyX);
        expectedPropertyXField.setRequired(true);
        expectedPropertyXField.setStep(NumberTextField.ANY);
        expectedPropertyXField.setConvertEmptyInputStringToNull(true);
        expectedPropertyXField.setType(Double.class);
        form.add(expectedPropertyXField);
        ...
        SessionParameter parameter = new SessionParameter(
                 experimentalPropertyX.getObject(),
        ...
~~~~
* Add the new reference property to application/algorithms/ljfit/SessionParameter.java
* Add new file to application/algorithms/ljfit/UploadedFileNames.java
* Add new file to application/algorithms/ljfit/UploadedFiles.java
* Add new file and property variables to fitting-web/src/main/java/ch/unibas/fitting/web/ljfit/commands/RunLjFitsCommand.java
* Add new property to application/algorithms/ljfit/LjFitRunResult.java:
~~~~
	public class LjFitRunResult {
    		public final double calcPropertyX,
		expPropertyX,
        ...
	public LjFitRunResult(double calcPropertyX, double expPropertyX ...
	        this.calcPropertyX=calcPropertyX;
        	this.expPropertyX=expPropertyX;
	...
        public LjFitRunResult(LjFitSession session,
                          LjFitRunInput in,
                          LjFitJsonResult jsonResult){
        this(
                jsonResult.propertyX,
                session.getSessionParameter().expectedPropertyX,
		...
~~~~
* Add property X to results table in web/ljfit/fitting/step2/LjSessionPage.html:

~~~~
        <td align="center" style="border: 1px solid; width: 60px">&Delta;H<sub>vap.exp</sub></td>
        <td align="center" style="border: 1px solid; width: 60px">Calc Prop X</td>
        <td align="center" style="border: 1px solid; width: 60px">Exp Prop X</td>
	...
        <td align="center" style="border: 1px solid; width: 60px"><span wicket:id="expdeltaH"></span></td>
        <td align="center" style="border: 1px solid; width: 60px"><span wicket:id="calcPropertyX"></span></td>
        <td align="center" style="border: 1px solid; width: 60px"><span wicket:id="expPropertyX"></span></td>
        ...
~~~~
* Add property to corresponding Java results routine

~~~~
 web/ljfit/fitting/step2/LjSessionPage.java:
        item.add(new Label("expdeltaH", format(singleResult.get_expdeltaH(), 2)));
        item.add(new Label("calcPropertyX", format(singleResult.get_calcPropertyX(), 2)));
        item.add(new Label("expPropertyX", format(singleResult.get_expPropertyX(), 2)));
	...
  ~~~~
* Add property to web/ljfit/fitting/step2/SingleRunResult.java:

~~~~
	Double _calcPropertyX, _expPropertyX, ...
	...
        result.result.peek(res -> {
            this._calcPropertyX = res.calcPropertyX;
            this._expPropertyX = res.expPropertyX;
	...
        public Double get_calcPropertyX() { return _calcPropertyX;    }
	public Double get_expPropertyX() { return _expPropertyX;    }
  ~~~~
* Add property to json result parsing in

~~~~
 application/algorithms/ljfit/LjFitJsonResult.java:
	public final double vaporization_enthalpy;
	public final double propertyX;
	...
	public LjFitJsonResult(JsonObject json) {
	        propertyX = json.get("propertyX").getAsDouble();
          ~~~~

* To access these variables from the API to use in the scripts, use values from web/ljfit/commands/RunLjFitsCommand.java, e.g.:
~~~~
        refPropertyX = float(ctx.parameters["reference_property_x"])
        testFile = ctx.parameters["lj_filename_test_file"]
        ~~~~

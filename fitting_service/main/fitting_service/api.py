from flask import Flask, request, redirect, send_file
from flask_restplus import Resource, Api, fields
from werkzeug.datastructures import FileStorage
from .calculation import CalculationService, CalculationStatus
from .calculation import InvalidInputException, CalculationNotRunningException, CalculationRunningException
from .job import JobsService

app = Flask(__name__)
api = Api(app,
          version='0.1',
          title='Calculation Service API',
          description='Provides operations for running different kind of calculations',
          validate=True)

ns_global = api.namespace('/', description='Global Operations')
ns_calculation = api.namespace('calculation', description='Fitting Operations')

model_svc_info = ns_global.model('ServiceInfo',
                                 {
                                     'version': fields.Float(required=True,
                                                             description='Current service version',
                                                             example=0.1),
                                     'server_status': fields.String(required=True,
                                                                    description='Couster status',
                                                                    example='totally insane')
                                 })

model_file_list = ns_calculation.model('FileList', {
    "files": fields.List(fields.String, required=True,
                              description='List of files',
                              example=["somefile.json"])})

model_algo_list = ns_global.model('AlgorithmList', {
    "algorithms": fields.List(fields.String, required=True,
                              description='List of registered algorithms',
                              example=["dummy_algorithm"])})

model_job_id_list = ns_calculation.model('JobIdList', {
    "jobs": fields.List(fields.String, required=True,
                        description='List of calculation ids',
                        example=["OEW1L", "ZKNDn", "UJP-n", "dsma9", "97S8j", "DtXXg", "H00g0", "023iV", "dMZ11",
                                 "Sykfq", "pUZLN", "Nwfz0", "a6XFG"])})

model_calc_id = ns_calculation.model('CalculationId', {
    "calculation": fields.String(required=True,
                                 description='Id of new calculation',
                                 example="2018-04-05_10-03-41-054461_OEW1L")})

model_calculation = ns_calculation.model('Calculation', {
    'parameters': fields.String(required=True,
                                description='Parameter for the fitting algorithm',
                                example='{"x":8765, "y": 4321}')
})

model_calculation_run = ns_calculation.model('Run', {
    'algorithm': fields.String(required=True,
                               description='Possible values: LJFit, MTPFit',
                               example="dummy_algorithm"),
    'parameters': fields.String(required=True,
                                description='Parameter for the fitting algorithm',
                                example='{"x":1234, "y": 5678}')
})

model_status = ns_calculation.model('Status', {
    'last_run': fields.String(required=True,
                              description='Calculation status',
                              example='2018-04-06_07-40-38-579644_-4bl6'),
    'status': fields.String(required=True,
                            description='Calculation status',
                            example='Running'),
    'message': fields.String(required=True,
                             description='Status message',
                             example='Step 1 / 10'),
    'calculation_parameters': fields.Nested(model_calculation,
                                            required=True,
                                            description='Parameters for the fitting algorithm',
                                            example={"parameters": {"x": 8765, "y": 4321}}),
    'run_parameters': fields.Nested(model_calculation_run, required=True,
                                    description='Parameters for the fitting algorithm',
                                    example={"algorithm": "dummy_algorithm", "parameters": {"x": 8765, "y": 4321}}),

    'input_files': fields.List(fields.String, required=True,
                               description='Uploaded input files',
                               example=[
                                   "somefile.json"
                               ])})

model_calculation_status = ns_calculation.model('CalculationStatus', {
    'id': fields.String(required=True,
                        description='calculation_id',
                        example='2018-04-06_07-40-38-478641_a6XFG'),
    'status': fields.Nested(model=model_status)})

model_calculation_status_list = ns_calculation.model('CalculationStatusList',
                                                     {"calculations": fields.List(
                                                         fields.Nested(model_calculation_status),
                                                         required=True,
                                                         description='Parameter for the fitting algorithm',
                                                         example=[{"id": "2018-04-05_13-42-43-207763_Nwfz0",
                                                                   "status": {
                                                                       "last_run": "2018-04-05_13-42-43-284265_SxcVY",
                                                                       "status": "Finished",
                                                                       "message": "",
                                                                       "calculation_parameters": {
                                                                           "parameters": {"calc_param1": "value1",
                                                                                          "calc_param2": "value2"}},
                                                                       "run_parameters": {
                                                                           "algorithm": "dummy_algorithm",
                                                                           "parameters": {"calc_param1": "value1",
                                                                                          "calc_param2": "value2",
                                                                                          "run_param1": "value1",
                                                                                          "run_param2": "value2"}},
                                                                       "input_files": ["somefile.json"]}},
                                                                  {"id": "2018-04-06_07-40-38-478641_a6XFG", "status": {
                                                                      "last_run": "2018-04-06_07-40-38-579644_-4bl6",
                                                                      "status": "Finished",
                                                                      "message": "",
                                                                      "calculation_parameters": {
                                                                          "parameters": {"calc_param1": "value1",
                                                                                         "calc_param2": "value2"}},
                                                                      "run_parameters": {"algorithm": "dummy_algorithm",
                                                                                         "parameters": {
                                                                                             "calc_param1": "value1",
                                                                                             "calc_param2": "value2",
                                                                                             "run_param1": "value1",
                                                                                             "run_param2": "value2"}},
                                                                      "input_files": ["somefile.json"]}}])})

model_run_id = ns_calculation.model('RunId', {"run_id": fields.String(required=True,
                                                                      description='Parameter for the fitting algorithm',
                                                                      example='2018-04-06_07-40-38-579644_-4bl6')})

upload_parser = api.parser()
upload_parser.add_argument('file', location='files',
                           type=FileStorage, required=True)


@ns_global.route('/info')
class VersionInfo(Resource):
    @api.response(200, 'service info', model=model_svc_info)
    def get(self):
        """
        Returns the version of all calculation scripts
        """
        return CalculationService().info


@ns_global.route('/algorithms')
class AlgorithmList(Resource):
    @api.response(200, 'list of registered algorithms', model=model_algo_list)
    def get(self):
        """
        Returns a list of calculations
        """
        pass
        return CalculationService().list_algorithms()


@ns_calculation.route('/')
class CalculationList(Resource):
    @api.response(200, 'list of calculations', model=model_calculation_status_list)
    def get(self):
        """
        Returns a list of available calculations
        """
        return CalculationService().list_all_calculations()

    @api.expect(model_calculation)
    @api.response(200, 'create new calculation', model=model_calc_id)
    def post(self):
        """
        Creates new calculation
        """
        print(api.payload)
        return CalculationService().create_new_calculation(api.payload)


@ns_calculation.route('/<string:calculation_id>')
@api.response(404, 'no calculation with id {calculation_id} found')
class CalculationResource(Resource):
    @api.response(200, 'calculation status', model=model_status)
    def get(self, calculation_id):
        """
        Returns the status of the specified calculation
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)
        return CalculationService().get_calculation_status(calculation_id)

    @api.response(200, 'parameters successfully updated', model=model_status)
    @api.response(405, 'parameters update failed')
    @api.expect(model_calculation)
    def post(self, calculation_id):
        """
        Update parameters
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)
        try:
            CalculationService().set_calculation_parameters(calculation_id, api.payload)
            return CalculationService().get_calculation_status(calculation_id)

        except Exception as ex:
            print(ex)
            return redirect(request.url, 405)

    @api.response(200, 'calculation deleted successfully')
    @api.response(405, 'calculation already running')
    def delete(self, calculation_id):
        """
        Deletes the specified calculation
        """

        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)
        try:
            return CalculationService().delete_calculation(calculation_id)
        except CalculationRunningException:
            return redirect(request.url, 405)


@ns_calculation.route('/<string:calculation_id>/cancel')
@api.response(404, 'no calculation with id {calculation_id} found')
@api.response(405, 'calculation not running, only running calculations can be canceled')
class CancelCalculationAction(Resource):
    @api.response(200, 'Calculation canceled')
    def post(self, calculation_id):
        """
        Abort the specified calculation
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)
        try:
            return CalculationService().cancel_calculation(calculation_id)
        except CalculationNotRunningException:
            return redirect(request.url, 405)


@ns_calculation.route('/<string:calculation_id>/run')
class RunCalculationAction(Resource):
    @api.response(200, 'calculation successfully started', model=model_run_id)
    @api.response(404, 'no calculation with id {calculation_id} found')
    @api.response(405, 'calculation already running or algorithm not supported')
    @api.response(412, 'input validation failed')
    @api.expect(model_calculation_run)
    def post(self, calculation_id):
        """
        Start a run of this calculation
        """
        if not CalculationService().calculation_exists(calculation_id):
            return {'message': 'calculation not found'}, 404
        algo = self.api.payload['algorithm']
        if not CalculationService().is_algorithm_supported(algo):
            return {'message': 'algorithm not supported'}, 405

        try:
            runid = CalculationService().run_calculation(calculation_id, self.api.payload, False)
            return {"run_id": runid}
        except CalculationRunningException:
            return {'message': 'calculation already running'}, 405
        except InvalidInputException:
            return {'message': 'invalid input'}, 412


@ns_calculation.route('/<string:calculation_id>/jobs')
@api.response(404, 'no calculation with id {calculation_id} found')
class JobResource(Resource):
    @api.response(200, 'list of jobs', model_job_id_list)
    def get(self, calculation_id):
        """
        List all running jobs for a calculation
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)

        return {"jobs": JobsService().list_jobs_for_calculation(calculation_id)}


@ns_calculation.route('/<string:calculation_id>/input')
class InputFileListResource(Resource):
    @api.response(200, 'list of input files', model=model_file_list)
    @api.response(404, 'no calculation with id {calculation_id} found')
    def get(self, calculation_id):
        """
        Upload input file
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)
        return CalculationService().list_input_files(calculation_id)

    @api.response(200, 'file uploaded successfully')
    @api.response(404, 'no calculation with id {calculation_id} found')
    @api.response(405, 'file upload failed')
    @api.expect(upload_parser)
    def post(self, calculation_id):
        """
        Upload input file
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)
        try:
            return CalculationService().upload_file(calculation_id, request)
        except InvalidInputException:
            return redirect(request.url, 405)


@ns_calculation.route('/<string:calculation_id>/input/<path:relative_path>')
@api.response(404, "no calculation with id {calculation_id} found or requested file doesn't exist")
@api.response(405, 'calculation did not yet run')
class InputFileDownloadResource(Resource):
    @api.response(200, 'File download')
    def get(self, calculation_id, relative_path):
        """
        List all running jobs for a calculation
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)

        return send_file(CalculationService().get_input_file_absolute_path(calculation_id, relative_path))


    @api.response(200, 'File deleted')
    def delete(self, calculation_id, relative_path):
        """
        Delete input file
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)

        return CalculationService().delete_input_file(calculation_id, relative_path)



@ns_calculation.route('/<string:calculation_id>/output')
@api.response(404, 'no calculation with id {calculation_id} found')
@api.response(405, 'calculation did not yet run')
class OutputFileListResource(Resource):
    @api.response(200, 'list of output files', model=model_file_list)
    def get(self, calculation_id):
        """
        List all running jobs for a calculation
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)
        if CalculationService().get_calculation_status(calculation_id)[CalculationStatus.LAST_RUN]:
            return CalculationService().list_output_files(calculation_id)
        return {"files": []}


@ns_calculation.route('/<string:calculation_id>/output/<path:relative_path>')
@api.response(404, "no calculation with id {calculation_id} found or requested file doesn't exist")
@api.response(405, 'calculation did not yet run')
class OutputFileDownloadResource(Resource):
    @api.response(200, 'File download')
    def get(self, calculation_id, relative_path):
        """
        Download output file
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)

        if CalculationService().get_calculation_status(calculation_id)[CalculationStatus.LAST_RUN]:
            return send_file(CalculationService().get_output_file_absolute_path(calculation_id, relative_path))

        return redirect(request.url, 405)

    @api.response(200, 'File deleted')
    def delete(self, calculation_id, relative_path):
        """
        Delete output file
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)

        if CalculationService().get_calculation_status(calculation_id)[CalculationStatus.LAST_RUN]:
            return CalculationService().delete_output_file(calculation_id, relative_path)

        return redirect(request.url, 405)

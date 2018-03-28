from flask import Flask, request, redirect
from flask_restplus import Resource, Api, fields
from werkzeug.datastructures import FileStorage
from .calculation import CalculationService
from .calculation import InvalidInputException, CalculationNotRunningException, CalculationRunningException
from .job import JobsService

app = Flask(__name__)
api = Api(app,
          version='0.1',
          title='Fitting service API',
          description='Provides operations for fitting algorithms',
          validate=True)

ns_global = api.namespace('/', description='Global Operations')
ns_calculation = api.namespace('calculation', description='Fitting Operations')

model_calculation = ns_calculation.model('Calculation', {
    'parameters': fields.String(required=True,
                                description='Parameter for the fitting algorithm',
                                example="{'x':8765, 'y': 4321}")
})

model_calculation_run = ns_calculation.model('Run', {
    'algorithm': fields.String(required=True,
                               description='Possible values: LJFit, MTPFit',
                               example="dummy_algorithm"),
    'parameters': fields.String(required=True,
                                description='Parameter for the fitting algorithm',
                                example="{'x':1234, 'y': 5678}")
})

upload_parser = api.parser()
upload_parser.add_argument('file', location='files',
                           type=FileStorage, required=True)


@ns_global.route('/info')
class VersionInfo(Resource):
    def get(self):
        """
        Returns the version of all calculation scripts
        """
        return CalculationService().info


@ns_global.route('/algorithms')
class AlgorithmList(Resource):
    def get(self):
        """
        Returns a list of calculations
        """
        pass
        return CalculationService().list_algorithms()


@ns_calculation.route('/')
class CalculationList(Resource):
    def get(self):
        """
        Returns a list of available calculations
        """
        return CalculationService().list_all_calculations()

    @api.expect(model_calculation)
    def post(self):
        """
        Creates new calculation
        """
        return CalculationService().create_new_calculation(api.payload)


@ns_calculation.route('/<string:calculation_id>')
@api.response(404, 'no calculation with id {calculation_id} found')
class CalculationResource(Resource):
    @api.response(200, 'calculation status')
    def get(self, calculation_id):
        """
        Returns the status of the specified calculation
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)
        return CalculationService().get_calculation_status(calculation_id)

    @api.expect(upload_parser)
    @api.response(200, 'file uploaded successfully')
    @api.response(405, 'file upload failed')
    def post(self, calculation_id):
        """
        Upload input file
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)
        try:
            return CalculationService().download_file(calculation_id, request)
        except InvalidInputException:
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
@api.response(200, 'calculation successfully started')
@api.response(404, 'no calculation with id {calculation_id} found')
@api.response(405, 'calculation running, can not delete running calculations.')
@api.expect(model_calculation_run)
class RunCalculationAction(Resource):
    def post(self, calculation_id):
        """
        Start a run of this calculation
        """

        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)
        try:
            return {"run_id": CalculationService().run_calculation(calculation_id, self.api.payload)}
        except CalculationRunningException:
            return redirect(request.url, 405)


@ns_calculation.route('/<string:calculation_id>/jobs')
@api.response(404, 'no calculation with id {calculation_id} found')
class JobResource(Resource):
    def get(self, calculation_id):
        """
        List all running jobs for a calculation
        """
        if not CalculationService().calculation_exists(calculation_id):
            return redirect(request.url, 404)

        return JobsService().list_jobs_for_calculation(calculation_id)

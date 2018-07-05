import logging
import sys

import os

import algorithms_dummies
from fitting_service import app, StorageService, Scanner

import fitting_service.settings as settings
from fitting_service.algorithms import scanner

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

mock_mode = False


def app_name():
    return 'FittingWeb mocked API ' if mock_mode else 'FittingWeb API '


if __name__ == '__main__':
    StorageService().initialize()
    if len(sys.argv) > 1 and sys.argv[1] == '--mock':
        scanner.ALGORITHM_PACKAGE = algorithms_dummies
        mock_mode = True

    logger.info(app_name() + 'Starting')

    Scanner.load_algorithms()

    host = os.getenv('FITTING_SERVICE_HOST', settings.FITTING_SERVICE_HOST)
    port = int(os.getenv('FITTING_SERVICE_PORT', settings.FITTING_SERVICE_PORT))
    app.run(host=host, port=port)

    logger.info(app_name() + 'Stopped')

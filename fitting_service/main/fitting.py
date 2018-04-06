import logging
import sys

import os

from fitting_service import app, Storage
import algorithms

import fitting_service.settings as settings

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

mock_mode = False


def app_name():
    return 'FittingWeb mocked API ' if mock_mode else 'FittingWeb API '


if __name__ == '__main__':
    Storage().initialize()
    if len(sys.argv) > 1 and sys.argv[1] == '--mock':
        mock_mode = True

    logger.info(app_name() + 'Starting')

    host = os.getenv('FITTING_SERVICE_HOST', settings.FITTING_SERVICE_HOST)
    port = int(os.getenv('FITTING_SERVICE_PORT', settings.FITTING_SERVICE_PORT))
    app.run(host=host, port=port)

    logger.info(app_name() + 'Stopped')

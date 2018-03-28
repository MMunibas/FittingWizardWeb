import logging
import sys

import os

from fitting_service import app
from fitting_service.file_acces import Storage
import algorithms

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

mock_mode = False


def app_name():
    return 'FittingWeb mocked API ' if mock_mode else 'FittingWeb API '


if __name__ == '__main__':
    Storage().set_root("../data")

    if len(sys.argv) > 1 and sys.argv[1] == '--mock':
        mock_mode = True

    logger.info(app_name() + 'Starting')

    port = int(os.getenv('FITTING_SERVICE_PORT', 5000))
    app.run(host='0.0.0.0', port=port)

    logger.info(app_name() + 'Stopped')

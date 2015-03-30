#!/usr/bin/env python

import sys

try:
 import scipy
 print "import scipy available"
 sys.exit(0)
except ImportError, e:
 print "ERROR: import scipy NOT available"
 sys.exit(1)
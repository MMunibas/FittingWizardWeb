#!/usr/bin/env python

import sys

try:
 import rdkit
 print "import rdkit available"
 from rdkit import Chem
 print "import Chem from rdkit available"
 sys.exit(0)
except ImportError, e:
 print "ERROR: import rdkit NOT available"
 sys.exit(1)
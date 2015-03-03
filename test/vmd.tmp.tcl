mol load psf phenol_gas.min.psf pdb phenol_gas.equi.pdb
set solute [atomselect top "segname LIG"]
set water [atomselect top "resname TIP3"]
$solute writepdb solute.pdb
$water writepdb solvent.pdb

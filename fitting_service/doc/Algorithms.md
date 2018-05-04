# Algorithms #

Following document describes all algorithms supported by the API.

## LJ Fit ##

**Source File:** ljfit.py

**Function to call:** ljfit()

**Input files:**
 * [name].par CHARMM parameter file
 * [liq_name].pdb pure liquid PDB file
 * [solute_name].pdb solute molecule PDB file
 * [solvent_name].pdb solvent PDB file
 * [name].lpun MTPL punch file
 * [name].top CHARMM topology file

**Required parameters**
 * filename_charmm_topology (.top topology file for CHARMM)
 * filename_solute_pdb (.pdb structure file for solute molecule)
 * filename_solvent_pdb (.pdb file for solvent box)
 * filename_charmm_parameter (.par file for CHARMM)
 * filename_mtpl_lpun (.lpun file for CHARMM MTPL routine)
 * filename_pureliquid_pdb (.pdb file for pure liquid made of solute molecules)
 * ti_lambda_0 (initial lambda value for thermodynamic integration (usually 0.0))
 * ti_lambda_1 (final lambda value for thermodynamic integration (usually 1.0))
 * charmm_simulation_temperature (temperature for molecular dynamics simulations)

**Generated output**
 * molar_mass (solute molar mass)
 * pure_liquid_density (density of pure liquid)
 * vaporization_enthalpy (free energy of vaporization of pure liquid)
 * solvation_free_energy (free energy of solvation of solute molecule)

## MTP Fit - Generating ##

**Source File:** mtp-part1.py

**Function to call:** mtpfit_part1()

**Input files:**
 * [name].xyz coordinate file

**Required parameters**
 * filename_xyz name of uploaded xyz file
 * molecule_charge total charge of uploaded molecule
 * molecule_multiplicity spin multiplicity of molecule
 * gaussian_input_commandline e.g. MP2/aug-cc-PVDZ nosymm
 * gaussian_num_cores number of cores to use for Gaussian job

**Generated output**
 * [name].pot_mtpfittab.txt fitting table file
 * (not yet complete)

## MTP Fit - Fitting ##

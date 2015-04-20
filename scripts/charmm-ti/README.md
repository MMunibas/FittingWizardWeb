Thermodynamic integration with CHARMM
=========
Tristan BEREAU, University of Basel, 2013

Perform thermodynamic integration of gas-phase and solvated compounds in CHARMM.

## Installation

Clone the main repository

```bash
git clone git@github.com:tbereau/charmm.ti.git
```

go to the `charmm.ti` directory (which we'll call `$DIR`). It contains an empty directory `briz`, 
which needs to be cloned independently

```bash
git clone git@github.com:tbereau/briz.git
```

When using any of the scripts, do _not_ copy them away from the original directory, 
as some files rely on one another. Instead, either call them from their absolute path
or use symbolic links.

## `perform-ti`

the `perform-ti` script generates, runs/submits, and analyzes thermodynamic
integration (TI) simulations with CHARMM. 

### Usage

```bash
$DIR/perform-ti [-h] --ti {vdw,pc,mtp} [--chm CHARMM] --tps TPS [--top TOP]
                --par PAR --slu SLU [--slv SLV] [--lpun LPUN]
                [--lmb LMB LMB LMB] [--nst NSTEPS] [--neq NEQUIL]
                [--rem REMOTE] [--num NUMPROC] [--sub] [--dir SUBDIR]
                [--back] [--dual DUAL DUAL DUAL]

Options:
      -h                 help
      -ti {vdw,pc,mtp}   interactions to couple: van der Waals, point charges, or multipoles
      --chm CHARMM       location of the CHARMM executable on local machine
      --tps TPS          topology of solute molecule (required)
      --top TOP          optional topology file (can call option multiple times)
      --par PAR          parameter file (>=1x required; can call option multiple times)
      --slu SLU          solute PDB file
      --slv SLV          solvent PDB file (only for liquid simulations)
      --lpun LPUN        local PUN file for multipole simulations
      --lmb LMB LMB LMB  lambda spacing: START, SPACING, END (default: 0.0, 0.1, 1.0)
      --nst NSTEPS       total number of steps per simulation (includes equilibration)
      --neq NEQUIL       number of steps for equilibration
      --rem REMOTE       name of remote cluster
      --num NUMPROC      number of cores per simulation
      --sub              submit simulations and exit the script (otherwise the script waits)
      --dir SUBDIR       name of the SUBDIR on the remote cluster; useful to restart runs
      --back             backward simulations
      --dual ML1 ML2 DUA dual topology: provide molecular names of MOL1, MOL2, and DUAL (work in progress)
```

### Description

The script will generate all CHARMM input files to run TI using the selected lambda spacings.

The `--rem` option allows to submit all jobs to a remote cluster. The script then waits for 
all simulations to complete, gathers the data back to the local machine, and exits the script. 
Note that this option requires the configuration of the `hosts.ini` file in the `$DIR/briz` directory.
To do so, copy `$DIR/briz/hosts-sample.ini` to `$DIR/briz/hosts.ini` and adapt it to your needs. 

The `--lpun` option, coupled with `--ti mtp` will generate CHARMM input files for multipole simulation, 
using the MTPL module (coming to a future release of CHARMM; please email me for details). 

## `generate-solvated-box`

The `generate-solvated-box` solvates a single molecule in a box of water and equilibrates the 
box using CHARMM and VMD.

## Usage
```
$DIR/generate-solvated-box file.top file.pdb file.par

where
      file.top          topology file of the solute molecule
      file.pdb          PDB file of the solute molecule
      file.par          parameter file of the solute molecule
```

## `submit-ti-sims`

This script is a simple wrapper for `perform-ti` that runs all `pc`, `mtp`, and `vdw` 
TI simulations. See script for details.

## `extract-temp-dG`

Parses output log file of a `perform-ti` run and extracts the free energy of each lambda 
window. See script for more details.

## `calc-deltaG-hydr`

Reads out and display all free energies for forward and backward simulations in both vacuum 
and water. The script requires the presence of `pc`, `mtp`, and `vdw` contributions.  
See script for more details.



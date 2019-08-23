# Routine to divide a single pdb into 2 files: a solute PDB and a solvent PDB for
# use in T.I. simulations in the fitting wizard. The user supplies a pdb file and
# the residue name of the solute molecule, the script extracts the residue with
# that name from the PDB

# usage: divide_pdb.py -pdb <file.pdb> -res <resname>

import sys

def divide_pdb(pdbfile, resname, slufile, slvfile): 
  #pdbfile=''
  #resname=''
  
  ## parse arguments
  #for i in range(len(sys.argv)):
  #  if sys.argv[i] == '-pdb':
  #    pdbfile = sys.argv[i+1]
  #  elif sys.argv[i] == '-res':
  #    resname = sys.argv[i+1]
  #  elif sys.argv[i] == '-h':
  #    print("Usage: divide_pdb.py -pdb [pdbfile] -res [resname] [-h]")
  #
  #if pdbfile == '' or resname == '':
  #  print("Usage: divide_pdb.py -pdb [pdbfile] -res [resname] [-h]")
  #  raise Exception('Incorrect arguments to divide_pdb')
  
  # arrays to hold PDB file segments
  header=[]
  solute=[]
  solvent=[]
  
  # read and parse the solvated ligang PDB file
  try:
    f = open(pdbfile,'r')
  except:
    raise Exception('Could not find mtp file: '+pdbfile)

  a = f.readlines()
  f.close()
  resnum=0
  for line in a:
    b = line.split()
    rn = line[23:26]
    # store header lines
    if b[0].lower() == 'remark':
      header.append(line)
    # save "ATOM" lines as either "solute" or "solvent" as appropriate
    elif b[0].lower() == 'atom':
      tresname = line[17:21]
      if tresname.lower() == resname.lower():
        solute.append(line)
        if resnum == 0:
          resnum = rn
        # catch more than 1 ligand residue in PDB
        elif resnum != rn:
          raise Exception('More than one ligand residue of type '+resname+' found in PDB file '+pdbfile)
      else:
        solvent.append(line)
    # currently only handle simple PDBs with "REMARK", "ATOM", "TER" and "END" labels
    else:
      if b[0].lower() != 'ter' and b[0].lower() != 'end':
        raise Exception('Line '+line+' not understood when parsing file '+pdbfile)
  
  # catch no solute or no solvent found in PDB
  if len(solute) < 1:
    raise Exception('Couldn\'t find residue '+resname+' in PDB file '+pdbfile)
  
  if len(solvent) < 1:
    raise Exception('No solvent atoms found in PDB file '+pdbfile)
  
  # write new isolated solute pdb:
  slu = open(slufile,'w+')
  for line in header:
    slu.write(line)
  atm = 1
  for line in solute:
    # array describing PDB fixed format columns:
    columns = ((0,6),(6,10),(13,16),(17,21),(23,26),(30,37),(38,45),(46,53),
       (54,59),(60,65),(72,76))
    b = [ line[c[0]:c[1]] for c in columns ]
    slu.write('%-6s%5i %-4s %-4s %4i    %8.3f%8.3f%8.3f%6.2f%6.2f      %-4s\n' %
       (b[0],atm,b[2],b[3],1,float(b[5]),float(b[6]),float(b[7]),float(b[8]),
       float(b[9]),b[10]))
    atm=atm+1
  slu.write('%-6s%5i %-4s %-4s %4i\nEND' %
     ('TER',atm,'    ',b[3],1))
  
  # write solvent pdb without solute:
  slv = open(slvfile,'w+')
  for line in header:
    slv.write(line)
  atm = 1
  resnum = 1
  curres = ''
  for line in solvent:
    # array describing PDB fixed format columns:
    columns = ((0,6),(6,10),(13,16),(17,21),(23,26),(30,37),(38,45),(46,53),
       (54,59),(60,65),(72,76))
    b = [ line[c[0]:c[1]] for c in columns ]
    if b[4] != curres:
      resnum=resnum+1
      curres=b[4]
    slv.write('%-6s%5i %-4s %-4s %4i    %8.3f%8.3f%8.3f%6.2f%6.2f      %-4s\n' %
       (b[0],atm,b[2],b[3],resnum,float(b[5]),float(b[6]),float(b[7]),float(b[8]),
       float(b[9]),b[10]))
    atm=atm+1
  slv.write('%-6s%5i %-4s %-4s %4i\nEND' %
     ('TER',atm,'    ',b[3],resnum))
  
  

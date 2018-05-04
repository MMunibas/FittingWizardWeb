##########################################################################
# Routines containing templates to write Gaussian input files

# Routine to write gaussian energy evaluation input file to generate .chk file for ESP calculation
def write_gaussian_inp(ctx,xyz,gau_inp_name,chk_name,cmd,charge,multiplicity,ncore) :

    coords = []
    types = []
    with ctx.input_dir.open_file(xyz,"r") as xyz_file:
       nline=1
       for line in xyz_file:
          words = line.split()
          if nline==1:
             natm=int(words[0])
          if nline>2 and nline <= natm+2:
             types.append(words[0])
             coords.append([words[1],words[2],words[3]])
          nline += 1
             
    gauContent = """%nproc={ncore}
%chk={chk}
%mem=1000MB

#P {cmd}

Single point energy evaluation

{charge} {multiplicity}
""".format(cmd=cmd, chk=chk_name, charge=str(charge),multiplicity=str(multiplicity), ncore=str(ncore),
           inp_name=gau_inp_name)

    for i in range (0,natm):
       gauContent=gauContent+"{typ}  {x}  {y}  {z}\n".format(typ=types[i],x=coords[i][0],y=coords[i][1],z=coords[i][2])
    gauContent=gauContent+"\n"

    with ctx.input_dir.open_file(gau_inp_name, "w") as gau_inp_file:
       gau_inp_file.write(gauContent)
    gau_inp_file.close()

    return gau_inp_file


##########################################################################
# Routine to write GDMA analysis input file to generate starting charges for ESP fitting

def write_gdma_inp(ctx,workdir,gdma_inp_name,gdma_out_name,fchk_name,pun_name,mtp_order):

    gdmaContent = """Title GDMA charge analysis
 File {fchk}

 Angstrom
 Multipoles
 Limit {mtp_order}
 Punch {pun_name}
 Start

 Finish
""".format(fchk=fchk_name,mtp_order=str(mtp_order),pun_name=pun_name)

    with ctx.output_dir.subdir("mtp").open_file(gdma_inp_name,"w") as gdma_inp_file:
       gdma_inp_file.write(gdmaContent)
    gdma_inp_file.close()

    return gdma_inp_file


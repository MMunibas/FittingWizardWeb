#!/bin/bash
# Run optimization of gas-phase compound-water dimers.
#
# Tristan BEREAU (2013)

###############################################
# Directory of the lj-fit.pc-mtp package
# Resolves sym links.
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
    DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    SOURCE="$(readlink "$SOURCE")"
    # if    #$SOURCE was a relative symlink, we need to resolve it relative to
    #the path where the symlink file was located
    [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" 
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
################################################


[ ! -f $DIR/cfg_parser.sh ] && echo "Can't find cfg_parser.sh" && exit 1
source $DIR/cfg_parser.sh 
cfg_parser $DIR/config.ini
cfg.section.remote

[ -z "$4" ] && echo "missing arguments: mol.top mol.pdb mol.par atom_type_sel [Ncor]" \
    && echo "  where atom_type_sel is the atom type(s) around which water
    molecules should sample." && \
        echo "  Note: include multiple atom types using double quotes" && \
        echo "  Note: the top and par files should include TIP3P " && exit 1 

top=$1
pdb=$2
par=$3
typ=$4
ncor=1
[ ! -z "$5" ] && ncor=$5
base=`basename $pdb .pdb`


# Run generate dimers atom-spec namd
$DIR/generate.dimers.atom-spec.namd.sh $top $pdb $par "$typ" $ncor
[ $? -ne 0 ] && exit 1

# Convert PDB files to Gaussian input files
cd frames
[ -z $gaussext ] && echo \
    "Error. Can't read gaussext variable in config.ini" && exit 1
for i in *.pdb; do
    python $DIR/g03_diintE_inpmaker.py -in $i
    if [ $gaussext != "inp" ]; then
        mv `basename $i .pdb`.inp `basename $i .pdb`.$gaussext
    fi
done
cd ..

# ** Connection to remote cluster **
#
# Test connection
sshaddress=${user}@${hostname}
ssh $sshaddress "hostname" >/dev/null
[ $? -ne 0 ] && echo "Cannot connect to remote computer $hostname" && exit 1
# Create directory for calculations
existing_dirs=`ssh $sshaddress "ls $workdir"`
dir_index=0
dir_name=`printf "dir%03d\n" $index_trial`
max_index=999
while [[ $existing_dirs =~ $dir_name ]]; do
    let dir_index+=1
    dir_name=`printf "dir%03d\n" $dir_index`
    [ $dir_index -ge $max_index ] && \
        echo "Error. No free directory in remote cluster." && exit 1
done
ssh $sshaddress "mkdir -p $workdir/$dir_name"
rsync -az frames/*.$gaussext $sshaddress:$workdir/$dir_name 
echo "Submitting scripts to $hostname:$workdir/$dir_name"
ssh $sshaddress "cd $workdir/$dir_name; for i in *.$gaussext; do $gsub \$i; done"
# Now wait for all simulations to finish
notfinished=1
cat > checkfinished.sh <<EOF
cd $workdir/$dir_name
for i in *.$gaussext; do 
    outfile='none'
    trialfile=\`basename \$i .$gaussext\`.$gaussout
    [ -f \$trialfile ] && outfile=\$trialfile
    [ "\$outfile" == "none" ] && exit 1
done
exit 0
EOF
echo "Waiting for Gaussian calculations..."
# Wait for 72 hours at most.
maxwait=259200
waitcur=0
while [ $notfinished -eq 1 ]; do
    ssh $sshaddress 'bash -s' < checkfinished.sh
    notfinished=$?
    sleep 10
    let waitcur+=10
    [ $waitcur -ge $maxwait ] && echo "Error. Max waiting time has been reached." && \
        echo "Check Gaussian calculations at $hostname:$workdir/$dir_name" && exit 1
done
echo "Gaussian calculations finished."
rsync -az --exclude 'core.*' $sshaddress:$workdir/$dir_name/*.$gaussout frames/ 2>/dev/null
ssh $sshaddress "rm -rf $workdir/$dir_name"
rm checkfinished.sh


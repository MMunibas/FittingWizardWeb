#!/usr/bin/perl

# script to accompany a TI run in the web fitting environment. The script should:
# 1. read the solute pdb file to determine the residue name
# 2. read the topology file to get the atom types in this residue
# 3. read the parameter file to get initial values for LJ parameters for each atom type in our residue
# 4. write an input file section to scale the LJ parameters for these atom types by the requested factor

use strict;

if(@ARGV+0 != 7) { die "usage: scale-vdw-inp.pl <pdbfile> <topology file> <parameter file> <sigma factor> <eps factor> <path-to-inputs> <scaled parameter file name>\n"; }

my $res="NULL"; # holds residue type from PDB file
my $sigfac=$ARGV[3];
my $epsfac=$ARGV[4];
my $path=$ARGV[5];
my $newpar=$ARGV[6];

open(PDB,"<$ARGV[0]");

while(<PDB>){

  chomp;
  my @a=split;

  if(lc $a[0] eq lc "ATOM"){
    if(lc $a[3] ne lc $res && lc $res ne lc "NULL") {
      die "scale-vdw-inp.pl: more than one residue type found in pdb file!\n(this should contain the solute molecule only...)\n";}
    $res=$a[3];
  }

}

if(lc $res eq lc "NULL"){ die "no residue found in file $ARGV[0], exiting\n";}
print "Found residue \"$res\" in pdb file, now searching topology file $ARGV[1].\n";

open(TOP,"<$ARGV[1]");

my $flag=0;
my @types; # holds atom types in residue

while(<TOP>){

  chomp;
  my @a=split;

  if(lc $a[0] eq lc "END"){ last; }

  if(lc $a[0] eq lc "RESI" && lc $a[1] eq lc $res){
    $flag=1;
  }elsif(lc $a[0] eq lc "RESI" && lc $a[1] ne lc $res){
    $flag=0;
  }

  if(lc $a[0] eq lc "ATOM" && $flag==1){ # found an atom belonging to our residue
    my $tf=0;
    for(my $n=0; $n<@types+0; $n++){ # check whether this is a new type
      if(lc $types[$n] eq lc $a[2]){ $tf=1; }
    }
    if($tf==0){ # new atom type
      $types[@types+0]=$a[2];
    }
  }

}

my $ntype=@types+0;
print "Found $ntype atom types in topology file\nNow checking for atomic masses\n";

close(TOP);
open(TOP,"<$ARGV[1]");

my @t1; # flag array to record if we found atom type mass in topology
my @mass;

while(<TOP>){

  chomp;
  my @a=split;

  if(lc $a[0] eq lc "END"){ last; }

  if(lc $a[0] eq lc "MASS"){
    for(my $n=0; $n<@types+0; $n++){ # check whether this nonbonded parameter matches a known atom type
      if(lc $types[$n] eq lc $a[2]){ 
        $mass[$n]=$a[3];
        $t1[$n]=1;
      }
    }
  }

}

for(my $n=0; $n<@types+0; $n++){
  if($t1[$n] != 1) {die "did not find mass in topology file for atom type $types[$n]\n";}
}

print "Now reading parameter file $ARGV[2] to get initial values\n";

open(PAR,"<$ARGV[2]");

$flag=0;
my @sig;
my @eps;
my @onefour;
my @onefourflag;
my @tt; # flag array to record if we found atom type in parameter file
my $flag2=0;

my @parts1 = split ('/', $ARGV[2]);
my @parts = split ('\.', $parts1[@parts1-1]);
pop @parts;
my $file_no_ext = join '.', @parts;
#my $newpar=$file_no_ext."_scaled.par";
open(OUT,">$path/$newpar");

while(<PAR>){

  chomp;
  my $line=$_;
  my @a=split;

  $flag2=0;

  if(lc $a[0] eq lc "END"){ last; }

  if(lc $a[0] eq lc "NONBONDED"){
    $flag=1;
  }
  if(lc $a[0] eq lc "BONDS" || lc $a[0] eq lc "ANGLES" || lc $a[0] eq lc "DIHEDRALS" || lc $a[0] eq lc "IMPROPERS" || lc $a[0] eq lc "END"){
    $flag=0;
  }

  if($flag==1 && @a+0 >= 4){
    for(my $n=0; $n<@types+0; $n++){ # check whether this nonbonded parameter matches a known atom type
      if(lc $types[$n] eq lc $a[0]){ 
        $sig[$n]=$a[3];
        $eps[$n]=$a[2];
        my $wrd=$a[4];
        my $char=substr($wrd, 0, 1);
        $onefourflag[$n]=0;
        if($char ne '!' && @a+0 >= 7){
          $onefour[$n][0]=$a[4];
          $onefour[$n][1]=$a[5];
          $onefour[$n][2]=$a[6];
          $onefourflag[$n]=1;
        }
        $tt[$n]=1;
        if($mass[$n] < 0.99 || $mass[$n] > 1.1){ # not hydrogen
          if($onefourflag[$n] == 0){
            printf OUT "%6s  0.000   %7.4f   %7.4f  ! scaled VDW parameter, original values = $eps[$n], $sig[$n]\n",$types[$n],$epsfac*$eps[$n],$sigfac*$sig[$n];
          }else{
            printf OUT "%6s  0.000   %7.4f   %7.4f  %7.4f  %7.4f  %7.4f ! scaled VDW parameter, original values = $eps[$n], $sig[$n]\n",$types[$n],$epsfac*$eps[$n],$sigfac*$sig[$n],$onefour[$n][0],$onefour[$n][1],$onefour[$n][2];
          }
          $flag2=1;
        }elsif($mass[$n] >= 0.99 && $mass[$n] <= 1.1){
          print "Warning: ignoring atom $types[$n] with mass $mass[$n] as looks like hydrogen\n";
        }
      }
    }
  }

  if($flag2==0){ # not a line containing solute LJ parameter
    print OUT "$line\n";
  }

}

for(my $n=0; $n<@types+0; $n++){
  if($tt[$n] != 1) {die "did not find parameters in parameter file for atom type $types[$n]\n";}
}
print "Found parameters for all atoms in parameter file. Wrote new parameter file to epsfac.$epsfac.sigfac.$sigfac.par\n";

print "modified parameters:\n";
for(my $n=0; $n<@types+0; $n++){
  if($mass[$n] < 0.99 || $mass[$n] > 1.1){ # not hydrogen
    if($onefourflag[$n] == 0){
      printf "%6s  0.000   %7.4f   %7.4f\n",$types[$n],$epsfac*$eps[$n],$sigfac*$sig[$n];
    }else{
      printf "%6s  0.000   %7.4f   %7.4f  %7.4f  %7.4f  %7.4f\n",$types[$n],$epsfac*$eps[$n],$sigfac*$sig[$n],$onefour[$n][0],$onefour[$n][1],$onefour[$n][2];
    }
  }
}


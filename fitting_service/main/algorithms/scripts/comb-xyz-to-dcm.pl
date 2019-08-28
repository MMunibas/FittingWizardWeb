#!/usr/bin/perl

use strict;

if(@ARGV+0 != 3){
  die "usage: comb-xyz-to-dcm.pl <fitted-chgs.xyz> <frames.txt> <parfile.dcm>\n";
}

my $toang=0.529177249;

# get nuclear and charge coordinates
open(XYZ,"<$ARGV[0]");
my $line=1;
my $natm=0;
my @coords;
my $atm=0;

my $nq=0;
my $qatm=0;
my @qcoords;
my @q;

while(<XYZ>){

  chomp;
  my @a=split;

  if($line>2){
    if($a[0] ne "X" && $a[0] ne "x" && @a+0 >= 4){
      $coords[$atm][0]=$a[1];
      $coords[$atm][1]=$a[2];
      $coords[$atm++][2]=$a[3];
    }elsif(@a+0 >= 4){
      $qcoords[$qatm][0]=$a[1];
      $qcoords[$qatm][1]=$a[2];
      $qcoords[$qatm][2]=$a[3];
      $q[$qatm++]=$a[4];
    }
  }

  $line++;

}
close(XYZ);

$natm=$atm;
$nq=$qatm;

# match each chg to nearest nucleus:
my @hash;
for(my $i=0; $i<$nq; $i++){
  my $r=99999.99;
  for(my $j=0; $j<$natm; $j++){
    my $tr=(($qcoords[$i][0]-$coords[$j][0])**2+($qcoords[$i][1]-$coords[$j][1])**2+
           ($qcoords[$i][2]-$coords[$j][2])**2)**0.5;
    if($tr<$r){
      $hash[$i]=$j;
      $r=$tr;
    }
  }
  print "Chg $i belongs to atom $hash[$i] ($r Angstrom)\n";
}

# get local axis frames in molecule
my @fr;
my $nf=0;
my $resn;
$line=1;
open(FRA,"<$ARGV[1]");
while(<FRA>){

  chomp;
  my @a=split;

  if($line==1){ $resn=$a[0]; }

  if(@a+0==3){
    $fr[$nf][0]=$a[0]-1;
    $fr[$nf][1]=$a[1]-1;
    $fr[$nf++][2]=$a[2]-1;
  }
  $line++;

}

# calculate local axes and transform charges
open(OUT,">$ARGV[2]");
my @qdcm;
print OUT "1          ! no. residue types defined here\n\n";
print OUT "$resn       ! residue name\n";
print OUT "$nf          ! no. axis system frames\n";
for(my $n=0; $n<$nf; $n++){
 my $a1=$fr[$n][0];
 my $a2=$fr[$n][1];
 my $a3=$fr[$n][2];
 print OUT " ",$a1+1,"  ",$a2+1,"  ",$a3+1," BO  ! atom indices involved in frame  1\n";

 my $b1x=$coords[$a1][0]-$coords[$a2][0];
 my $b1y=$coords[$a1][1]-$coords[$a2][1];
 my $b1z=$coords[$a1][2]-$coords[$a2][2];
 my $rb1=sqrt($b1x**2+$b1y**2+$b1z**2);
 $b1x=$b1x/$rb1;
 $b1y=$b1y/$rb1;
 $b1z=$b1z/$rb1;

 my $b2x=$coords[$a3][0]-$coords[$a2][0];
 my $b2y=$coords[$a3][1]-$coords[$a2][1];
 my $b2z=$coords[$a3][2]-$coords[$a2][2];
 my $rb2=sqrt($b2x**2+$b2y**2+$b2z**2);
 $b2x=$b2x/$rb2;
 $b2y=$b2y/$rb2;
 $b2z=$b2z/$rb2;

 my @ex1;
 my @ey1;
 my @ez1;

 my @ex2;
 my @ey2;
 my @ez2;

 my @ex3;
 my @ey3;
 my @ez3;

# z-axes:
 $ez1[0]=$b1x;
 $ez1[1]=$b1y;
 $ez1[2]=$b1z;

 $ez2[0]=$b1x;
 $ez2[1]=$b1y;
 $ez2[2]=$b1z;

 $ez3[0]=$b2x;
 $ez3[1]=$b2y;
 $ez3[2]=$b2z;

# y-axes:
 $ey1[0]=$b1y*$b2z-$b1z*$b2y;
 $ey1[1]=$b1z*$b2x-$b1x*$b2z;
 $ey1[2]=$b1x*$b2y-$b1y*$b2x;
 my $rey=sqrt($ey1[0]**2+$ey1[1]**2+$ey1[2]**2);
 $ey1[0]/=$rey;
 $ey1[1]/=$rey;
 $ey1[2]/=$rey;


 $ey2[0]=$ey1[0];
 $ey2[1]=$ey1[1];
 $ey2[2]=$ey1[2];

 $ey3[0]=$ey1[0];
 $ey3[1]=$ey1[1];
 $ey3[2]=$ey1[2];

# x-axes:
 $ex1[0]=$b1y*$ey1[2]-$b1z*$ey1[1];
 $ex1[1]=$b1z*$ey1[0]-$b1x*$ey1[2];
 $ex1[2]=$b1x*$ey1[1]-$b1y*$ey1[0];
 my $rex=sqrt($ex1[0]**2+$ex1[1]**2+$ex1[2]**2);
 $ex1[0]/=$rex;
 $ex1[1]/=$rex;
 $ex1[2]/=$rex;

 $ex2[0]=$ex1[0];
 $ex2[1]=$ex1[1];
 $ex2[2]=$ex1[2];

 $ex3[0]=$b2y*$ey1[2]-$b2z*$ey1[1];
 $ex3[1]=$b2z*$ey1[0]-$b2x*$ey1[2];
 $ex3[2]=$b2x*$ey1[1]-$b2y*$ey1[0];
 my $rex=sqrt($ex3[0]**2+$ex3[1]**2+$ex3[2]**2);
 $ex3[0]/=$rex;
 $ex3[1]/=$rex;
 $ex3[2]/=$rex;

 # define chg coords relative to nuclei
 my $nqa=0;
 for(my $i=0;$i<$nq;$i++){
   if($hash[$i]==$fr[$n][0]){
    #check if atom exists in another frame already
    my $rpt=0;
    for(my $j=0; $j<$n; $j++){
      if($fr[$n][0]==$fr[$j][0] || $fr[$n][0]==$fr[$j][1] || $fr[$n][0]==$fr[$j][2]){
        $rpt=1;
      }
    }
    if($rpt==0){
     my $nn=$hash[$i];
     my $lx=$qcoords[$i][0]-$coords[$nn][0];
     my $ly=$qcoords[$i][1]-$coords[$nn][1];
     my $lz=$qcoords[$i][2]-$coords[$nn][2];

     my $tlx=$lx*$ex1[0]+$ly*$ex1[1]+$lz*$ex1[2];
     my $tly=$lx*$ey1[0]+$ly*$ey1[1]+$lz*$ey1[2];
     my $tlz=$lx*$ez1[0]+$ly*$ez1[1]+$lz*$ez1[2];

     $qdcm[$nqa][0]=$tlx;
     $qdcm[$nqa][1]=$tly;
     $qdcm[$nqa][2]=$tlz;
     $qdcm[$nqa][3]=$q[$i];

     $nqa++;
    }
   }
 }
 print OUT "$nqa  0          ! no. chgs and polarizable sites for atom  ",$fr[$n][0]+1,"\n";
 for(my $i=0;$i<$nqa;$i++){
   printf OUT "     %13.10f      %13.10f      %13.10f      %13.10f\n",$qdcm[$i][0],$qdcm[$i][1],$qdcm[$i][2],$qdcm[$i][3];
 }

 $nqa=0;
 for(my $i=0;$i<$nq;$i++){
   if($hash[$i]==$fr[$n][1]){
#check if atom exists in another frame already
    my $rpt=0;
    for(my $j=0; $j<$n; $j++){
      if($fr[$n][1]==$fr[$j][0] || $fr[$n][1]==$fr[$j][1] || $fr[$n][1]==$fr[$j][2]){
        $rpt=1;
      }
    }
    if($rpt==0){
     my $nn=$hash[$i];
     my $lx=$qcoords[$i][0]-$coords[$nn][0];
     my $ly=$qcoords[$i][1]-$coords[$nn][1];
     my $lz=$qcoords[$i][2]-$coords[$nn][2];
 my $tr=sqrt($lx**2+$ly**2+$lz**2);
     my $tlx=$lx*$ex2[0]+$ly*$ex2[1]+$lz*$ex2[2];
     my $tly=$lx*$ey2[0]+$ly*$ey2[1]+$lz*$ey2[2];
     my $tlz=$lx*$ez2[0]+$ly*$ez2[1]+$lz*$ez2[2];
 my $tr=sqrt($tlx**2+$tly**2+$tlz**2);
     $qdcm[$nqa][0]=$tlx;
     $qdcm[$nqa][1]=$tly;
     $qdcm[$nqa][2]=$tlz;
     $qdcm[$nqa][3]=$q[$i];
     $nqa++;
    }
   }
 }
 print OUT "$nqa  0          ! no. chgs and polarizable sites for atom  ",$fr[$n][1]+1,"\n";
 for(my $i=0;$i<$nqa;$i++){
   printf OUT "     %13.10f      %13.10f      %13.10f      %13.10f\n",$qdcm[$i][0],$qdcm[$i][1],$qdcm[$i][2],$qdcm[$i][3];
 }

 $nqa=0;
 for(my $i=0;$i<$nq;$i++){
   if($hash[$i]==$fr[$n][2]){
#check if atom exists in another frame already
    my $rpt=0;
    for(my $j=0; $j<$n; $j++){
      if($fr[$n][2]==$fr[$j][0] || $fr[$n][2]==$fr[$j][1] || $fr[$n][2]==$fr[$j][2]){
        $rpt=1;
      }
    }
    if($rpt==0){
     my $nn=$hash[$i];
     my $lx=$qcoords[$i][0]-$coords[$nn][0];
     my $ly=$qcoords[$i][1]-$coords[$nn][1];
     my $lz=$qcoords[$i][2]-$coords[$nn][2];

     my $tlx=$lx*$ex3[0]+$ly*$ex3[1]+$lz*$ex3[2];
     my $tly=$lx*$ey3[0]+$ly*$ey3[1]+$lz*$ey3[2];
     my $tlz=$lx*$ez3[0]+$ly*$ez3[1]+$lz*$ez3[2];

     $qdcm[$nqa][0]=$tlx;
     $qdcm[$nqa][1]=$tly;
     $qdcm[$nqa][2]=$tlz;
     $qdcm[$nqa][3]=$q[$i];
     $nqa++;
    }
   }
 }
 print OUT "$nqa  0          ! no. chgs and polarizable sites for atom  ",$fr[$n][2]+1,"\n";
 for(my $i=0;$i<$nqa;$i++){
   printf OUT "     %13.10f      %13.10f      %13.10f      %13.10f\n",$qdcm[$i][0],$qdcm[$i][1],$qdcm[$i][2],$qdcm[$i][3];
 }

}
print OUT "\n";
close(OUT);
print "\n\nOutput written to file $ARGV[2]\n\n";


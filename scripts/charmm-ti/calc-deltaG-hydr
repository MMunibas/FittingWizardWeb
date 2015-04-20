#!/bin/bash

# Initialize variables
bothdirections=0
nsteps=20000
lambda_step=0.1
direnv=("." ".")
currentfilename=
returnValue=

function showHelp
{
  echo "Usage:"
  echo "$0 [-b] [-n N] [-l N] [-v dir] [-w dir]"
  echo ""
  echo " -b: include backward simulations"
  echo " -n: nsteps (default: $nsteps)"
  echo " -l: lambda window spacing (default: $lambda_step)"
  echo " -v: directory containing vacuum simulations"
  echo " -w: directory containing water simulations"
}

function getFileName
{
  env=${direnv[$1]}
  simtype=$2
  filenamedirection=$3
  currentfilename="$env/ti.$simtype.$filenamedirection.$nsteps.$lambda_step.out"
  return
}

function existsOrDie
{
  [ ! -f $currentfilename ] && echo "Missing file $currentfilename" && exit 1
}

function extractDataFromFile
{
  simtype=$2
  filenamedirection=$3
  getFileName $1 $simtype $filenamedirection
  existsOrDie
  tail -n 1 $currentfilename | grep "Normal termination" > /dev/null
  [ $? -eq 1 ] && \
    echo "File $currentfilename did not complete successfully" && \
    exit 1
  returnValue=`tail -n 2 $currentfilename | head -n 1 | awk '{print $5}'`
  return
}

OPTIND=1
while getopts "h?:n:l:v:w:b" opt
do
  case "$opt" in
    h|\?)
      showHelp
      exit 0
      ;;
    n)
      nsteps=$OPTARG
      echo "option nsteps: $nsteps"
      ;;
    l)
      lambda_step=$OPTARG
      echo "option lambda window spacing: $lambda_step"
      ;;
    v)
      direnv[0]=$OPTARG
      echo "option vacuum directory: ${direnv[0]}"
      ;;
    w)
      direnv[1]=$OPTARG
      echo "option water directory: ${direnv[1]}"
      ;;
    b)
      bothdirections=1
      echo "option include backward simulations turned on"
      ;;
  esac
done
shift $((OPTIND-1)) # Shift off the options

deltaGpcf="0.0"
deltaGmtpf="0.0"
# Options for filename:
# Vacuum is 0; water is 1
# "vdw", "pcsg", "mtp"
# "f" or "b"
extractDataFromFile 0 "vdw" "f"
deltaGpcf=`echo "$deltaGpcf - $returnValue" | bc -l`
deltaGmtpf=`echo "$deltaGmtpf - $returnValue" | bc -l`
extractDataFromFile 0 "pcsg" "f"
deltaGpcf=`echo "$deltaGpcf - $returnValue" | bc -l`
extractDataFromFile 0 "mtp" "f"
deltaGmtpf=`echo "$deltaGmtpf - $returnValue" | bc -l`

extractDataFromFile 1 "vdw" "f"
deltaGpcf=`echo "$deltaGpcf + $returnValue" | bc -l`
deltaGmtpf=`echo "$deltaGmtpf + $returnValue" | bc -l`
extractDataFromFile 1 "pcsg" "f"
deltaGpcf=`echo "$deltaGpcf + $returnValue" | bc -l`
extractDataFromFile 1 "mtp" "f"
deltaGmtpf=`echo "$deltaGmtpf + $returnValue" | bc -l`

echo ""

if [ $bothdirections -eq 1 ]; then
  deltaGpcb="0.0"
  deltaGmtpb="0.0"
  # Options for filename:
  # Vacuum is 0; water is 1
  # "vdw", "pcsg", "mtp"
  # "f" or "b"
  extractDataFromFile 0 "vdw" "b"
  deltaGpcb=`echo "$deltaGpcb + $returnValue" | bc -l`
  deltaGmtpb=`echo "$deltaGmtpb + $returnValue" | bc -l`
  extractDataFromFile 0 "pcsg" "b"
  deltaGpcb=`echo "$deltaGpcb + $returnValue" | bc -l`
  extractDataFromFile 0 "mtp" "b"
  deltaGmtpb=`echo "$deltaGmtpb + $returnValue" | bc -l`
  
  extractDataFromFile 1 "vdw" "b"
  deltaGpcb=`echo "$deltaGpcb - $returnValue" | bc -l`
  deltaGmtpb=`echo "$deltaGmtpb - $returnValue" | bc -l`
  extractDataFromFile 1 "pcsg" "b"
  deltaGpcb=`echo "$deltaGpcb - $returnValue" | bc -l`
  extractDataFromFile 1 "mtp" "b"
  deltaGmtpb=`echo "$deltaGmtpb - $returnValue" | bc -l`
  avgpc=`echo "($deltaGpcf+$deltaGpcb)*0.5" | bc -l`
  avgmtp=`echo "($deltaGmtpf+$deltaGmtpb)*0.5" | bc -l`
  stdpc=`echo "sqrt(($deltaGpcf-1.*$avgpc)^2+($deltaGpcb-1.*$avgpc)^2/2.)" | bc -l`
  stdmtp=`echo "sqrt(($deltaGmtpf-1.*$avgmtp)^2+($deltaGmtpb-1.*$avgmtp)^2/2.)" | bc -l`
  printf "PC : %7.4f +/- %7.4f kcal/mol\n" $avgpc  $stdpc
  printf "MTP: %7.4f +/- %7.4f kcal/mol\n" $avgmtp $stdmtp
else
  printf "PC : %7.4f kcal/mol\n" $deltaGpcf 
  printf "MTP: %7.4f kcal/mol\n" $deltaGmtpf
fi
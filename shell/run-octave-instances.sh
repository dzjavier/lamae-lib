#!/bin/sh -f
FILE=$1
OFILE=`echo $FILE | sed 's/.$//' | sed 's/.$//'`"-output.txt"
EFILE=`echo $FILE | sed 's/.$//' | sed 's/.$//'`"-error.txt"
echo "Running -> " $FILE
echo "Octave Output -> " $OFILE 
echo "Octave Error -> " $EFILE 
nohup octave -q $FILE > $OFILE 2> $EFILE &
echo "Command Started"

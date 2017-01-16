#!/bin/bash

#ARGS: 
# - $1: THE REASONER ID

# THE LIST OF THE URIS/PATHS TO THE ONTOLOGIES 
# MUST BE IN ontologySet.txt FILE
# THIS SCRIPT READS ontologySet.txt LINE BY LINE
# AND LAUNCHES A TIMED TASK

# TIMEOUT TAKES THE TIMEOUT AS FIRST ARGUMENT 
# E.G., 30M == 30 MINUTES, 10S == 10 SECONDS, ETC
# IF THE TIMEMOUT IS REACHED, THE RETURNED 
# VALUE IS -124, AND WE LOG THE TIMEOUT IN A 
# DIFFERENT FILE

TIMESOUT_FILE="$1timesout.txt"

while read -r line
do
	echo "Processing $line "
	timeout 30m ./expStatTask.sh $1 $line

	if [ $? -eq 124 ]; then
		echo "$line timeout">> $TIMESOUT_FILE
	fi

done < "ontologySet.txt"

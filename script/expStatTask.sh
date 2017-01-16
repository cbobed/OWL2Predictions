#!/bin/bash

#ADD AS MANY LINES AS THE FOLLOWING TO BUILD THE CLASSPATH 
CLASSPATH=$CLASSPATH:./lib/owlapi/v3/owlapi-apibinding-3.5.5.jar
CLASSPATH=$CLASSPATH:./lib/owlapi/v3/owlapi-distribution-3.5.5.jar

## MANY ARE MISSING!!! 

## THIS ASSUMES THAT THE LIBRARY FOR FACTPP IS IN THE SAME DIRECTORY
## AS THE SCRIPT
java -cp $CLASSPATH -Djava.library.path=./ -Xss1g -Xms2g -Xmx14g sid.owl2predictions.utils.TimeHarvester $*


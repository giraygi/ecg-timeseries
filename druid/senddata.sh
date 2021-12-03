#!/bin/bash

####################################
#  SEND LOCAL FILE IN THE FIRST ARGUMENT TO DIRECTORY
#  OF ALL DRUID DOCKER CONTAINERS IN THE SECOND ARGUMENT 
####################################

args=("$@")
echo "Copying ${args[0]} to docker container directory ${args[1]}"
docker cp ${args[0]} middlemanager:${args[1]}
docker cp ${args[0]} router:${args[1]}
docker cp ${args[0]} historical:${args[1]}
docker cp ${args[0]} broker:${args[1]}
docker cp ${args[0]} coordinator:${args[1]}


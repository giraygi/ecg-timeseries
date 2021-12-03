#!/bin/bash

####################################
#  REMOVES FILE FROM ALL DRUID CONTAINERS
####################################

args=("$@")
echo "Remove ${args[0]} from all druid docker containers"
docker exec middlemanager rm -rf ${args[0]}
docker exec router rm -rf ${args[0]}
docker exec historical rm -rf ${args[0]}
docker exec broker rm -rf ${args[0]}
docker exec coordinator rm -rf ${args[0]}


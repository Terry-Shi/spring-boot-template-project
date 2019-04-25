#!/bin/bash

PROJECT_HOME="$(cd "$(dirname "$0")/" && pwd -P)"
echo $PROJECT_HOME

gradle build -x test
docker build -t spring-boot-template-project:1.0 -f ./docker/Dockerfile .

#cd ${PROJECT_HOME}/docker-compose
#docker-compose up -d
# docker-compose down


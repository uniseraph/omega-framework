#!/usr/bin/env bash

SERVICE_NAME=$1
VERSION=$2


pssh -h hosts/$SERVICE_NAME -l admin -i 'mkdir -p /home/admin/services/lib'
pscp -h hosts/$SERVICE_NAME  -l  admin  ./lib-repo/${SERVICE_NAME}-${VERSION}.jar /home/admin/services/lib/

pssh -h hosts/$SERVICE_NAME -l admin -i ' curl -fsSL -X POST http://localhost:8080/shutdown ;  \
                                sleep 5 &&  \
                               java -Djava.security.egd=file:/dev/./urandom -jar /home/admin/service/lib/${SERVICE_NAME}-${VERSION}.jar \
                               --spring.cloud.config.discovery.enabled=true  \
                               --spring.cloud.config.profile=test  \
                               --spring.cloud.config.label=master  \
                               --eureka.client.serviceUrl.defaultZone=http://eureka1:8080/eureka/,http://eureka2:8080/eureka,http://eureka3:8080/eureka \
                               '

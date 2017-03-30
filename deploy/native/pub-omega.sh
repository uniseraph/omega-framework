#!/usr/bin/env bash

SERVICE_NAME=$1

pscp -h hosts/$SERVICE_NAME  -l  admin  ./omega-framework/lib/omega-framework-${SERVICE_NAME}*.jar /home/admin/omega-framework/lib/

pssh -h hosts/$SERVICE_NAME -l admin -i ' curl -X POST http://localhost:8080/shutdown ;  \
                                sleep 5 &&  \
                               java -Djava.security.egd=file:/dev/./urandom -jar /home/admin/omega-framework/lib/omega-framework-${SERVICE_NAME}-0.1.jar \
                               --server.port=8080  \
                               --eureka.client.serviceUrl.defaultZone=http://eureka1:8080/eureka/,http://eureka2:8080/eureka,http://eureka3:8080/eureka \
                               '

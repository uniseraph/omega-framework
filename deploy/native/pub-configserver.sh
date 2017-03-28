#!/usr/bin/env bash


pscp -h hosts/configserver  -l  root  ./omega-framework/lib/omega-framework-configserver*.jar /opt/omega-framework/lib/

pssh -h hosts/configserver -l root -i ' curl -X POST http://localhost:8080/shutdown ;  \
                               cd /opt/omega-framework ; \
                               java -Djava.security.egd=file:/dev/./urandom -jar lib/omega-framework-configserver-0.1.jar \
                               --server.port=8080  \
                               --spring.rabbitmq.host=rabbitmq \
                               --eureka.client.serviceUrl.defaultZone=http://eureka1:8080/eureka/ \
                               '

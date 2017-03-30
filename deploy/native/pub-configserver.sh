#!/usr/bin/env bash


pscp -h hosts/configserver  -l  admin  ../../lib/omega-framework-configserver*.jar /home/admin/omega-framework/lib/

pssh -h hosts/configserver -l admin -i ' curl -fsSL -X POST http://localhost:8080/shutdown ;\
                               sleep 5 &&
                               cd /home/admin/omega-framework && \
                               java -Djava.security.egd=file:/dev/./urandom -jar lib/omega-framework-configserver-0.1.jar \
                               --server.port=8080  \
                               --spring.rabbitmq.host=rabbitmq \
                               --eureka.client.serviceUrl.defaultZone=http://eureka1:8080/eureka/,http://eureka2:8080/eureka,http://eureka3:8080/eureka \
                               '

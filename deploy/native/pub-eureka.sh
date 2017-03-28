#!/usr/bin/env bash


pscp -h hosts/eureka  -l  root  ../../lib/omega-framework-eureka*.jar /opt/omega-framework/lib/


pssh -h hosts/eureka1 -l root -i ' curl -X POST http://localhost:8080/shutdown '
sleep 3
pssh -h hosts/eureka1 -l root -i ' java -Djava.security.egd=file:/dev/./urandom -jar /opt/omega-framework/lib/omega-framework-eureka-0.1.jar \
                               --server.port=8080 --spring.profiles.active=dev'

#pssh -h hosts/eureka1 -l root -i ' curl -X POST http://localhost:8080/shutdown ;  \
#                               cd /opt/omega-framework ; \
#                               java -Djava.security.egd=file:/dev/./urandom -jar lib/omega-framework-eureka-0.1.jar \
#                               --server.port=8080 --spring.profiles.active=eureka1 \
#                               --eureka1.instance.hostname=eureka1 \
#                               --eureka2.instance.hostname=eureka2 \
#                               --eureka3.instance.hostname=eureka3 '

#pssh -h hosts/eureka2 -l root -i ' curl -X POST http://localhost:8080/shutdown ;  \
#                               cd /opt/omega-framework ; \
#                               java -Djava.security.egd=file:/dev/./urandom -jar lib/omega-framework-eureka-0.1.jar \
#                               --server.port=8080 --spring.profiles.active=eureka1 \
#                               --eureka1.instance.hostname=eureka1 \
#                               --eureka2.instance.hostname=eureka2 \
#                               --eureka3.instance.hostname=eureka3 '
#
#pssh -h hosts/eureka3 -l root -i ' curl -X POST http://localhost:8080/shutdown ;  \
#                               cd /opt/omega-framework ; \
#                               java -Djava.security.egd=file:/dev/./urandom -jar lib/omega-framework-eureka-0.1.jar \
#                               --server.port=8080 --spring.profiles.active=eureka1 \
#                               --eureka1.instance.hostname=eureka1 \
#                               --eureka2.instance.hostname=eureka2 \
#                               --eureka3.instance.hostname=eureka3 '

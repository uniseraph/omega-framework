FROM  openjdk:8-alpine

RUN mkdir -p /opt/omega-framework/lib
ADD target/omega-framework-eureka-0.1.jar /opt/omega-framework/lib

WORKDIR /opt/omega-framework

ENTRYPOINT exec java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom  -jar  lib/omega-framework-eureka-0.1.jar $*

#!/usr/bin/env bash




function cleanup() {

    curl --connect-timeout 1 -fsSL -X POST http://localhost:8080/shutdown

}
trap cleanup SIGTERM

java ${JAVA_OPTS}  -Djava.security.egd=file:/dev/./urandom -jar lib/omega-framework-configserver-0.1.jar $*
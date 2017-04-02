#!/usr/bin/env bash


java ${JAVA_OPTS}  -Djava.security.egd=file:/dev/./urandom -jar lib/omega-framework-configserver-0.1.jar $*
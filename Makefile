SHELL = /bin/bash

MAJOR_VERSION = $(shell cat VERSION)
GIT_VERSION   = $(shell git log -1 --pretty=format:%h)
GIT_NOTES     = $(shell git log -1 --oneline)



IMAGE_NAME = omega-reg/omega-framework
REGISTRY   = registry.cn-hangzhou.aliyuncs.com

build:
	mvn clean package -Dmaven.test.skip=true

image:
	docker build --rm -t ${IMAGE_NAME}:${MAJOR_VERSION} omega-framework-assembly

push:
	docker tag ${IMAGE_NAME}:${MAJOR_VERSION} ${IMAGE_NAME}:${MAJOR_VERSION}-${GIT_VERSION}
	docker tag ${IMAGE_NAME}:${MAJOR_VERSION} ${REGISTRY}/${IMAGE_NAME}:${MAJOR_VERSION}
	docker tag ${IMAGE_NAME}:${MAJOR_VERSION} ${REGISTRY}/${IMAGE_NAME}:${MAJOR_VERSION}-${GIT_VERSION}
	docker push ${REGISTRY}/${IMAGE_NAME}:${MAJOR_VERSION}-${GIT_VERSION}
	docker push ${REGISTRY}/${IMAGE_NAME}:${MAJOR_VERSION}

shell:
	docker run -ti --rm  -w /opt/omega-framework-assembly-${MAJOR_VERSION}  ${IMAGE_NAME}:${MAJOR_VERSION}  sh

eureka:
	docker run -d  -w /opt/omega-framework-assembly-${MAJOR_VERSION}  ${IMAGE_NAME}:${MAJOR_VERSION}  java -jar lib/omega-framework-eureka-0.1.jar


.PHONY: build image push

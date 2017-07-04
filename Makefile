SHELL = /bin/bash

MAJOR_VERSION = $(shell cat VERSION)
GIT_VERSION   = $(shell git log -1 --pretty=format:%h)
GIT_NOTES     = $(shell git log -1 --oneline)



IMAGE_NAME = omega-reg/omega-framework
REGISTRY   = registry.cn-hangzhou.aliyuncs.com

build:
	mvn clean package -Dmaven.test.skip=true
	cp omega-framework-assembly/target/omega-framework-assembly-0.1-bin.tar.gz omega-framework-assembly/target/omega-framework-assembly-0.1-${GIT_VERSION}-bin.tar.gz


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


image-eureka:
	docker build --rm -t ${IMAGE_NAME}-eureka:${MAJOR_VERSION} omega-framework-eureka


push-eureka:
	docker tag ${IMAGE_NAME}-eureka:${MAJOR_VERSION} ${IMAGE_NAME}-eureka:${MAJOR_VERSION}-${GIT_VERSION}
	docker tag ${IMAGE_NAME}-eureka:${MAJOR_VERSION} ${REGISTRY}/${IMAGE_NAME}-eureka:${MAJOR_VERSION}
	docker tag ${IMAGE_NAME}-eureka:${MAJOR_VERSION} ${REGISTRY}/${IMAGE_NAME}-eureka:${MAJOR_VERSION}-${GIT_VERSION}
	docker push ${REGISTRY}/${IMAGE_NAME}-eureka:${MAJOR_VERSION}-${GIT_VERSION}
	docker push ${REGISTRY}/${IMAGE_NAME}-eureka:${MAJOR_VERSION}




image-configserver:
	docker build --rm -t ${IMAGE_NAME}-configserver:${MAJOR_VERSION} omega-framework-configserver


push-configserver:
	docker tag ${IMAGE_NAME}-configserver:${MAJOR_VERSION} ${IMAGE_NAME}-configserver:${MAJOR_VERSION}-${GIT_VERSION}
	docker tag ${IMAGE_NAME}-configserver:${MAJOR_VERSION} ${REGISTRY}/${IMAGE_NAME}-configserver:${MAJOR_VERSION}
	docker tag ${IMAGE_NAME}-configserver:${MAJOR_VERSION} ${REGISTRY}/${IMAGE_NAME}-configserver:${MAJOR_VERSION}-${GIT_VERSION}
	docker push ${REGISTRY}/${IMAGE_NAME}-configserver:${MAJOR_VERSION}-${GIT_VERSION}
	docker push ${REGISTRY}/${IMAGE_NAME}-configserver:${MAJOR_VERSION}

.PHONY: build image push

FROM  registry.cn-hangzhou.aliyuncs.com/zanecloud/java:8-jce

RUN mkdir -p /opt/omega-framework/lib
ADD target/omega-framework-configserver-0.1.jar /opt/omega-framework/lib
VOLUME /opt/omega-framework/config-repo

WORKDIR /opt/omega-framework

ENTRYPOINT exec java   ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar lib/omega-framework-configserver-0.1.jar $*
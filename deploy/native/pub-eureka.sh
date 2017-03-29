#!/usr/bin/env bash


pscp -h hosts/eureka  -l  root  ../../lib/omega-framework-eureka*.jar /opt/omega-framework/lib/

pssh -h hosts/eureka1 -l root -i ' curl -X POST http://localhost:8080/shutdown ;  \
                               sleep 5 && \
                               cd /opt/omega-framework && \
                               java -Djava.security.egd=file:/dev/./urandom -jar lib/omega-framework-eureka-0.1.jar \
                               --server.port=8080 --spring.profiles.active=eureka1 \
                               --eureka1.instance.hostname=eureka1 \
                               --eureka2.instance.hostname=eureka2 \
                               --eureka3.instance.hostname=eureka3 '
  SECONDS=0
  while [[ $(curl -fsSL http://eureka1:8080/health 2>&1 1>/dev/null; echo $?) != 0 ]]; do
    ((SECONDS++))
    if [[ ${SECONDS} == 100 ]]; then
      echo "eureka1 failed to start. Exiting..."
      exit 1
    fi
    sleep 1
  done

pssh -h hosts/eureka2 -l root -i ' curl -X POST http://localhost:8080/shutdown ;  \
                                sleep 5 && \
                               cd /opt/omega-framework && \
                               java -Djava.security.egd=file:/dev/./urandom -jar lib/omega-framework-eureka-0.1.jar \
                               --server.port=8080 --spring.profiles.active=eureka1 \
                               --eureka1.instance.hostname=eureka1 \
                               --eureka2.instance.hostname=eureka2 \
                               --eureka3.instance.hostname=eureka3 '
  SECONDS=0
  while [[ $(curl -fsSL http://eureka2:8080/health 2>&1 1>/dev/null; echo $?) != 0 ]]; do
    ((SECONDS++))
    if [[ ${SECONDS} == 100 ]]; then
      echo "eureka2 failed to start. Exiting..."
      exit 1
    fi
    sleep 1
  done


pssh -h hosts/eureka3 -l root -i ' curl -X POST http://localhost:8080/shutdown ;  \
                               cd /opt/omega-framework ; \
                               java -Djava.security.egd=file:/dev/./urandom -jar lib/omega-framework-eureka-0.1.jar \
                               --server.port=8080 --spring.profiles.active=eureka1 \
                               --eureka1.instance.hostname=eureka1 \
                               --eureka2.instance.hostname=eureka2 \
                               --eureka3.instance.hostname=eureka3 '
  SECONDS=0
  while [[ $(curl -fsSL http://eureka3:8080/health 2>&1 1>/dev/null; echo $?) != 0 ]]; do
    ((SECONDS++))
    if [[ ${SECONDS} == 100 ]]; then
      echo "eureka3 failed to start. Exiting..."
      exit 1
    fi
    sleep 1
  done

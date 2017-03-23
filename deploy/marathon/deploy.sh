#!/usr/bin/env bash

#deploy rabbitmq witch rabbitmq.mesos.consul

curl -i -H 'Content-Type: application/json' -d @rabbitmq.json  kvm1:8080/v2/apps



# deploy eureka cluster with eureka1/2/3.mesos.consul
curl -i -H 'Content-Type: application/json' -d @eureka1.json  kvm1:8080/v2/apps
curl -i -H 'Content-Type: application/json' -d @eureka2.json  kvm1:8080/v2/apps
curl -i -H 'Content-Type: application/json' -d @eureka3.json  kvm1:8080/v2/apps



# deploy configserver with configserver.mesos.consul
curl -i -H 'Content-Type: application/json' -d @configserver.json  kvm1:8080/v2/apps


curl -i -H 'Content-Type: application/json' -d @es.json  kvm1:8080/v2/apps


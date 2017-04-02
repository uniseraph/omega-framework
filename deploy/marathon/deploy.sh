#!/usr/bin/env bash

#deploy rabbitmq witch rabbitmq.mesos.consul

curl -i -H 'Content-Type: application/json' -d @rabbitmq.json  kvm1:8080/v2/apps

#docker run -ti --rm   activatedgeek/rabbitmqadmin:latest --vhost=ongo360_vhost  --username=ongo360 --password=ongo360  --host=rabbitmq.service.consul  --port=15672       declare exchange name=task durable=true type=direct

#docker run -ti --rm   activatedgeek/rabbitmqadmin:latest --vhost=ongo360_vhost  --username=ongo360 --password=ongo360  --host=rabbitmq.service.consul --port=15672     declare queue name=sendEmail durable=true

#docker run -ti --rm   activatedgeek/rabbitmqadmin:latest --vhost=ongo360_vhost  --username=ongo360 --password=ongo360  --host=rabbitmq.service.consul --port=15672     declare binding source=task destination=sendEmail


# deploy eureka cluster with eureka1/2/3.mesos.consul
curl  -H 'Content-Type: application/json' -d @eureka1.json  kvm1:8080/v2/apps
curl  -H 'Content-Type: application/json' -d @eureka2.json  kvm1:8080/v2/apps
curl  -H 'Content-Type: application/json' -d @eureka3.json  kvm1:8080/v2/apps



# deploy configserver with configserver.mesos.consul
curl  -H 'Content-Type: application/json' -d @configserver.json  kvm1:8080/v2/apps


curl  -H 'Content-Type: application/json' -d @es.json  kvm1:8080/v2/apps


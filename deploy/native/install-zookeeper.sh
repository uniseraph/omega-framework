#!/usr/bin/env bash

pscp -h hosts/zookeeper -l root ./binary/zookeeper-3.4.9.tar.gz /root/zookeeper-3.4.9.tar.gz


pssh -h hosts/zookeeper -l root -i ' tar zxvf /root/zookeeper-3.4.9.tar.gz -C /opt && \
                                     cd /opt/zookeeper-3.4.9 && \
                                     cp conf/zoo_sample.cfg conf/zoo.cfg &&              \
                                     bash bin/zkServer.sh restart '
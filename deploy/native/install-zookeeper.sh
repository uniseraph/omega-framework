#!/usr/bin/env bash

curr_path=`pwd`
cd `dirname $0`

pscp -h hosts/zookeeper -l admin ./binary/zookeeper-3.4.9.tar.gz /home/admin/zookeeper-3.4.9.tar.gz

pssh -h hosts/zookeeper -l admin -i ' rm -rf /home/admin/zookeeper-3.4.9 && \
                                     tar zxvf /home/admin/zookeeper-3.4.9.tar.gz -C /home/admin && \
                                     cd /home/admin/zookeeper-3.4.9 && \
                                     cp conf/zoo_sample.cfg conf/zoo.cfg &&              \
                                     bash bin/zkServer.sh restart '

cd $curr_path
#!/usr/bin/env bash

curr_path=`pwd`
cd `dirname $0`

pscp -h hosts/mycat -l admin ./binary/Mycat-server-1.6.1-RELEASE-20170329150015-linux.tar.gz /home/admin/Mycat-server-1.6.1-RELEASE-20170329150015-linux.tar.gz

pssh -h hosts/zookeeper -l admin -i ' tar zxvf /home/admin/Mycat-server-1.6.1-RELEASE-20170329150015-linux.tar.gz -C /home/admin '

pscp -h hosts/mycat -l admin ./mycat/conf/*  /home/admin/mycat/conf/

pssh -h hosts/zookeeper -l admin -i ' cd /home/admin/mycat && \
                                    ./bin/mycat console '

cd $curr_path
#!/usr/bin/env bash



pscp -h hosts/mycat -l root ./binary/Mycat-server-1.6.1-RELEASE-20170329150015-linux.tar.gz /root/Mycat-server-1.6.1-RELEASE-20170329150015-linux.tar.gz

pssh -h hosts/zookeeper -l root -i ' tar zxvf /root/Mycat-server-1.6.1-RELEASE-20170329150015-linux.tar.gz -C /opt '

pscp -h hosts/mycat -l root ./mycat/conf/*  /opt/mycat/conf/

pssh -h hosts/zookeeper -l root -i ' cd /opt/mycat && \
                                    ./bin/mycat console '

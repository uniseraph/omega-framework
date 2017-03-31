#!/usr/bin/env bash

curr_path=`pwd`
cd `dirname $0`

echo "所有需要root权限的动作在此执行"

if [[ "$(whoami)" != "admin" ]]; then
    echo "Please run as admin"
    exit 1
fi

cd hosts

awk '{print $1}' all > hosts
awk '{print $1 > $2}' all

rm -f eureka
rm -f configserver
rm -f zookeeper
rm -f mycat
cat eureka* > eureka
cat configserver* > configserver
cat zookeeper* > zookeeper
cat mycat* > mycat

cd ..

cat hosts/hosts | xargs -n 1 bash copy-key.sh

pscp -h hosts/hosts -l root ./binary/jdk-8u121-linux-x64.rpm /root/jdk-8u121-linux-x64.rpm
pscp -h hosts/hosts -l root ./hosts/all /root/all

pssh -h hosts/hosts -l root -i 'cat /root/all >> /etc/hosts'
pssh -h hosts/hosts -l root -i 'rpm -i /root/jdk-8u121-linux-x64.rpm'
pssh -h hosts/hosts -l root -i 'yum install -y tcpdump curl jq telnet'
pssh -h hosts/hosts -l root -i 'useradd admin && su - admin && mkdir -p /home/admin/.ssh'

pscp -h hosts/hosts -l root /home/admin/.ssh/id_rsa.pub /home/admin/.ssh/authorized_keys

pssh -h hosts/hosts -l admin -i 'mkdir -p /home/admin/omega-framework/lib'

cd $curr_path
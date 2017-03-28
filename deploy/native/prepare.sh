#!/usr/bin/env bash


echo "注意本脚本用于环境初始化，只能执行一次，请慎重操作！！！！！！"

#拷贝免登
pscp -h hosts/all -l root -A  ~/.ssh/id_rsa.pub    /root/.ssh/authorized_keys

pscp -h hosts/all -l root ./binary/jdk-8u121-linux-x64.rpm  /root/jdk-8u121-linux-x64.rpm

pscp -h hosts/all -l root ./hosts/all   /root/all

pssh -h hosts/all -l root -i 'cat /root/all >> /etc/hosts'

pssh -h hosts/all -l root -i 'rpm -i /root/jdk-8u121-linux-x64.rpm'

pssh -h hosts/all -l root -i 'mkdir -p /opt/omega-framework/lib'
pssh -h hosts/all -l root -i 'yum install -y  tcpdump curl jq'




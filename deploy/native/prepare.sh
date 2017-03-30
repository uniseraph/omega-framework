#!/usr/bin/env bash


echo "所有需要root权限的动作在此执行"


if [[ "$(whoami)" != "admin" ]]; then
    echo "Please run as admin"
    exit 1
fi



cat hosts/hosts | xargs -n 1 bash copy-key.sh
pscp -h hosts/hosts -l root ./binary/jdk-8u121-linux-x64.rpm  /root/jdk-8u121-linux-x64.rpm
pscp -h hosts/hosts -l root ./hosts/all   /root/all
pssh -h hosts/hosts -l root -i 'cat /root/all >> /etc/hosts'
pssh -h hosts/hosts -l root -i 'rpm -i /root/jdk-8u121-linux-x64.rpm'
pssh -h hosts/hosts -l root -i 'yum install -y  tcpdump curl jq telnet'



pscp -h hosts/rabbitmq -l root ./binary/rabbitmq-server-3.6.8-1.el7.noarch.rpm /root/rabbitmq-server-3.6.8-1.el7.noarch.rpm
pscp -h hosts/rabbitmq -l root ./binary/rabbitmqadmin /usr/bin/rabbitmqadmin

pssh -h hosts/rabbitmq -l root -i ' yum install -y rabbitmq-server-3.6.8-1.el7.noarch.rpm  && \
                                    systemctl restart rabbitmq-server && \
                                    sleep 5 && \
                                    rabbitmq-plugins enable rabbitmq_management && \
                                    rabbitmqctl add_user ongo360 ongo360 && \
                                    rabbitmqctl set_user_tags ongo360 administrator && \
                                    rabbitmqctl add_vhost ongo360_vhost && \
                                    rabbitmqctl set_permissions -p "ongo360_vhost"  ongo360  ".*" ".*" ".*"'

pssh -h hosts/rabbitmq -l root -i ' chmod +x /usr/bin/rabbitmqadmin && \
                                    rabbitmqadmin --vhost=ongo360_vhost -u ongo360 -p ongo360 \
                                        declare  exchange name=task durable=true type=direct && \
                                    rabbitmqadmin --vhost=ongo360_vhost -u ongo360 -p ongo360 \
                                        declare queue name=sendEmail durable=true  && \
                                    rabbitmqadmin --vhost=ongo360_vhost -u ongo360 -p ongo360 \
                                        declare binding source=task destination=sendEmail '


pssh -h hosts/hosts -l root -i 'useradd admin && su - admin && mkdir  -p /home/admin/.ssh'

pscp -h hosts/hosts -l root /home/admin/.ssh/id_rsa.pub /home/admin/.ssh/authorized_keys

pssh -h hosts/hosts -l admin -i 'mkdir -p /home/admin/omega-framework/lib'

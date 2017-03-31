#!/usr/bin/env bash


curr_path=`pwd`
cd `dirname $0`

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

cd $curr_path
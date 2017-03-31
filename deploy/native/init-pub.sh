#!/usr/bin/env bash

curr_path=`pwd`
cd `dirname $0`

yum install -y  pssh curl telnet
ln -s /usr/bin/pscp.pssh /usr/bin/pscp

useradd admin
passwd admin
su - admin
if [[ ! -f ~/.ssh/id_rsa.pub ]]; then
    ssh-keygen -t rsa
fi

cd $curr_path
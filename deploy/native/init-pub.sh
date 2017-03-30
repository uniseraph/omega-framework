#!/usr/bin/env bash


yum install -y  pssh curl telnet

ln -s /usr/bin/pscp.pssh /usr/bin/pscp

if [[ ! -f ~/.ssh/id_rsa.pub ]]; then
    ssh-keygen -t rsa
fi



cat hosts/hosts | xargs -n 1 bash copy-key.sh
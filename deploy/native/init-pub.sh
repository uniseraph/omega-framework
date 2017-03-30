#!/usr/bin/env bash


yum install -y  pssh curl telnet

ln -s /usr/bin/pscp.pssh /usr/bin/pscp



#cat hosts/hosts | xargs -n 1 bash copy-key.sh
useradd admin

passwd admin
su - admin
if [[ ! -f ~/.ssh/id_rsa.pub ]]; then
    ssh-keygen -t rsa
fi
#cat hosts/hosts | xargs -n 1 bash copy-2admin.sh

#pssh -h hosts/hosts -l root -i 'mkdir -p /opt/omega-framework/lib'

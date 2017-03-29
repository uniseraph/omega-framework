#!/usr/bin/env bash


yum install -y  pssh curl telnet

ln -s /usr/bin/pscp.pssh /usr/bin/pscp

cat host/all | xargs -n 1 scp ~/.ssh/id_rsa.pub
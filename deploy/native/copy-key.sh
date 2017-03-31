#!/usr/bin/env bash

curr_path=`pwd`
cd `dirname $0`

scp ~/.ssh/id_rsa.pub   root@$1:/root/.ssh/authorized_keys

cd $curr_path
#!/usr/bin/env bash

curr_path=`pwd`
cd `dirname $0`

pscp -h hosts/configserver  -l  admin  -r ./config-repo  /home/admin/

pssh -h hosts/configserver1 -l admin -i ' curl -fsSL -X POST http://localhost:8080/bus/refresh '

cd $curr_path
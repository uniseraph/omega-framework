#!/usr/bin/env bash


pscp -h hosts/configserver  -l  admin  -r ./config-repo  /home/admin/

pssh -h configserver1 -l admin -i ' curl -fsSL -X POST http://localhost:8080/bus/refresh '

#!/usr/bin/env bash

scp ~/.ssh/id_rsa.pub   root@$1:/home/admin/.ssh/authorized_keys

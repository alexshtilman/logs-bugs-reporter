#!/bin/sh

config_host=$1
config_port=$2
shift 2
cmd="$@"

# wait for the postgres docker to be running

while ! curl http://$config_host:$config_port/actuator/refresh 2>&1 | grep '52'
do
  sleep 1
done

# run the command
exec $cmd

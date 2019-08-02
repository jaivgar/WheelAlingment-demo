#!/bin/bash
#More sleep time between these commands might be needed on slower devices like a Raspberry Pi (because of the database accesses)

sleep_time=5
echo Shutting down Core Systems
pkill -f datamanager
pkill -f orchestrator
pkill -f gatekeeper
pkill -f authorization
pkill -f eventhandler
pkill -f gateway
sleep "$sleep_time"
pkill -f serviceregistry
sleep "$sleep_time"

if pgrep -f serviceregistry
then
  kill -KILL $(ps aux | grep 'datamanager' | awk '{print $2}')
  kill -KILL $(ps aux | grep 'orchestrator' | awk '{print $2}')
  kill -KILL $(ps aux | grep 'gatekeeper' | awk '{print $2}')
  kill -KILL $(ps aux | grep 'authorization' | awk '{print $2}')
  kill -KILL $(ps aux | grep 'eventhandler' | awk '{print $2}')
  kill -KILL $(ps aux | grep 'gateway' | awk '{print $2}')
  kill -KILL $(ps aux | grep 'serviceregistry' | awk '{print $2}')
  echo Core systems forcefully killed
else
  echo Core systems killed
fi

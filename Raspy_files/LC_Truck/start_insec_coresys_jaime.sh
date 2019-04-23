#!/bin/bash

# Put in /home/jaime/Documents/Github/core-java
parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
sleep_time=20

cd "$parent_path"

echo Starting Core Systems - wait 1 minute
#More sleep time between core systems might be needed on slower devices like a Raspberry Pi

#cd serviceregistry_sql/target
#nohup java -jar serviceregistry_sql-4.0.jar -d -daemon > insecure_sr.log &
#echo Service Registry started
#sleep "$sleep_time"

cd authorization/target
nohup java -jar authorization-4.0.jar -d -daemon > insecure_auth.log &
echo Authorization started
sleep "$sleep_time"

cd ../../gateway/target
nohup java -jar gateway-4.0.jar -d -daemon > insecure_gateway.log &
echo Gateway started
sleep "$sleep_time"

cd ../../gatekeeper/target
nohup java -jar gatekeeper-4.0.jar -d -daemon > insecure_gk.log &
echo Gatekeeper started
sleep "$sleep_time"

cd ../../orchestrator/target
nohup java -jar orchestrator-4.0.jar -d -daemon > insecure_orch.log &
echo Orchestrator started
sleep "$sleep_time"



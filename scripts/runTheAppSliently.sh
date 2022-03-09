#!/bin/bash
./../mvn clean package
nohup java -jar ./../target/database-benchmark-client-1.0.0-SNAPSHOT.jar > database-benchmark-client-1.0.0-SNAPSHOT.log 2>&1 &
echo $! > amq-client-app.pid.file
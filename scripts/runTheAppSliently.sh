#!/bin/bash
mvn -f ./../pom.xml clean package
#chmod 777 ./../target/quarkus-app/quarkus-run.jar
nohup java -jar ./../target/quarkus-app/quarkus-run.jar > amq-benchmark-client-1.0.0-SNAPSHOT.log 2>&1 &
echo $! > amq-client-app.pid.file
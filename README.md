# AMQ Benchmark Quarkus(Java) Client

## Requirements
* Java 11
* Maven 3.x
* Internet accesses to download needed artifacts

## Design notes
 * Configuration is defined in [application.properties](./src/main/resources/application.properties)

## Running the AMQ benchmark in Development mode

### Launching the application

Configure below properties as per your AMQ. 
```properties
amqp-host=163.75.93.7
amqp-port=5672
#amqp-username=quarkus
#amqp-password=quarkus

#Configuring the incoming channel (reading from AMQP)
mp.messaging.incoming.exampleQueue-in.connector=smallrye-amqp
mp.messaging.incoming.exampleQueue-in.address=exampleQueue
#Configuring the outgoing channel (writing to AMQP)
mp.messaging.outgoing.exampleQueue-out.connector=smallrye-amqp
mp.messaging.outgoing.exampleQueue-out.address=exampleQueue
```

Run the benchmark application from the command line args - 
```shell
mvn quarkus:dev
```

### Running the benchmark

The client application exposes an API that can be used to start the test:
```properties
http://localhost:9090/benchmark/TEST_DURATION/THREADS
```
Where:
* TYPE can be any of:
  * `databaseWrite`: Does write to the database mentioned as part of the JDBC URL. At this moment only mongo supported. 
  * `databaseRead`: Does read to the database mentioned as part of the JDBC URL. At this moment only mongo supported and reads the record with ID=1. We can extend the functionality based on requirement.
* DURATION is the duration in seconds of the test
* THREADS is the number of parallel threads to spawn (AKA number of users)



Examples:
```shell
 curl -X GET http://localhost:9090/benchmark/databaseWrite/120/3
```
Result is in JSON format:
```json
{
  "noOfExecutions" : 34135,
  "noOfFailures" : 0,
  "minResponseTime" : {
    "index" : 615,
    "responseTime" : 1
  },
  "maxResponseTime" : {
    "index" : 9144,
    "responseTime" : 80
  },
  "averageResponseTime" : 2,
  "percentile95" : 3,
  "percentile99" : 4,
  "totalTimeMillis" : 74882,
  "elapsedTimeMillis" : 30010,
  "requestsPerSecond" : 1137.0
}
```
**Note** The `index` attribute in `minResponseTime` and `maxResponseTime` represent the (first) index of the request 
for which that time what calculated

## Deploying the application on to Open Shift Cluster
Refer [Deploying the application on to Open Shift Cluster](./open-shift/)

## Running the Benchmark for multiple users
Refer [Running the benchmark for multiple users in automated way](./scripts/)


curl http://localhost:9090/benchmark/intermittent-metrics
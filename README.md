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
http://localhost:9090/benchmark/TEST_DURATION_IN_SECONDS/RECEIVE_WAIT_TIME_IN_SECONDS/THREADS
```
Where:
* TEST_DURATION_IN_SECONDS is the time duration to send the messages from the producer service.
* RECEIVE_WAIT_TIME_IN_SECONDS This is the amount of time benchmark will wait for the consumer service to receive the messages. During this period no messages will be sent.
* THREADS is the number of parallel threads to spawn (AKA number of users)

Examples:
```shell
 curl -X GET http://localhost:9090/benchmark/120/30/1
```
Above curl command will trigger the benchmark - 120 seconds will constantly send messages and after that wait for 30 seconds so that consumer can receive possible amount of messages. 

Result is in JSON format:
```json
{
  "messagesReceived" : 2285,
  "noOfFailures" : 0,
  "messagesReceivedPerSecond" : 19.0,
  "elapsedTimeMillis" : 120149,
  "totalMessagesSent" : 161462
}        
```

Below API will expose the metrics to get the metrics during the time of benchmarking.


```shell
curl http://localhost:9090/benchmark/intermittent-metrics
```

```json
{
  "messagesReceived" : 2052,
  "noOfFailures" : 0,
  "messagesReceivedPerSecond" : 16.0,
  "elapsedTimeMillis" : 124944,
  "totalMessagesSent" : 147776
}
```
# AMQ Benchmark Quarkus(Java) Client

## Requirements
* Java 16
* Maven 3.8.x
* Internet accesses to download needed artifacts

**Maven 3.8.x setup on RHEL**

wget https://dlcdn.apache.org/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.tar.gz
tar xvf apache-maven-3.8.4-bin.tar.gz
mv apache-maven-3.8.4  /usr/local/apache-maven

export M2_HOME=/usr/local/apache-maven
export M2=$M2_HOME/bin
export PATH=$M2:$PATH

source ~/.bashrc
mvn -version

**JDK setup on RHEL**

cd /tmp
wget https://download.java.net/java/GA/jdk16.0.1/7147401fd7354114ac51ef3e1328291f/9/GPL/openjdk-16.0.1_linux-x64_bin.tar.gz
tar xzf openjdk-16.0.1_linux-x64_bin.tar.gz
mv jdk-16.0.1 /opt

cd /opt/jdk-16.0.1
alternatives --install /usr/bin/java java /opt/jdk-16.0.1/bin/java 2

alternatives --install /usr/bin/jar jar /opt/jdk-16.0.1/bin/jar 2
alternatives --install /usr/bin/javac javac /opt/jdk-16.0.1/bin/javac 2
alternatives --set jar /opt/jdk-16.0.1/bin/jar
alternatives --set javac /opt/jdk-16.0.1/bin/javac

export JAVA_HOME=/opt/jdk-16.0.1/
export JRE_HOME=/opt/jdk-16.0.1/jre/
export PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin


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
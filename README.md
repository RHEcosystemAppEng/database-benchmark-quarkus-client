- [Database Benchmark Quarkus(Java) Client](#database-benchmark-quarkus-java--client)
  * [Requirements](#requirements)
  * [Design notes](#design-notes)
  * [Running the Database benchmark in Development mode](#running-the-database-benchmark-in-development-mode)
    + [Launching the application](#launching-the-application)
    + [Running the application by connecting to Mongo on Openshift](#running-the-application-by-connecting-to-mongo-on-openshift)
    + [Running the application by setting up Postgres](#running-the-application-by-setting-up-postgres)
    + [Running the benchmark](#running-the-benchmark)
  * [Deploying the application on to Open Shift Cluster](#deploying-the-application-on-to-open-shift-cluster)
  * [Running the Benchmark for multiple users](#running-the-benchmark-for-multiple-users)

# Database Benchmark Quarkus(Java) Client

## Requirements
* Java 11
* Maven 3.x
* Internet accesses to download needed artifacts

## Design notes
 * Configuration is defined in [application.properties](./src/main/resources/application.properties)

## Running the Database benchmark in Development mode

### Launching the application
```shell
mvn quarkus:dev
```

### Running the application by connecting to Mongo on Openshift

```shell
# Expose the mongo deployed on open shift cluster so that you can access it locally. Exposing mongo on port 34000.
oc port-forward mongodb-benchmark-replica-set-0 34000:27017

# add/update the configuration in Application.properties
quarkus.mongodb.connection-string=mongodb://localhost:34000
quarkus.app.benchmark.database-kind=mongo

# Connect to mongo using mongo CLI tool.
mongo mongodb://developer:password@localhost:34000
```

### Running the application by setting up Postgres

```shell
# Or spin up postgres container 
podman run --name psql-container -p 5432:5432 -e POSTGRES_USER=newuser -e POSTGRES_PASSWORD=Password1 -e POSTGRES_DB=mydb postgres

# Modify application.properties with the environment variables you set
quarkus.app.benchmark.database-kind=rdbms

quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/mydb
quarkus.datasource.username=newuser
quarkus.datasource.password=password
```


### Running the benchmark

The client application exposes an API that can be used to start the test:
```properties
http://localhost:9090/benchmark/TYPE/DURATION/THREADS
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
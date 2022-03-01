# Running Database Benchmark Quarkus(Java) Client on the Open Shift

**Step 1:** Building docker image and publish to quay.io

```shell
//adding the jib library so that we can build the docker image.
mvn quarkus:add-extension -Dextensions="container-image-jib"

//building the app to create docker image.
mvn clean package -Dquarkus.container-image.build=true

docker image tag lrangine/database-benchmark-client:1.0.0-SNAPSHOT quay.io/lrangine/database-benchmark-client:2.0.0-SNAPSHOT
docker image push quay.io/lrangine/database-benchmark-client:2.0.0-SNAPSHOT
```
**Step 2:** Deploy the Mongo DB on Open Shift. All the yaml files required for the setup are available in the [folder](./aws/mongodb). You can find the [documentation](./aws/mongodb/README.md) or refer mongo DB documentation. 

**Step 3:** Create the [secret-database-benchmark-mongo-con-string.yaml](secret-database-benchmark-mongo-con-string.yaml) so that database benchmark application can refer it during run time in the next step. App refers to the key `connectionString.standard`. In this way it is easier to modify during run time.

```shell
oc create -f secret-database-benchmark-mongo-con-string.yaml
```
**Step 4:** Deploy the application [deploy-database-benchmark-app.yaml](deploy-database-benchmark-app.yaml) using `oc` CLI tool.

```shell
oc create -f deploy-database-benchmark-app.yaml
```
Above command will spin up the application pods and monitor the logs for any errors. You should be good to perform database benchmarking.

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
* THREADS is the number of parallel threads to spawn


You can use `oc exec` command to trigger the rest API.
Example - 
```shell
oc exec database-benchmark-quarkus-client-867485c897-kfdx2 -- /bin/sh -c "curl -X GET http://localhost:9090/benchmark/databaseWrite/60/3"
```

Response is in JSON format:
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

**Sharding the collection**

```shell
sh.enableSharding("fruits")
//we need to have hashed index on the shard key. Hashed index is a different index than regular index.
db.demo.message.ensureIndex({_id: "hashed"})
//shard the collection now
sh.shardCollection("fruits.demo.message",{"_id":"hashed"})
```

```shell
//If you want to see the data distribution among shards of a collection
db.demo.message.getShardDistribution()
```

```shell
//To see if the mongo sharding cluster balancer enabled or not
sh.getBalancerState()

//enabling the balancer
sh.startBalancer() 
```


**Note** The `index` attribute in `minResponseTime` and `maxResponseTime` respresent the (first) index of the request
for which that time what calculated

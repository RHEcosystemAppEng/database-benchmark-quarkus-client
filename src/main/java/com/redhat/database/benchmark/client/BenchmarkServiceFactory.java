package com.redhat.database.benchmark.client;

import com.redhat.database.benchmark.client.mongo.MongoFruitService;
import com.redhat.database.benchmark.client.postgres.RdbmsFruitService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;


@ApplicationScoped
public class BenchmarkServiceFactory {
    private Logger logger = LoggerFactory.getLogger(BenchmarkServiceFactory.class);

    @ConfigProperty(name = "quarkus.app.benchmark.database-kind", defaultValue = "mongo")
    String datasourceDbKind;

    @Inject
    Instance<MongoFruitService> mongoFruitServiceInstance;

    @Inject
    Instance<RdbmsFruitService> rdbmsFruitServiceInstance;

    public IBenchmarkService getBenchmarkService(){
        logger.info("Initializing Benchmark Service...!!!");
        if("mongo".equalsIgnoreCase(datasourceDbKind)){
            logger.info("Initializing Benchmark Service - Mongo Database...!!!");
            return mongoFruitServiceInstance.get();
        }else if("rdbms".equalsIgnoreCase(datasourceDbKind)){
            logger.info("Initializing Benchmark Service - RDBMS Database...!!!");
            return rdbmsFruitServiceInstance.get();
        }
           throw new RuntimeException("No Matching Benchmark service to create..quarkus.app.benchmark.database-kind property should have one of the values [mongo,rdbms]");
    }

}

package com.redhat.database.benchmark.mongo.crud;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class FruitService {
    private Logger logger = LoggerFactory.getLogger(FruitService.class);


    @Inject
    private Instance<MongoClient> mongoClientInstance;


    public Fruit add(Fruit fruit) {
        logger.info("Fruit {} is added..!!",getCollection().insertOne(fruit).getInsertedId().toString());
        //logger.info("Fruit {} is added..!!",fruit);
        return fruit;
    }

    public Fruit get(String id) {
        return getCollection().find(eq("id", id)).first();
    }

    private MongoCollection<Fruit> getCollection() {
        return mongoClientInstance.get().getDatabase("fruits").getCollection("demo.fruit", Fruit.class);
    }
}

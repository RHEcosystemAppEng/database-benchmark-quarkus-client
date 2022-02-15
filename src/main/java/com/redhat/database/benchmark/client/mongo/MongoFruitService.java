package com.redhat.database.benchmark.client.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.redhat.database.benchmark.client.IBenchmarkService;
import com.redhat.database.benchmark.client.Fruit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class MongoFruitService implements IBenchmarkService {
    private Logger logger = LoggerFactory.getLogger(MongoFruitService.class);

    @Inject
    Instance<MongoClient> mongoClientInstance;

    @Override
    public Fruit add(Fruit fruit) {
        logger.debug("Fruit {} is added..!!",getCollection().insertOne(fruit).getInsertedId().toString());
        return fruit;
    }

    @Override
    public Fruit get(String id) {
        return getCollection().find(eq("id", id)).first();
    }

    private MongoCollection<Fruit> getCollection() {
        return mongoClientInstance.get().getDatabase("fruits").getCollection("demo.fruit", Fruit.class);
    }
}

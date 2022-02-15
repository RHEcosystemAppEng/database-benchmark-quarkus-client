package com.redhat.database.benchmark.client.mongo;

import com.redhat.database.benchmark.client.BenchmarkServiceFactory;
import com.redhat.database.benchmark.client.Fruit;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {

    @Inject
    BenchmarkServiceFactory benchmarkServiceFactory;

    @POST
    public void add(Fruit fruit) {
        benchmarkServiceFactory.getBenchmarkService().add(fruit);
    }
}
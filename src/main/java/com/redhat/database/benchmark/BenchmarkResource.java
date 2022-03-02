package com.redhat.database.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/benchmark")
public class BenchmarkResource {
    @Inject
    BenchmarkRunner benchmarkRunner;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{testType}/{durationInSeconds}/{receiveWaitTimeInSeconds}/{noOfThreads}")
    public String run(@PathParam("testType") String testType, @PathParam("durationInSeconds") int durationInSeconds, @PathParam("receiveWaitTimeInSeconds") int receiveWaitTimeInSeconds,
                      @PathParam("noOfThreads") int noOfThreads) throws JsonProcessingException, InterruptedException {
        return benchmarkRunner.run(testType, durationInSeconds, receiveWaitTimeInSeconds, noOfThreads);
    }
}
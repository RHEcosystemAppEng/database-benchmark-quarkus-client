package com.redhat.database.benchmark.client;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class Metadata {

    private String benchmarkSeqId;
    private Timestamp startTime;
    private Timestamp endTime;

    public Metadata(){
        this.benchmarkSeqId = UUID.randomUUID().toString();
        this.startTime = Timestamp.from(Instant.now());
    }

    public Metadata(String benchmarkSeqId, Timestamp startTime, Timestamp endTime) {
        this.benchmarkSeqId = benchmarkSeqId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getBenchmarkSeqId() {
        return benchmarkSeqId;
    }

    public void setBenchmarkSeqId(String benchmarkSeqId) {
        this.benchmarkSeqId = benchmarkSeqId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}

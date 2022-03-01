package com.redhat.database.benchmark;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.Data;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonPropertyOrder
@Data
public class TestMetrics {

    private long noOfExecutions;
    private long noOfFailures;
    private double requestsPerSecond;

    private long elapsedTimeMillis;
    private long totalMessagesSent;

    public long getTotalMessagesSent() {
        return totalMessagesSent;
    }

    public void setTotalMessagesSent(long totalMessagesSent) {
        this.totalMessagesSent = totalMessagesSent;
    }

    public Long getNoOfExecutions() {
        return noOfExecutions;
    }

    public void setNoOfExecutions(Long noOfExecutions) {
        this.noOfExecutions = noOfExecutions;
    }

    public Long getNoOfFailures() {
        return noOfFailures;
    }

    public void setNoOfFailures(Long noOfFailures) {
        this.noOfFailures = noOfFailures;
    }

    public Long getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    public void setElapsedTimeMillis(Long elapsedTimeMillis) {
        this.elapsedTimeMillis = elapsedTimeMillis;
    }

    public double getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public void setRequestsPerSecond(double requestsPerSecond) {
        this.requestsPerSecond = requestsPerSecond;
    }
}

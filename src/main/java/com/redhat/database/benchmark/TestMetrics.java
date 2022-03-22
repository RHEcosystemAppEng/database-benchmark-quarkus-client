package com.redhat.database.benchmark;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.Data;

import java.sql.Timestamp;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonPropertyOrder
@Data
public class TestMetrics {

    private long messagesReceived;
    private long noOfFailures;
    private double messagesReceivedPerSecond;

    private long elapsedTimeMillis;
    private long totalMessagesSent;

    private String message;

    private Timestamp producerStartTime;
    private Timestamp producerEndTime;
    private Timestamp consumerEndTime;
    private long consumerWaitTime;

    public long getTotalMessagesSent() {
        return totalMessagesSent;
    }

    public void setTotalMessagesSent(long totalMessagesSent) {
        this.totalMessagesSent = totalMessagesSent;
    }

    public Long getMessagesReceived() {
        return messagesReceived;
    }

    public void setMessagesReceived(Long messagesReceived) {
        this.messagesReceived = messagesReceived;
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

    public double getMessagesReceivedPerSecond() {
        return messagesReceivedPerSecond;
    }

    public void setMessagesReceivedPerSecond(double messagesReceivedPerSecond) {
        this.messagesReceivedPerSecond = messagesReceivedPerSecond;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessagesReceived(long messagesReceived) {
        this.messagesReceived = messagesReceived;
    }

    public void setNoOfFailures(long noOfFailures) {
        this.noOfFailures = noOfFailures;
    }

    public void setElapsedTimeMillis(long elapsedTimeMillis) {
        this.elapsedTimeMillis = elapsedTimeMillis;
    }

    public Timestamp getProducerStartTime() {
        return producerStartTime;
    }

    public void setProducerStartTime(Timestamp producerStartTime) {
        this.producerStartTime = producerStartTime;
    }

    public Timestamp getProducerEndTime() {
        return producerEndTime;
    }

    public void setProducerEndTime(Timestamp producerEndTime) {
        this.producerEndTime = producerEndTime;
    }

    public Timestamp getConsumerEndTime() {
        return consumerEndTime;
    }

    public void setConsumerEndTime(Timestamp consumerEndTime) {
        this.consumerEndTime = consumerEndTime;
    }

    public long getConsumerWaitTime() {
        return consumerWaitTime;
    }

    public void setConsumerWaitTime(long consumerWaitTime) {
        this.consumerWaitTime = consumerWaitTime;
    }
}

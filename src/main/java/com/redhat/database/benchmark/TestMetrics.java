package com.redhat.database.benchmark;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.Data;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonPropertyOrder
@Data
public class TestMetrics {
    @Data
    static class ResponseTimeAndID {
        private long index;
        private long responseTime;

        public static ResponseTimeAndID defaultMinResponseTime() {
            ResponseTimeAndID result = new ResponseTimeAndID();
            result.index = 0;
            result.responseTime = Long.MAX_VALUE;
            return result;
        }

        public static ResponseTimeAndID defaultMaxResponseTime() {
            ResponseTimeAndID result = new ResponseTimeAndID();
            result.index = 0;
            result.responseTime = 0;
            return result;
        }

        void minOf(Execution execution) {
            long responseTime = execution.duration().toMillis();
            if (responseTime < this.responseTime) {
                this.responseTime = responseTime;
                this.index = execution.getIndex();
            }
        }

        void maxOf(Execution execution) {
            long responseTime = execution.duration().toMillis();
            if (responseTime > this.responseTime) {
                this.responseTime = responseTime;
                this.index = execution.getIndex();
            }
        }

        public Long getIndex() {
            return index;
        }

        public void setIndex(Long index) {
            this.index = index;
        }

        public Long getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(Long responseTime) {
            this.responseTime = responseTime;
        }
    }

    private long noOfExecutions;
    private long noOfFailures;
    private ResponseTimeAndID minResponseTime;
    private ResponseTimeAndID maxResponseTime;
    private long averageResponseTime;
    private long percentile95;
    private long percentile99;
    private long totalTimeMillis;
    private long elapsedTimeMillis;
    private double requestsPerSecond;

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

    public ResponseTimeAndID getMinResponseTime() {
        return minResponseTime;
    }

    public void setMinResponseTime(ResponseTimeAndID minResponseTime) {
        this.minResponseTime = minResponseTime;
    }

    public ResponseTimeAndID getMaxResponseTime() {
        return maxResponseTime;
    }

    public void setMaxResponseTime(ResponseTimeAndID maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }

    public Long getPercentile95() {
        return percentile95;
    }

    public void setPercentile95(Long percentile95) {
        this.percentile95 = percentile95;
    }

    public Long getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Long averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public Long getPercentile99() {
        return percentile99;
    }

    public void setPercentile99(Long percentile99) {
        this.percentile99 = percentile99;
    }

    public Long getTotalTimeMillis() {
        return totalTimeMillis;
    }

    public void setTotalTimeMillis(Long totalTimeMillis) {
        this.totalTimeMillis = totalTimeMillis;
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

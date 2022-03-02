package com.redhat.database.benchmark;

import com.redhat.database.benchmark.Execution;
import com.redhat.database.benchmark.TestMetrics;
import com.redhat.database.benchmark.client.Message;
import com.redhat.database.benchmark.client.amq.MessageService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@ApplicationScoped
public class Stats {


    MessageService messageService;

    public Stats(MessageService messageService){
        this.messageService = messageService;
    }

    private Instant startTime = Instant.now();


    private long noOfFailures;

    public Execution startOne(long index) {

        return new Execution(index, callback);
    }

    private Consumer<Execution> callback = execution -> completed(execution);

    private void completed(Execution execution) {
        noOfFailures += execution.isFailed() ? 1 : 0;
    }

    public TestMetrics build(int receiveWaitTimeInSeconds) throws InterruptedException {
        Instant producerEndTime = Instant.now();
        Thread.sleep(receiveWaitTimeInSeconds*1000);
        Duration testDuration = Duration.between(startTime, producerEndTime);
        Instant consumerEndTime = Instant.now();
        long noOfRequests = messageService.getNumberOfMessages(Timestamp.from(consumerEndTime));
        TestMetrics metrics = new TestMetrics();
        metrics.setNoOfExecutions(noOfRequests);
        metrics.setTotalMessagesSent(messageService.getMessagesCount());
        metrics.setNoOfFailures(noOfFailures);
        metrics.setElapsedTimeMillis(testDuration.toMillis());
        metrics.setRequestsPerSecond(1000 * noOfRequests / testDuration.toMillis());

        return metrics;
    }
}

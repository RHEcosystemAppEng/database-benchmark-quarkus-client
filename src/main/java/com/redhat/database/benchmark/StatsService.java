package com.redhat.database.benchmark;

import com.redhat.database.benchmark.client.amq.MessageService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@Singleton
public class StatsService {

    @Inject
    MessageService messageService;

    @Inject
    ErrorDaoService errorDaoService;

    private Instant startTime = Instant.now();

   public TestMetrics buildMetrics(int receiveWaitTimeInSeconds) throws InterruptedException {
        Instant producerEndTime = Instant.now();
        Thread.sleep(receiveWaitTimeInSeconds*1000);
        Duration testDuration = Duration.between(startTime, producerEndTime);
        Instant consumerEndTime = Instant.now();
        long messagesReceived = messageService.getNumberOfMessagesReceived(Timestamp.from(consumerEndTime));
        TestMetrics metrics = new TestMetrics();
        metrics.setMessagesReceived(messagesReceived);
        metrics.setTotalMessagesSent(messageService.getMessagesCount());
        metrics.setNoOfFailures(errorDaoService.getErrorsCount());
        metrics.setElapsedTimeMillis(testDuration.toMillis());
        metrics.setMessagesReceivedPerSecond(1000 * messagesReceived / testDuration.toMillis());

        return metrics;
    }

    public TestMetrics getIntermittentMetrics(){
        Instant endTime = Instant.now();
        Duration testDuration = Duration.between(startTime, endTime);
        long messagesReceived = messageService.getNumberOfMessagesReceived(Timestamp.from(endTime));
        TestMetrics metrics = new TestMetrics();
        metrics.setMessagesReceived(messagesReceived);
        metrics.setTotalMessagesSent(messageService.getMessagesCount());
        metrics.setNoOfFailures(errorDaoService.getErrorsCount());
        metrics.setElapsedTimeMillis(testDuration.toMillis());
        metrics.setMessagesReceivedPerSecond(1000 * messagesReceived / testDuration.toMillis());

        return metrics;
    }

}
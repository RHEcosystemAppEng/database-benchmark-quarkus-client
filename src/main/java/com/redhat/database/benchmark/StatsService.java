package com.redhat.database.benchmark;

import com.redhat.database.benchmark.client.Metadata;
import com.redhat.database.benchmark.client.amq.MessageDaoService;
import com.redhat.database.benchmark.client.amq.MetadataDaoService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class StatsService {

    @Inject
    MessageDaoService messageDaoService;

    @Inject
    ErrorDaoService errorDaoService;

    @Inject
    MetadataDaoService metadataDaoService;

   public TestMetrics buildMetrics(int receiveWaitTimeInSeconds) throws InterruptedException {
       final Metadata metadata = metadataDaoService.getMetadata();

        Instant producerEndTime = Instant.now();
        Thread.sleep(receiveWaitTimeInSeconds*1000);

        Duration testDuration = Duration.between(metadata.getStartTime().toInstant(), producerEndTime);
        Instant consumerEndTime = Instant.now();
        long messagesReceived = messageDaoService.getNumberOfMessagesReceived(Timestamp.from(consumerEndTime));
        TestMetrics metrics = new TestMetrics();
        metrics.setMessagesReceived(messagesReceived);
        metrics.setTotalMessagesSent(messageDaoService.getMessagesCount());
        metrics.setNoOfFailures(errorDaoService.getErrorsCount());
        metrics.setElapsedTimeMillis(testDuration.toMillis());
        metrics.setMessagesReceivedPerSecond(1000 * messagesReceived / testDuration.toMillis());

        return metrics;
    }

    public TestMetrics getIntermittentMetrics(){
        Instant endTime = Instant.now();
        Instant startTime = metadataDaoService.getMetadata().getStartTime().toInstant();
        Duration testDuration = Duration.between(startTime, endTime);
        long messagesReceived = messageDaoService.getNumberOfMessagesReceived(Timestamp.from(endTime));
        TestMetrics metrics = new TestMetrics();
        metrics.setMessagesReceived(messagesReceived);
        metrics.setTotalMessagesSent(messageDaoService.getMessagesCount());
        metrics.setNoOfFailures(errorDaoService.getErrorsCount());
        metrics.setElapsedTimeMillis(testDuration.toMillis());
        metrics.setMessagesReceivedPerSecond(1000 * messagesReceived / testDuration.toMillis());

        return metrics;
    }

}

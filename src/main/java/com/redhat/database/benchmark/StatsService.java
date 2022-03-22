package com.redhat.database.benchmark;

import com.redhat.database.benchmark.client.Metadata;
import com.redhat.database.benchmark.client.amq.MessageDaoService;
import com.redhat.database.benchmark.client.amq.MetadataDaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class StatsService {
    private Logger logger = LoggerFactory.getLogger(StatsService.class);

    @Inject
    MessageDaoService messageDaoService;

    @Inject
    ErrorDaoService errorDaoService;

    @Inject
    MetadataDaoService metadataDaoService;

    public TestMetrics buildMetrics(int receiveWaitTimeInSeconds) throws InterruptedException {
        final Metadata metadata = metadataDaoService.getMetadata();

        Instant producerEndTime = Instant.now();
        performConsumerSleepWaitTime(receiveWaitTimeInSeconds);
        Instant consumerEndTime = Instant.now();
        TestMetrics metrics = new TestMetrics();
        Duration testDuration = Duration.between(metadata.getStartTime().toInstant(), producerEndTime);
        long messagesReceived = messageDaoService.getNumberOfMessagesReceived(Timestamp.from(consumerEndTime));

        metrics.setMessagesReceived(messagesReceived);
        metrics.setTotalMessagesSent(messageDaoService.getMessagesCount());
        metrics.setNoOfFailures(errorDaoService.getErrorsCount());
        metrics.setElapsedTimeMillis(testDuration.toMillis());
        metrics.setMessagesReceivedPerSecond(1000 * messagesReceived / testDuration.toMillis());

        return metrics;
    }

    private void performConsumerSleepWaitTime(int receiveWaitTimeInSeconds) throws InterruptedException {
        if (receiveWaitTimeInSeconds == -1) {
            Instant consTimeoutStart = Instant.now();
            while (!messageDaoService.hasAllMessagesReceived()) {
                Thread.sleep(10000);
                logger.info("Sleeping for default 10 seconds to receive all the messages.");
                Instant consTimeoutEnd = Instant.now();
                Duration timeoutElapsed = Duration.between(consTimeoutStart, consTimeoutEnd);
                //timeout after 120 minutes.
                if(timeoutElapsed.toMinutes() > 120){
                    break;
                }
            }
        } else {
            logger.info("Sleeping for receiveWaitTimeInSeconds=["+ receiveWaitTimeInSeconds +"] to receive messages");
            Thread.sleep(receiveWaitTimeInSeconds * 1000);
        }
    }

    public TestMetrics getIntermittentMetrics(){
        Instant endTime = Instant.now();
        Metadata metadata = metadataDaoService.getMetadata();
        TestMetrics metrics = new TestMetrics();
        if(metadata == null){
            metrics.setMessage("ERROR: There is no existing benchmark run so no metrics available.");
            return metrics;
        }

        Instant startTime = metadata.getStartTime().toInstant();
        Duration testDuration = Duration.between(startTime, endTime);
        long messagesReceived = messageDaoService.getNumberOfMessagesReceived(Timestamp.from(endTime));
        metrics.setMessagesReceived(messagesReceived);
        metrics.setTotalMessagesSent(messageDaoService.getMessagesCount());
        metrics.setNoOfFailures(errorDaoService.getErrorsCount());
        metrics.setElapsedTimeMillis(testDuration.toMillis());
        metrics.setMessagesReceivedPerSecond(1000 * messagesReceived / testDuration.toMillis());

        return metrics;
    }

}

package com.redhat.database.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.redhat.database.benchmark.client.Message;
import com.redhat.database.benchmark.client.Metadata;
import com.redhat.database.benchmark.client.amq.MessageProducerService;
import com.redhat.database.benchmark.client.amq.MessageDaoService;
import com.redhat.database.benchmark.client.amq.MetadataDaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ApplicationScoped
public class BenchmarkRunner {
    private Logger logger = LoggerFactory.getLogger(BenchmarkRunner.class);

    @Inject
    MessageProducerService messageProducerService;

    @Inject
    MessageDaoService messageDaoService;

   @Inject
   ErrorDaoService errorDaoService;

    @Inject
    StatsService statsService;

    @Inject
    MetadataDaoService metadataDaoService;

    private final String smallString = "7f2kvogvshjxox7zu3qcqccnp3dhulqoc84piara8ryfxvnrb05yk";
    private final String mediumString = "8qpgoiakys0jke2mxbpjy1hyexhjzcfjdyk8kf5xl2ck3vpzri0wp7gqnyuh8ltwj35w0tzkycvzzkdcqkvw7wzu8kbafk9ewys1o581o31qg2esl5n79d80221l";
    private final String longString = "b7b5tvh4rf81r5xwcba3fj5ysd17f3kpqmj49r1hsrjrf867li660mvb14bfgutvdzbha8s3rqwurarqyqmtczxtn82m481dbgidh5jc16oys9b8hqeeqoxyenor2aazgbb5cglv7dc40viva3dk29wyfdk1qr34vnltmukb50cxdx76c";
    private final String veryLongString = "zikh40hdzjhg7b38ch1js2ebtk571h7w5in7gl3bmx77k414wp3w3ysw6mewwlk47j3ebnhkwsurk0yq2hi5r3oqzo0hfgc67vplq7m2aaruow0u5v9119gbl5khnqe9cphph7w301e81rongggymqzpf2t44tdnezmpns6s8mx2o8hgw6r4wnnbpitbyjlsz1u08w3uc";

    private final String[] stringMessages = {smallString, mediumString, longString, veryLongString};

    public String run(int durationInSeconds, int receiveWaitTimeInSeconds, int noOfThreads) throws JsonProcessingException,
            InterruptedException {
        logger.info("Total Number of Records before deleting all messages -{}", messageDaoService.getMessagesCount());
        messageDaoService.deleteAllMessages();
        logger.info("Total Number of Records after deleting all messages -{}", messageDaoService.getMessagesCount());

        logger.info("Total Number of Records before deleting all ERRORS -{}", errorDaoService.getErrorsCount());
        errorDaoService.deleteAllErrors();
        logger.info("Total Number of Records after deleting all ERRORS -{}", errorDaoService.getErrorsCount());

        Metadata metadata = new Metadata();
        metadataDaoService.insertMetadata(metadata);
        new Worker(durationInSeconds, noOfThreads, metadata).run();
        metadataDaoService.updateMetadata(metadata);

        TestMetrics metrics = statsService.buildMetrics(receiveWaitTimeInSeconds);
        logger.info("Printing top errors.");
        errorDaoService.printTopHunErrors();
        String metricsResult = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(metrics);
        logger.info("Metrics Result:"+metricsResult);
        return metricsResult;
    }

    private class Worker {
        private int durationInSeconds;
        private int noOfThreads;
        private AtomicLong itemsCounter = new AtomicLong(0);
        AtomicBoolean timerElapsed = new AtomicBoolean(false);
        private Metadata metadata;

        private Worker(int durationInSeconds, int noOfThreads, Metadata metadata) {
            this.durationInSeconds = durationInSeconds;
            this.noOfThreads = noOfThreads;
            this.metadata = metadata;
        }

        private void run() throws InterruptedException {
            logger.info("Ready to run for {} seconds in {} threads", durationInSeconds,
                    noOfThreads);
            ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);

            Timer timer = new Timer("timer");
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logger.info("Timer elapsed");
                    timerElapsed.set(true);
                    timer.cancel();
                }
            }, durationInSeconds * 1000);

            Collection<Callable<Void>> callables =
                    IntStream.rangeClosed(1, noOfThreads).mapToObj(n -> newCallable()).collect(Collectors.toList());
            executor.invokeAll(callables);
        }

        private Callable<Void> newCallable() {
            return () -> {
                while (!timerElapsed.get()) {
                    long index = itemsCounter.incrementAndGet();
                    try {
                        logger.info("Executing: {}", index);
                        newMessageSendOperation.execute();
                    } catch (Exception e) {
                        logger.error("Failed to run: {}", e.getMessage());
                        errorDaoService.insertError("Failed to submit message - "+e.getMessage());
                    }
                }
                return null;
            };
        }

        private Supplier<Message> newMessageData = () -> new Message(UUID.randomUUID().toString(), "Apple",
                stringMessages[ThreadLocalRandom.current().nextInt(0,4)], metadata.getBenchmarkSeqId());

        private final DatabaseOperation newMessageSendOperation = () -> messageProducerService.send(newMessageData.get());

    }

    @FunctionalInterface
    interface DatabaseOperation {
        Message execute() throws Exception;
    }
}
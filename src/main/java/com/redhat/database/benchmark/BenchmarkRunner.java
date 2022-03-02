package com.redhat.database.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.redhat.database.benchmark.client.Message;
import com.redhat.database.benchmark.client.amq.MessageProducerService;
import com.redhat.database.benchmark.client.amq.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    MessageService messageService;



    public String run(String testType, int durationInSeconds, int receiveWaitTimeInSeconds, int noOfThreads) throws JsonProcessingException,
            InterruptedException {
        TestMetrics metrics = new Worker(testType, durationInSeconds, receiveWaitTimeInSeconds, noOfThreads, messageService).run();
        return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(metrics);
    }

    private class Worker {
        private String testType;
        private int durationInSeconds;
        private int receiveWaitTimeInSeconds;
        private int noOfThreads;
        private AtomicLong itemsCounter = new AtomicLong(0);
        AtomicBoolean timerElapsed = new AtomicBoolean(false);
        private Stats stats;
        private Worker(String testType, int durationInSeconds, int receiveWaitTimeInSeconds, int noOfThreads, MessageService messageService) {
            this.testType = testType;
            this.durationInSeconds = durationInSeconds;
            this.noOfThreads = noOfThreads;
            this.receiveWaitTimeInSeconds = receiveWaitTimeInSeconds;
            this.stats = new Stats(messageService);
        }

        private TestMetrics run() throws InterruptedException {
            logger.info("Ready to run for {} seconds of type '{}' in {} threads", durationInSeconds,
                    testType,
                    noOfThreads);
            ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
            logger.info("Total Number of Records before deleting all messages -{}", messageService.getMessagesCount());
            messageService.deleteAllMessages();
            logger.info("Total Number of Records after deleting all messages -{}", messageService.getMessagesCount());

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

           TestMetrics metrics = stats.build(receiveWaitTimeInSeconds);
           logger.info("Completed {} tests in {}ms", metrics.getNoOfExecutions(), metrics.getElapsedTimeMillis());
           messageService.printTopHunMessages();
           logger.info("Total Number of Records - {}", messageService.getMessagesCount());

            return metrics;
        }

        private Callable<Void> newCallable() {
            return () -> {
                while (!timerElapsed.get()) {
                    long index = itemsCounter.incrementAndGet();
                    Execution execution = stats.startOne(index);
                    try {
                        logger.info("Executing: {}", index);
                        executorOfType().execute();
                        execution.stop();
                    } catch (Exception e) {
                        logger.error("Failed to run: {}", e.getMessage());
                        execution.failed();
                    }
                }
                return null;
            };
        }

        private DatabaseOperation executorOfType() {
            DatabaseOperation dbOperation = null;
            if (testType.equalsIgnoreCase("databaseWrite")) {
                    dbOperation = newMessageSendOperation;
            }
            logger.info("Executor Type, DatabaseOperation: {},{}", testType, dbOperation);
            return dbOperation;
        }

        private Supplier<Message> newMessageData = () -> new Message(UUID.randomUUID().toString(), "Apple",
                "Daily an apple keeps doctor away..!!");

        private final DatabaseOperation newMessageSendOperation = () -> messageProducerService.send(newMessageData.get());

    }

    @FunctionalInterface
    interface DatabaseOperation {
        Message execute() throws Exception;
    }
}
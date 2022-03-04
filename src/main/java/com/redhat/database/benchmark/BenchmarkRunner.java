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

   @Inject
   ErrorDaoService errorDaoService;

    @Inject
    StatsService statsService;


    public String run(int durationInSeconds, int receiveWaitTimeInSeconds, int noOfThreads) throws JsonProcessingException,
            InterruptedException {
        new Worker(durationInSeconds, noOfThreads, errorDaoService).run();
        TestMetrics metrics = statsService.buildMetrics(receiveWaitTimeInSeconds);
        return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(metrics);
    }

    private class Worker {
        private int durationInSeconds;
        private int noOfThreads;
        private AtomicLong itemsCounter = new AtomicLong(0);
        AtomicBoolean timerElapsed = new AtomicBoolean(false);

        private ErrorDaoService errorDaoService;

        private Worker(int durationInSeconds, int noOfThreads, ErrorDaoService errorDaoService) {
            this.durationInSeconds = durationInSeconds;
            this.noOfThreads = noOfThreads;
            this.errorDaoService = errorDaoService;
        }

        private void run() throws InterruptedException {
            logger.info("Ready to run for {} seconds in {} threads", durationInSeconds,
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
                        errorDaoService.insertError(e.getMessage());
                    }
                }
                return null;
            };
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
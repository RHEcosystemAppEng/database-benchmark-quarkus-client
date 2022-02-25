package com.redhat.database.benchmark.client.amq;

import com.redhat.database.benchmark.client.Message;

import io.agroal.api.AgroalDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.*;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class MessageProducerService {
    private Logger logger = LoggerFactory.getLogger(MessageProducerService.class);

/*
    @Inject
    @Channel("fruits-out")
    Emitter<Message> emitter;
*/


    @Inject
    MessageService messageService;

    public Message send(Message message) {
        //emitter.send(message.setSent(Timestamp.from(Instant.now())));
        //inserting message asynchronously
        message.setSent(Timestamp.from(Instant.now()));

        message.setReceived(Timestamp.from((Instant.now().plusSeconds(6))));

        //insert the record asynchronously.
        CompletableFuture.runAsync(() -> messageService.insertMessage(message));

       /* try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/



        return message;
    }


}
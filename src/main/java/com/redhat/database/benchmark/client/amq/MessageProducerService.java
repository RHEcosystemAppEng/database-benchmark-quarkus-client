package com.redhat.database.benchmark.client.amq;

import com.redhat.database.benchmark.client.Message;

import io.agroal.api.AgroalDataSource;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.*;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class MessageProducerService {
    private Logger logger = LoggerFactory.getLogger(MessageProducerService.class);

    @Inject
    @Channel("fruits-out")
    Emitter<Message> emitter;

    @Inject
    MessageService messageService;

    public Message send(Message message) {
        emitter.send(message.setSent(Timestamp.from(Instant.now())));

        //insert the record asynchronously.
        CompletableFuture.runAsync(() -> messageService.insertMessage(message));

        return message;
    }


}
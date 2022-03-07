package com.redhat.database.benchmark.client.amq;

import com.redhat.database.benchmark.client.Message;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
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
    @Channel("exampleQueue-out")
    @OnOverflow(value = OnOverflow.Strategy.UNBOUNDED_BUFFER)
    Emitter<Message> emitter;

    @Inject
    MessageDaoService messageDaoService;

    public Message send(Message message) {
        emitter.send(message.setSent(Timestamp.from(Instant.now())));
        //insert the record asynchronously.
        CompletableFuture.runAsync(() -> messageDaoService.insertMessage(message));

        return message;
    }


}
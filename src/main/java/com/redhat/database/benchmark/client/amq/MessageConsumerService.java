package com.redhat.database.benchmark.client.amq;
import com.redhat.database.benchmark.client.Message;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.*;
import java.time.Instant;

@ApplicationScoped
public class MessageConsumerService {
    private final Logger logger = Logger.getLogger(MessageConsumerService.class);

    @Inject
    MessageDaoService messageDaoService;

    @Incoming("exampleQueue-in")
    public void receive(Message message) {
        logger.info("Message is Received.. Hurray.. "+ message.getUuid());
       int updatedCount = messageDaoService.updateMessage(message.setReceived(Timestamp.from(Instant.now())));
       if(updatedCount == -1 || updatedCount == 0){
           logger.errorf("Message is received but there is no source message record - message UID={%d} ", message.getUuid());
       }
       logger.infof("Got a message: %d - %s", message.getName(), message.getDescription());
    }
}

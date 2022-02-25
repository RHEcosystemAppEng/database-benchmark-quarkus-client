package com.redhat.database.benchmark.client.amq;
import com.redhat.database.benchmark.client.Message;
import io.agroal.api.AgroalDataSource;
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
    AgroalDataSource h2DataSource;


    @Inject
    MessageService messageService;




 /*   @Incoming("fruits-in")*/
    public void receive(Message message) {
        messageService.updateMessage(message.setReceived(Timestamp.from(Instant.now())));
       logger.infof("Got a message: %d - %s", message.getName(), message.getDescription());
    }
}

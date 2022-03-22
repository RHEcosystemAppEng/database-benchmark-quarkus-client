package com.redhat.database.benchmark.client.amq;
import com.redhat.database.benchmark.ErrorDaoService;
import com.redhat.database.benchmark.client.Message;
import com.redhat.database.benchmark.client.Metadata;
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

    @Inject
    ErrorDaoService errorDaoService;

    @Inject
    MetadataDaoService metadataDaoService;

    @Incoming("exampleQueue-in")
    public void receive(Message message) {
        Metadata metadata = metadataDaoService.getMetadata();
        if(metadata == null || !metadata.getBenchmarkSeqId().equalsIgnoreCase(message.getBenchmarkSeqId())){
            logger.info("Received an old message..message_benchmark_seq_id="+message.getBenchmarkSeqId()+", metadata_benchmark_seq_id="+metadata.getBenchmarkSeqId());
            return;
        }
       int updatedCount = messageDaoService.updateMessage(message.setReceived(Timestamp.from(Instant.now())));
        logger.info("Message is Received.. Hurray.. "+ message.getUuid()+", updatedCount="+updatedCount);
       if(updatedCount != 1){
           errorDaoService.insertError("Message is received but updatedCount is not 1 - message UID="+message.getUuid()+", updatedCount"+updatedCount);
           logger.error("Message is received but updatedCount is not 1. There is no source message record - message UID= "+message.getUuid());
       }
    }
}

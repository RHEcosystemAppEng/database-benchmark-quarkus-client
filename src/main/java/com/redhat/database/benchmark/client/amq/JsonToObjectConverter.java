package com.redhat.database.benchmark.client.amq;

import io.smallrye.reactive.messaging.MessageConverter;
import io.smallrye.reactive.messaging.amqp.IncomingAmqpMetadata;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.enterprise.context.ApplicationScoped;
import java.lang.reflect.Type;

@ApplicationScoped
public class JsonToObjectConverter implements MessageConverter {

    @Override
    public boolean canConvert(Message<?> in, Type target) {
        return in.getMetadata(IncomingAmqpMetadata.class)
                .map(meta -> meta != null && meta.getContentType() != null && meta.getContentType().equals("application/json") && target instanceof Class)
                .orElse(false);

    }

    @Override
    public Message<?> convert(Message<?> in, Type target) {
        return in.withPayload(((JsonObject) in.getPayload()).mapTo((Class<?>) target));
    }


}

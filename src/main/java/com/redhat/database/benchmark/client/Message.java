package com.redhat.database.benchmark.client;

import java.sql.Timestamp;
import java.util.Objects;

public class Message {

    private String name;
    private String description;
    private String uuid;
    private Timestamp sent;
    private Timestamp received;

    public Message(String uuid, String name, String description) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Message)) {
            return false;
        }

        Message other = (Message) obj;

        return Objects.equals(other.name, this.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Timestamp getSent() {
        return sent;
    }

    public Message setSent(Timestamp sent) {
        this.sent = sent;
        return this;
    }



    public Timestamp getReceived() {
        return received;
    }

    public Message setReceived(Timestamp received) {
        this.received = received;
        return this;
    }
}
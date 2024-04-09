package com.company.chatapp.domain;

import java.util.UUID;

public class MessageTestSamples {

    public static Message getMessageSample1() {
        Message message = new Message();
        message.setId("id1");
        message.setSender("sender1");
        message.setRecipient("recipient1");
        message.setContent("content1");
        return message;
    }

    public static Message getMessageSample2() {
        Message message = new Message();
        message.setId("id2");
        message.setSender("sender2");
        message.setRecipient("recipient2");
        message.setContent("content2");
        return message;
    }

    public static Message getMessageRandomSampleGenerator() {
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setSender(UUID.randomUUID().toString());
        message.setRecipient(UUID.randomUUID().toString());
        message.setContent(UUID.randomUUID().toString());
        return message;
    }
}

package com.company.chatapp.domain;

import java.util.UUID;

public class MessageTestSamples {

    public static Message getMessageSample1() {
        return new Message().id("id1").sender("sender1").recipient("recipient1").content("content1");
    }

    public static Message getMessageSample2() {
        return new Message().id("id2").sender("sender2").recipient("recipient2").content("content2");
    }

    public static Message getMessageRandomSampleGenerator() {
        return new Message()
            .id(UUID.randomUUID().toString())
            .sender(UUID.randomUUID().toString())
            .recipient(UUID.randomUUID().toString())
            .content(UUID.randomUUID().toString());
    }
}

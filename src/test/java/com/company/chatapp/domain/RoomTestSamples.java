package com.company.chatapp.domain;

import java.util.UUID;

public class RoomTestSamples {

    public static Room getRoomSample1() {
        return new Room().id("id1").user1("user11").user2("user21");
    }

    public static Room getRoomSample2() {
        return new Room().id("id2").user1("user12").user2("user22");
    }

    public static Room getRoomRandomSampleGenerator() {
        return new Room().id(UUID.randomUUID().toString()).user1(UUID.randomUUID().toString()).user2(UUID.randomUUID().toString());
    }
}
